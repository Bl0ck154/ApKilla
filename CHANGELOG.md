# Changelog

## Unreleased (candidate v0.4.1)

- Verify the target app identity on the App info screen before pressing Force stop.
- Recognize disabled Force stop controls without pointless retries.
- Invalidate the remembered foreground target on Accessibility interruption.
- Add app identity regression tests.
- Add a checksum-verified Gradle 8.9 Wrapper and pin CI actions to immutable commits. Not yet released.


## v0.4.0 — 2026-11-02

- Refuse stale targets after service disconnect or process restart.
- Require a bound Accessibility service before starting a force-stop request.
- Avoid concurrent operations and expire old requests.
- Restrict automated taps to Settings/system confirmation windows, and ignore keyboard-target events.
- Record the last outcome locally, with an option to copy redacted diagnostic results.
- Add unit tests, Android checks and safer tag-only release publishing.
- Add translated Force stop button selectors, documentation and a public GitHub Pages website.

## v0.3.5 — 2026-08-19

- Removed the unreliable launcher-based "Kill last" shortcut.
- Kept the one-tap Quick Settings workflow.
