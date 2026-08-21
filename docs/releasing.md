# Release guide

The Java sister repository uses a conservative pre-1.0 release process. `master` is the integration branch; production deployment remains a deliberate operator action in Coolify.

## Before a release

- Confirm the Java source and Flyway migrations are the intended variant; do not copy Python-only files into this repository.
- Run `./mvnw -B --no-transfer-progress verify` and build the Docker image.
- Run the Compose smoke test with non-production credentials and remove its volumes afterward.
- Review the dependency, container, and filesystem security scan results.
- Update the user-facing documentation and note configuration, migration, rollback, and operational impact in the pull request.
- Create a signed, annotated tag only after review. Until a first stable release exists, use semantic pre-release versions such as `0.1.0-alpha.1`.

## Release evidence

Record the commit, tag, test result, image digest, migration range, and rollback target in the release notes. Never include tokens, user identifiers, private messages, media, or database dumps.

## Rollback

Stop promotion, keep the prior image available, and follow the [operations runbook](operations.md). Do not roll back by deleting or editing an applied Flyway migration. Restore data only through the documented, isolated restore procedure after assessing compatibility.
