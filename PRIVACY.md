# Privacy

ApKilla runs locally on your Android device. It does not request the Internet permission and contains no advertising SDK, analytics or telemetry.

## Accessibility access

ApKilla observes foreground window events to identify the app to stop. When you tap its Quick Settings tile, it reads the Android Settings accessibility tree to locate the Force stop and confirmation controls. Android grants this capability through its Accessibility permission, which you can disable in system settings at any time.

## Data kept on your phone

- The last observed foreground app package identifier and whether it remains a valid target.
- A temporary pending force-stop target and request timestamp, cleared after the operation or on timeout.
- The Quick Settings tile's setup state.
- A bounded local history of up to 20 operation outcomes and timestamps, without app names or screen contents.

When you explicitly tap **Copy diagnostics**, the app copies device/version metadata and operation outcomes to the system clipboard; you decide whether to share it.

Data is stored using Android private app preferences. App backup is disabled. ApKilla does not transmit these records. Uninstalling the app removes its local preferences according to Android's app-data handling.

## Contact

For project questions, open a GitHub Issue. Do not attach screenshots containing personal information without redacting them.
