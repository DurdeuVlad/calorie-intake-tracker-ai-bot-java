# Security policy

## Supported versions

Only the current `master` branch is supported while the project is pre-1.0.

## Reporting a vulnerability

Do not open a public issue for credentials, private-data exposure, authentication bypass, webhook validation flaws, or data-isolation defects. Use [GitHub private vulnerability reporting](https://github.com/DurdeuVlad/calorie-intake-tracker-ai-bot-java/security/advisories/new) when enabled, or contact the repository owner through the private contact method configured in the repository settings. Include reproduction steps and impact, but never include live tokens or personal food-journal data.

We will acknowledge a report within 7 days, assess it privately, and coordinate a fix before disclosure.

## Operational baseline

- Rotate a token immediately if it is exposed.
- Store runtime secrets only in Coolify or GitHub Actions secrets.
- Configure a Telegram webhook secret and an explicit Telegram user allowlist.
- Restrict database access to the application network and back up encrypted volumes.
- Do not retain original user media.
