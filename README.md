# ApKilla

A tiny Android Quick Settings tile that **force-stops the app you were just using** without root, ADB, Shizuku or Tasker.

## How it works

Android does not let a normal third-party app directly call `force-stop` on another package. ApKilla uses an Accessibility service instead:

1. It remembers the foreground app.
2. You pull down Quick Settings and tap **Kill app**.
3. ApKilla opens that package's system **App info** screen.
4. Accessibility clicks **Force stop** and the confirmation button automatically.
5. It returns from Settings.

The Settings screen can flash briefly. From the user's side it is still one tap.

## Setup

1. Install the APK and open **ApKilla** once.
2. Tap **Enable Accessibility service** and enable **ApKilla automation**.
3. Tap **Add Quick Settings tile** (or add **Kill app** manually from Quick Settings edit mode).
4. Open any app, pull down Quick Settings, tap **Kill app**.

The Accessibility permission normally remains enabled after a phone reboot, so there is no wireless-debugging pairing or Shizuku startup ritual.

## Safety / behavior

- No root.
- No ADB / wireless debugging.
- No Shizuku.
- No Internet permission and no network code.
- Does not try to kill System UI, Settings, the launcher, or ApKilla itself.
- If you are on the home screen there is deliberately no kill target, so an older app is not killed by accident.
- A pending kill request expires after 10 seconds.

## OEM compatibility

The primary selector looks for Settings view IDs containing `force_stop`, which is more stable than matching visible text. Text fallbacks are included for English, Ukrainian, Russian, Lithuanian and Polish. The positive confirmation button is primarily detected by the standard `android:id/button1` ID.

Android skins can still rename/restructure the App info screen. If a specific Realme/ColorOS version does not match, capture the Settings UI hierarchy and add its button ID/text to `KillAccessibilityService`.

## Build

Requires Android SDK 35, JDK 17 and Gradle 8.9.

```bash
gradle :app:assembleDebug
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
