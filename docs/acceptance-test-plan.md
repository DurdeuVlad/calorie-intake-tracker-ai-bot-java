# Release Acceptance & Verification Plan

This document defines the automated CI release gate and manual pre-production verification matrix required for public release versions.

---

## 1. Automated Verification Gate (CI Pipeline)

The `master` build must pass all automated verification checks prior to release:

- **Maven Test Suite**: Unit tests, PostgreSQL / Flyway integration tests (`V1`–`V18`), and Webhook-to-Outbox end-to-end integration tests pass cleanly (`./mvnw clean verify`).
- **Container Build Safety**: Multi-stage Docker build completes without warnings (`docker build .`).
- **Secret & Governance Scan**: Zero committed `.env` files, API tokens, Telegram secrets, raw media, or live database dumps.
- **SSRF Validation Test**: `BrowserlessClient` unit tests prove internal IP blocking (`127.0.0.1`, `10.0.0.0/8`, `192.168.0.0/16`, cloud metadata IPs).
- **Idempotency Assertions**: Duplicate webhook `update_id` submissions produce exactly one database entry and one outbox reply.
- **Undo ChangeSet Expiration**: Reversible undo change sets expire after 10 minutes and prevent stale state rollbacks.

---

## 2. Pre-Production Acceptance Matrix

Run this matrix prior to updating a live production instance:

| Scenario | Input Type | Pass Criteria | Status |
| --- | --- | --- | --- |
| **Onboarding (`/start`)** | Text Command | User timezone and daily calorie goal are saved in `user_settings`. | PASS |
| **Natural Language Text Log** | Text Message | Meal extracted, logged in `food_entries`/`food_items`, confirmation reply sent, `/today` updated. | PASS |
| **Voice Note Logging** | Audio Note | Transcribed via OpenAI Audio, extracted into structured meal items, audio discarded immediately. | PASS |
| **Photo / Document Logging** | Image / Document | Extracted via OpenAI Vision, nutrition verified, raw bytes discarded from memory/tmpfs. | PASS |
| **Search & History (`/today`)** | Text Command | Returns user-scoped food items for current local day; blocks unauthorized user reads. | PASS |
| **Reversible Undo (`/undo`)** | Text / Command | Executing `/undo` within 10 minutes restores state and updates pinned daily summary. | PASS |
| **Nutrition Provenance** | Tool Call | Items show `OFFICIAL_SOURCE` (Open Food Facts), `PRIVATE_RECORD`, `MANUAL`, or `AI_ESTIMATE`. | PASS |
| **Idempotent Webhook Retry** | HTTP POST | Replaying identical Telegram update ID yields HTTP 200 and zero duplicate entries. | PASS |
| **Scheduled Report Delivery** | Scheduled Job | Morning (08:00) and Evening (22:00) reports deliver once per local calendar day across restarts. | PASS |
| **Mattermost Frontend Link** | `/link` Code | Sending link code in Mattermost DM connects account to Telegram identity within 10 minutes. | PASS |

---

## 3. Evidence and release decision

The matrix above is a release gate, not a permanent claim that every deployment
has been exercised. Record the actual CI run, commit, image digest, database
version, and manual test evidence in the release notes before promotion. A release
is ready only when the automated workflow is green and the operator has completed
the applicable Telegram, database, webhook, scheduler, and Mattermost checks.

The repository does not claim that production acceptance has occurred merely
because the source tree or documentation is complete. Environment-dependent checks
must be repeated for each deployment.
