package io.github.foodjournal.messaging;

import io.github.foodjournal.config.BotProperties;
import io.github.foodjournal.domain.TelegramAccessGrant;
import io.github.foodjournal.repository.TelegramAccessGrantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Applies bootstrap configuration once per ID, then authorizes Telegram users from PostgreSQL. */
@Service
public class TelegramAccessService {
  private final TelegramAccessGrantRepository grants;
  private final BotProperties properties;

  public TelegramAccessService(TelegramAccessGrantRepository grants, BotProperties properties) {
    this.grants = grants;
    this.properties = properties;
  }

  @Transactional
  public boolean allowed(long telegramUserId) {
    seedBootstrapGrants();
    return grants.findById(telegramUserId).map(TelegramAccessGrant::isActive).orElse(false);
  }

  @Transactional
  public boolean isAdmin(long telegramUserId) {
    seedBootstrapGrants();
    return grants.findById(telegramUserId)
        .map(grant -> grant.isActive() && grant.isAdmin())
        .orElse(false);
  }

  @Transactional
  public void grantUser(long telegramUserId, long actorId) {
    grants.findById(telegramUserId).ifPresentOrElse(
        grant -> grant.grant(actorId),
        () -> grants.save(new TelegramAccessGrant(telegramUserId, false, actorId)));
  }

  @Transactional
  public boolean revokeRegularUser(long telegramUserId) {
    return grants.findById(telegramUserId).map(grant -> {
      if (grant.isAdmin()) return false;
      grant.revoke();
      return true;
    }).orElse(false);
  }

  private void seedBootstrapGrants() {
    for (Long id : properties.adminTelegramUserIds()) {
      if (id == null || id <= 0) continue;
      grants.findById(id).ifPresentOrElse(TelegramAccessGrant::makeAdmin,
          () -> grants.save(new TelegramAccessGrant(id, true, null)));
    }
    // The old allowlist is migration-only: never reactivate a user an admin revoked.
    for (Long id : properties.allowedTelegramUserIds()) {
      if (id == null || id <= 0 || properties.adminTelegramUserIds().contains(id)) continue;
      if (!grants.existsById(id)) grants.save(new TelegramAccessGrant(id, false, null));
    }
  }
}
