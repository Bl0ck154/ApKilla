# ApKilla release signing

ApKilla release builds use one permanent Android signing certificate.

- Key alias: `apkilla`
- Algorithm: RSA 4096
- Certificate SHA-256: `9D:CC:58:EF:2D:A7:58:65:9A:A2:98:61:D6:D7:BA:AC:C1:7C:FC:DD:97:0A:07:B5:FC:74:55:E1:FE:AC:1C:FA`
- Certificate validity: 2026-08-18 through 2054-01-03

The private keystore must never be committed to this public repository.

## GitHub Actions secrets

The manual `Build APK` workflow expects these repository secrets:

- `APKILLA_KEYSTORE_B64` — base64-encoded contents of the permanent JKS file.
- `APKILLA_KEYSTORE_PASSWORD` — password for both the keystore and the `apkilla` key entry.

After the secrets are configured, run **Actions → Build APK → Run workflow**. The workflow builds `assembleRelease`, verifies the APK signature with `apksigner`, uploads an Actions artifact, and publishes the APK to the `v0.3.1` GitHub Release.

Keep an offline backup of the JKS and its password. Losing the key means future APKs cannot update installations signed with this certificate.
