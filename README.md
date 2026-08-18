# ApKilla

A tiny Android utility that **force-stops the app you were just using** without root, ADB, Shizuku or Tasker.

## Quick access

ApKilla now supports three one-tap entry points:

- **Quick Settings tile** — pull down the notification shade and tap **Kill app**.
- **Pinned home-screen shortcut** — add **Kill last app** from the redesigned ApKilla screen.
- **App icon shortcut** — long-press the ApKilla launcher icon and tap **Kill last**.

## How it works

Android does not let a normal third-party app directly call `force-stop` on another package. ApKilla uses an Accessibility service instead:

1. It remembers the foreground app.
2. You trigger ApKilla from Quick Settings or a launcher shortcut.
3. ApKilla opens that package's system **App info** screen.
4. Accessibility clicks **Force stop** and the confirmation button automatically.
5. It returns from Settings.

The Settings screen can flash briefly. From the user's side it is still one action.

## Setup

1. Install the APK and open **ApKilla** once.
2. Enable **ApKilla automation** in Accessibility.
3. Add the **Kill app** Quick Settings tile.
4. Optionally pin the **Kill last app** home-screen shortcut.

The redesigned setup rows disappear once Android confirms the corresponding setup step.

## Reliability changes in 0.3.0

- Accessibility service rebinds no longer erase the remembered foreground target.
- Foreground tracking also listens for window-content changes so it recovers faster after OEM/service lifecycle churn.
- The Quick Settings tile is always treated as an action tile and remains interactable; readiness is checked when the user taps it.
- If the foreground flag was briefly lost, a recently tracked safe app can still be used instead of producing a dead tap.
- Tile presence is remembered from `onTileAdded()`, `onStartListening()` and Android 13+ tile-placement callbacks.
- The app includes a **Repair / add tile again** action for OEMs that remove a custom tile.

## Safety / behavior

- No root.
- No ADB / wireless debugging.
- No Shizuku.
- No Internet permission and no network code.
- Does not try to kill System UI, Settings, the launcher, or ApKilla itself.
- A pending kill request expires after 10 seconds.
- Launcher shortcuts only use a recently tracked app, rather than an arbitrarily old package.

## OEM compatibility

The primary selector looks for Settings view IDs containing `force_stop`, which is more stable than matching visible text. Text fallbacks are included for English, Ukrainian, Russian, Lithuanian and Polish. The positive confirmation button is primarily detected by the standard `android:id/button1` ID.

Android skins can still rename/restructure the App info screen. If a specific Realme/ColorOS version does not match, capture the Settings UI hierarchy and add its button ID/text to `KillAccessibilityService`.

## Build

Requires Android SDK 35, JDK 17 and Gradle 8.9.

```bash
gradle :app:assembleDebug
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
