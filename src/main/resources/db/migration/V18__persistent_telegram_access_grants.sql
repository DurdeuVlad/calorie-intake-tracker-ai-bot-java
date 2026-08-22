CREATE TABLE telegram_access_grants (
  telegram_user_id BIGINT PRIMARY KEY,
  is_admin BOOLEAN NOT NULL DEFAULT FALSE,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  granted_by BIGINT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Preserve every existing Telegram journal owner during the move from an environment allowlist.
INSERT INTO telegram_access_grants (telegram_user_id, is_admin, active, granted_by, created_at, updated_at)
SELECT telegram_user_id, FALSE, TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM food_users
ON CONFLICT (telegram_user_id) DO NOTHING;
