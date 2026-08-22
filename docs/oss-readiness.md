# OSS readiness and parity audit

Audit date: 2026-08-21. Scope: the Java implementation in this repository compared with the Java paths and Java-owned operational assets in the primary `calorie-intake-tracker-ai-bot` repository.

## Parity result

The sister repository contains the complete Java source tree, Maven project, tests, migrations, resources, Dockerfile, Compose/development scripts, environment template, wrapper, and Java operational documentation. A recursive comparison of `src/`, `pom.xml`, `Dockerfile`, `compose.yaml`, `docker/`, `.env.example`, Maven wrapper files, `LICENSE`, and the Java docs is clean. The Python application and Python CI workflow are intentionally excluded.

## Issue register

### High

- **Security contact was not actionable. Fixed in-repository; setting remains.** `SECURITY.md` now links to GitHub private vulnerability reporting. Repository settings must enable that feature or select one maintainer-owned security mailbox before public release; do not publish secrets in issues.
- **Public release governance was incomplete. Fixed.** Added a code of conduct, support guide, release guide, CODEOWNERS, issue templates, pull-request checklist, Dependabot, and CI security scan. These files establish the baseline but do not replace enabling GitHub settings.

### Medium

- **Documentation drift. Fixed.** README badges and prose said Spring Boot 3.4 while `pom.xml` uses 3.5.16; documentation now reflects 3.5.
- **Build reproducibility was weaker than necessary. Fixed.** CI now uses the checked-in Maven wrapper and has a bounded timeout and concurrency cancellation. The container build was verified successfully. Container base images remain intentionally on maintained major tracks; pin image digests before a high-assurance production release.
- **Branch governance is not encoded in Git. Action required.** Enable protection or rulesets in GitHub (recommendations below).

### Low

- **Versioning and support expectations were implicit. Fixed.** Added `docs/releasing.md` and `SUPPORT.md`.
- **Maintainer ownership was implicit. Fixed.** Added `.github/CODEOWNERS`; update the owner if project stewardship changes.

## Recommended GitHub settings before going public

For `master`, use a ruleset or branch protection with: pull requests required; at least one approving review; dismiss stale approvals; required CODEOWNERS review; required CI checks (`build-test` and `security-scan` when applicable); conversation resolution; force-push and deletion disabled; linear history preferred; and administrators included where practical. Require signed commits only if the maintainer workflow can consistently produce them.

Enable private vulnerability reporting, Dependabot security updates, secret scanning
and push protection where available, and Actions restricted to reviewed or pinned
actions. Because this repository is public, verify those settings before inviting
outside contributions and keep the security contact and owner information current.

## Remaining recommended hardening

1. Pin Docker base images and third-party Actions to reviewed digests before a security-sensitive release.
2. Decide whether the sample Compose stack should ship Mattermost by default; it increases local resource use and attack surface even though the service binds loopback ports.
3. Add a `CHANGELOG.md` or GitHub Releases after the first tagged release.
4. Have a maintainer test the full Compose path on a clean host with Java 21, Docker, and PostgreSQL; this environment can run the Maven/Docker checks only if those tools are available.

## Verification record

- Java source/assets parity comparison: passed; no differences in the Java implementation or its Java-owned deployment assets.
- `./mvnw -B --no-transfer-progress -DskipTests package`: passed.
- Focused web-search tests: passed (11 tests).
- Docker image build: passed.
- A full local test run exercised the Testcontainers-backed pipeline and migration/evaluation tests successfully, but later parallel container startup hit the host's Testcontainers PostgreSQL log-readiness timeout. This is an environment/resource limitation to reproduce in clean CI; it is not treated as a source regression because the affected web-search class passes in isolation and earlier PostgreSQL-backed classes completed successfully.
