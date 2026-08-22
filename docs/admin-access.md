# Telegram administrator access

Telegram access has two layers:

1. `ADMIN_TELEGRAM_USER_IDS` bootstraps administrator grants when the application
   starts. Each configured numeric Telegram user ID is both an active administrator
   and an ordinary journal user.
2. Regular grants are stored in PostgreSQL (`telegram_access_grants`) and are
   managed by administrators at runtime. This means routine user changes do not
   require a Coolify redeploy.

## Bootstrap

Set `ADMIN_TELEGRAM_USER_IDS` to one or more comma-separated numeric Telegram user
IDs in the deployment environment. Keep `ALLOWED_TELEGRAM_USER_IDS` empty for a
new installation. It is retained only as a first-rollout migration input; it does
not reactivate a user that an administrator later revoked.

After the application is ready, send `/start` from the bootstrap administrator's
private Telegram chat. The administrator can then manage regular users.

## Runtime commands

Send one of these commands from a private chat with the bot:

```text
/adduser NUMERIC_ID
/removeuser NUMERIC_ID
```

The implementation also accepts `/add-user`, `/remove-user`, `/add`, and `/remove`
with the same numeric argument. Telegram bot suffixes such as `/adduser@botname`
are accepted as well.

Rules:

- Commands in group chats are rejected.
- Only an active administrator can use them.
- A regular user can be granted and later revoked.
- An administrator cannot revoke or demote an administrator, including itself.
- Replies do not echo the target ID.
- User journal data remains isolated by the application's user identity.

When a user is granted access, their first private message creates or initializes
their journal identity. Removing access prevents future processing; it does not
delete that user's historical journal data.

## Safe rollout check

Verify the migration that creates `telegram_access_grants` has completed, then test
the following with a non-production test account:

1. Grant the test account from the bootstrap administrator's private chat.
2. Confirm the test account can use `/start`, log a meal, and read its own report.
3. Confirm the administrator still has ordinary journal access.
4. Revoke the test account and confirm subsequent messages produce no journal side
   effects.

Never put Telegram IDs, bot tokens, private messages, or database exports in GitHub
issues or pull requests.
