# Troubleshooting

## The tile says no foreground app

Open the app you want to stop, wait for its screen to become visible, then pull down Quick Settings and tap **Kill app**. ApKilla refuses to use a stale target after Accessibility reconnects. If you are on Home or Settings, there is deliberately no target.

## Accessibility is enabled but the tile doesn't work

Open ApKilla. Its status distinguishes "On" from "Enabled · reconnecting". If Android disconnected the service, disable and re-enable **ApKilla automation** in Accessibility settings. Some OEMs restrict background services; check your phone's settings without disabling other security protections.

## Force stop does not happen

The system's App info screen and confirmation dialog differ between Android versions and vendors. Check the **Last action** row in ApKilla. Then report a bug with:

- Device manufacturer/model, Android version, and OEM skin version.
- App version and where the process stopped (App info, dialog, return, or no action).
- The exact Last action message and reproducible steps.

Use **Copy diagnostics** in ApKilla to get a local history of operation statuses. It does not include package names or screen contents. Review the copied text before sharing.

Never upload a full accessibility screen dump containing private messages, account data, or other sensitive content.

## The app says "Force stop requested" but nothing changed

That status confirms a UI request, **not** a verified package stop. Protected apps may reject Force stop, and another app can immediately restart a process. This is a known limitation; attach your Android build and steps to a bug report.

## v0.4.1 candidate: target verification

This pending version refuses to press **Force stop** unless the Settings app info header identifies the intended app. Some OEM layouts or Android package-visibility restrictions may prevent the check from succeeding, in which case the action fails safely with **Target could not be verified**. Please report the phone and Android/OEM version. No broad installed-app visibility permission is requested.
