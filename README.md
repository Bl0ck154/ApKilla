# ApKilla

A tiny Android utility that **force-stops the app you are using from a Quick Settings tile** without root, ADB, Shizuku or Tasker.

## Quick access

ApKilla uses one primary entry point:

- **Quick Settings tile** — while using an app, pull down the notification shade and tap **Kill app**.

The experimental **Kill last** launcher/home-screen shortcut was removed because Android/OEM Accessibility window events are not reliable enough to reconstruct the previously used app after returning Home.

## How it works

Android does not let a normal third-party app directly call `force-stop` on another package. ApKilla uses an Accessibility service instead:

1. It tracks the current foreground app while you are using it.
2. You open Quick Settings and tap **Kill app**.
3. ApKilla opens that package's system **App info** screen.
4. Accessibility clicks **Force stop** and the confirmation button automatically.
5. It returns from Settings.

The Settings screen can flash briefly. From the user's side it is still one action.

## Setup

1. Install the APK and open **ApKilla** once.
2. Enable **ApKilla automation** in Accessibility.
3. Add the **Kill app** Quick Settings tile.

The setup rows disappear once Android confirms the corresponding setup step.

## Reliability

- Accessibility service rebinds no longer erase the remembered foreground target.
- `TYPE_WINDOW_CONTENT_CHANGED` events are used for Settings automation but are not trusted to replace the foreground app target.
- The Quick Settings tile is always treated as an action tile and remains interactable; readiness is checked when the user taps it.
- If the foreground flag is briefly lost because the Accessibility service is being rebound, a recently tracked safe app can still be used.
- Tile presence is remembered from `onTileAdded()`, `onStartListening()` and Android 13+ tile-placement callbacks.
- The app includes a **Repair / add tile again** action for OEMs that remove a custom tile.

## Safety / behavior

- No root.
- No ADB / wireless debugging.
- No Shizuku.
- No Internet permission and no network code.
- Does not try to kill System UI, Settings, the launcher, or ApKilla itself.
- A pending kill request expires after 10 seconds.

## OEM compatibility

The primary selector looks for Settings view IDs containing `force_stop`, which is more stable than matching visible text. Text fallbacks are included for English, Ukrainian, Russian, Lithuanian and Polish. The positive confirmation button is primarily detected by the standard `android:id/button1` ID.

Android skins can still rename or restructure the App info screen. If a specific Realme/ColorOS version does not match, capture the Settings UI hierarchy and add its button ID/text to `KillAccessibilityService`.

## Build

Requires Android SDK 35, JDK 17 and Gradle 8.9.

```bash
gradle :app:assembleRelease
```

Signed releases are built in GitHub Actions with the permanent ApKilla signing certificate.
