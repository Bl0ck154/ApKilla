# Contributing to ApKilla

Bug reports, verified OEM selectors, automated tests, and documentation improvements are welcome.

## Reporting a problem

Include the ApKilla release, phone model, Android version and manufacturer UI version. Explain which application was visible before you pulled down Quick Settings and what happened after tapping the tile. The **Last action** row in ApKilla can help.

Do not publish private messages, screen contents, accessibility hierarchy dumps, tokens, or package usage history. Redact screenshots.

## Pull requests

- Use a separate branch and describe the user-visible behavior being changed.
- Keep the no-network design and narrow Accessibility use case.
- Test a debug build with `gradle :app:testDebugUnitTest :app:assembleDebug`.
- Include device details for any OEM-specific selector change.
- Do not add logging of app names, screen content, or personal data.
- Do not modify the release certificate or embed signing secrets.

For security-sensitive changes, see [SECURITY.md](SECURITY.md).
