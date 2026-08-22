package io.github.foodjournal.messaging;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

/** Private-chat-only access management. Responses intentionally never include a target ID. */
@Service
public class TelegramAdminService {
  private static final Pattern COMMAND = Pattern.compile(
      "^/(add[_-]?user|remove[_-]?user|add|remove)(?:@[^\\s]+)?\\s+([1-9][0-9]*)\\s*$",
      Pattern.CASE_INSENSITIVE);
  private final TelegramAccessService access;

  public TelegramAdminService(TelegramAccessService access) { this.access = access; }

  public Optional<String> handle(String text, long actorId, boolean privateChat) {
    Matcher command = COMMAND.matcher(text == null ? "" : text.trim());
    if (!command.matches()) return Optional.empty();
    if (!privateChat) return Optional.of("Admin commands are available only in a private chat.");
    if (!access.isAdmin(actorId)) return Optional.of("You are not authorized to manage users.");
    long targetId;
    try { targetId = Long.parseLong(command.group(2)); }
    catch (NumberFormatException invalid) { return Optional.of("That user ID is invalid."); }
    if (targetId == actorId) return Optional.of("You cannot change your own administrator access.");
    String action = command.group(1).toLowerCase();
    if (action.startsWith("add")) {
      access.grantUser(targetId, actorId);
      return Optional.of("User access granted.");
    }
    return Optional.of(access.revokeRegularUser(targetId)
        ? "User access removed."
        : "That user is not a removable regular user.");
  }
}
