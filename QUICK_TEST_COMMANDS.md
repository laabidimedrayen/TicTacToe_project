# Quick Testing Commands

## Install on Physical Device (USB)

```bash
# Make sure device is connected and USB debugging is enabled
adb devices  # Verify device is listed

# Install app
./gradlew installDebug

# Or use Android Studio: Click Run button with device selected
```

## Install on Emulator

```bash
# Start emulator first (or use Android Studio Device Manager)
# Then install
./gradlew installDebug

# Or use Android Studio: Click Run button with emulator selected
```

## Install on Both Devices

```bash
# Install on all connected devices
./gradlew installDebug

# Or install on specific device
adb -s <device-id> install -r app/build/outputs/apk/debug/app-debug.apk
```

## View Logs

```bash
# View all logs
adb logcat

# Filter by package name
adb logcat | grep "com.example.tictactoe"

# Filter by Firebase
adb logcat | grep "Firebase"

# View logs from specific device
adb -s <device-id> logcat
```

## Check Connected Devices

```bash
# List all connected devices
adb devices

# Output example:
# List of devices attached
# emulator-5554    device
# ABC123XYZ       device
```

## Clear App Data (for fresh start)

```bash
# Clear app data on device
adb shell pm clear com.example.tictactoe

# Or manually: Settings → Apps → TicTacToe → Clear Data
```

## Rebuild and Install

```bash
# Clean build
./gradlew clean

# Build and install
./gradlew installDebug

# Or in Android Studio: Build → Rebuild Project, then Run
```

## Check Firebase Connection

```bash
# Monitor network traffic (requires root or emulator)
adb shell tcpdump -i any -s 0 -w /sdcard/firebase.pcap

# Or check in Firebase Console → Realtime Database
# You should see game data appearing in real-time
```

## Useful ADB Commands

```bash
# Take screenshot
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png

# Record screen
adb shell screenrecord /sdcard/record.mp4
# Press Ctrl+C to stop
adb pull /sdcard/record.mp4

# View app info
adb shell dumpsys package com.example.tictactoe

# Force stop app
adb shell am force-stop com.example.tictactoe

# Start app
adb shell am start -n com.example.tictactoe/.MainActivity
```

## Testing Workflow

1. **Connect both devices:**
   ```bash
   adb devices  # Verify both are listed
   ```

2. **Install on both:**
   ```bash
   ./gradlew installDebug  # Installs on all connected devices
   ```

3. **Monitor logs (in separate terminal):**
   ```bash
   adb logcat | grep "com.example.tictactoe"
   ```

4. **Test the game:**
   - Create game on device 1
   - Join on device 2
   - Watch logs for any errors

5. **Check Firebase Console:**
   - Open Firebase Console → Realtime Database
   - Watch game data update in real-time

