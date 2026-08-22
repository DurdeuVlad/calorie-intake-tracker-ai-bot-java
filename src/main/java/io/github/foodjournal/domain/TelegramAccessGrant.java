package io.github.foodjournal.domain;

import jakarta.persistence.*;
import java.time.Instant;

/** Persistent Telegram authorization, independent of journal records and chat membership. */
@Entity
@Table(name = "telegram_access_grants")
public class TelegramAccessGrant {
  @Id
  @Column(name = "telegram_user_id")
  private long telegramUserId;

  @Column(name = "is_admin", nullable = false)
  private boolean admin;

  @Column(nullable = false)
  private boolean active;

  @Column(name = "granted_by")
  private Long grantedBy;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected TelegramAccessGrant() {}

  public TelegramAccessGrant(long telegramUserId, boolean admin, Long grantedBy) {
    this.telegramUserId = telegramUserId;
    this.admin = admin;
    this.active = true;
    this.grantedBy = grantedBy;
    this.createdAt = Instant.now();
    this.updatedAt = createdAt;
  }

  public long getTelegramUserId() { return telegramUserId; }
  public boolean isAdmin() { return admin; }
  public boolean isActive() { return active; }

  public void makeAdmin() {
    admin = true;
    active = true;
    grantedBy = null;
    updatedAt = Instant.now();
  }

  public void grant(long actorId) {
    active = true;
    grantedBy = actorId;
    updatedAt = Instant.now();
  }

  public void revoke() {
    active = false;
    updatedAt = Instant.now();
  }
}
