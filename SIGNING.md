# ApKilla release signing

ApKilla uses a permanent Android signing certificate for upgrades.

- Key alias: `apkilla`
- Algorithm: RSA 4096
- Certificate SHA-256: `9D:CC:58:EF:2D:A7:58:65:9A:A2:98:61:D6:D7:BA:AC:C1:7C:FC:DD:97:0A:07:B5:FC:74:55:E1:FE:AC:1C:FA`
- Valid: 2026-08-18 to 2054-01-03

Keep an offline backup of the JKS and password. Losing this key prevents seamless upgrades of existing installations. Never commit the private key.

## GitHub Actions secrets

- `APKILLA_KEYSTORE_B64`: Base64-encoded permanent JKS
- `APKILLA_KEYSTORE_PASSWORD`: password for the JKS and `apkilla` key

Create an annotated or lightweight Git tag matching the `versionName` in `app/build.gradle`, for example `v0.4.0`. The release workflow runs only for version tags, executes tests, builds a signed APK, verifies its certificate, and publishes it in GitHub Releases.

A published APK is never overwritten under the same version. Do not re-tag released versions.
