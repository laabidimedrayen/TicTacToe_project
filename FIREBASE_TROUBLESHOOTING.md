# Firebase Troubleshooting Guide

## ✅ Firebase is FREE!

Firebase Realtime Database has a **free tier (Spark Plan)** that includes:
- **1 GB storage** - Plenty for testing
- **100 simultaneous connections** - Perfect for multiplayer
- **10 GB data transfer per month** - More than enough for development

**You will NOT be charged** unless you exceed these limits (very unlikely for testing).

## Common Issues and Solutions

### Issue 1: "Nothing happens" when clicking buttons

**Check these in order:**

1. **Verify Firebase Database exists:**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Select your project: `xogame-8a5e1`
   - Click **Realtime Database** in left menu
   - If you see "Get started" or "Create Database", click it
   - Choose location (e.g., `us-central1`)
   - Start in **Test mode**

2. **Check Database Rules:**
   - In Firebase Console → Realtime Database → Rules tab
   - Should be:
   ```json
   {
     "rules": {
       ".read": true,
       ".write": true
     }
   }
   ```
   - Or more specific:
   ```json
   {
     "rules": {
       "games": {
         ".read": true,
         ".write": true
       }
     }
   }
   ```
   - Click **Publish** after changing rules

3. **Check Logcat for errors:**
   - In Android Studio, open **Logcat** (bottom panel)
   - Filter by: `Firebase` or `OnlineGameService`
   - Look for error messages
   - Common errors:
     - `PERMISSION_DENIED` → Check database rules
     - `Network error` → Check internet connection
     - `Database not found` → Create database in console

### Issue 2: "Game not found" error

**Solutions:**
1. Make sure game was created recently (check Firebase console)
2. Verify Game ID is exactly 8 characters, all uppercase
3. Check if game exists in Firebase Console → Realtime Database → `games` node
4. Old games might be deleted - create a new one

### Issue 3: Firebase not connecting

**Check:**
1. **Internet connection:**
   - Phone: WiFi or mobile data enabled
   - Emulator: Check if browser works (tests internet)

2. **google-services.json:**
   - File should be in `app/` folder (same level as `build.gradle.kts`)
   - Should contain your project ID: `xogame-8a5e1`
   - Rebuild project if you just added it

3. **Firebase initialization:**
   - Check Logcat for: `Firebase instance created`
   - If you see errors, Firebase might not be initialized

### Issue 4: Permission Denied errors

**Solution:**
1. Go to Firebase Console → Realtime Database → Rules
2. Make sure rules allow read/write:
   ```json
   {
     "rules": {
       ".read": true,
       ".write": true
     }
   }
   ```
3. Click **Publish**
4. Wait a few seconds for rules to update

### Issue 5: App crashes on startup

**Check:**
1. **Logcat** for crash logs
2. Verify `google-services.json` is in correct location
3. Rebuild project: `./gradlew clean build`
4. Check if Firebase dependencies are synced

## Debugging Steps

### Step 1: Test Firebase Connection

The app now includes automatic Firebase testing. When you open the online game screen, check Logcat for:
- `Firebase instance created` ✅
- `Firebase write successful` ✅
- `Firebase read successful` ✅

If you see errors, note the error message.

### Step 2: Monitor Firebase Console

1. Open [Firebase Console](https://console.firebase.google.com/)
2. Go to **Realtime Database**
3. You should see a `games` node appear when someone creates a game
4. Watch it update in real-time as players make moves

### Step 3: Check Logcat

In Android Studio:
1. Open **Logcat** (View → Tool Windows → Logcat)
2. Filter by: `OnlineGameService` or `Firebase`
3. Look for:
   - `Creating game with ID: ...` (when creating)
   - `Attempting to join game: ...` (when joining)
   - Error messages (red text)

### Step 4: Verify Database URL

Sometimes Firebase needs the database URL explicitly set. Check if your `google-services.json` has the database URL, or we might need to set it in code.

## Quick Fixes

### Fix 1: Rebuild Everything
```bash
./gradlew clean
./gradlew build
```

### Fix 2: Reinstall App
```bash
adb uninstall com.example.tictactoe
./gradlew installDebug
```

### Fix 3: Clear Firebase Cache
- Uninstall app
- Reinstall app
- This resets Firebase initialization

### Fix 4: Check Firebase Project
- Go to Firebase Console
- Verify project `xogame-8a5e1` exists
- Check if Realtime Database is enabled
- Verify package name matches: `com.example.tictactoe`

## Testing Checklist

- [ ] Firebase Console → Realtime Database exists and is created
- [ ] Database rules allow read/write (test mode)
- [ ] `google-services.json` is in `app/` folder
- [ ] Project rebuilt after adding Firebase
- [ ] Internet connection works on both devices
- [ ] Logcat shows Firebase connection messages
- [ ] Firebase Console shows `games` node when creating game

## Still Not Working?

1. **Share Logcat output:**
   - Filter by `OnlineGameService` or `Firebase`
   - Copy error messages
   - Share them for help

2. **Check Firebase Console:**
   - Does the database exist?
   - Do you see any data when creating a game?
   - What do the rules look like?

3. **Verify setup:**
   - Is `google-services.json` correct?
   - Are Firebase dependencies in `build.gradle.kts`?
   - Is Google Services plugin applied?

## Expected Logcat Output (Success)

When everything works, you should see:
```
D/OnlineGameService: Firebase database initialized
D/OnlineGameService: Database reference: https://xogame-8a5e1-default-rtdb.firebaseio.com/
D/FirebaseDebug: Firebase instance created
D/FirebaseDebug: Firebase write successful - connection works!
D/FirebaseDebug: Firebase read successful - connection works!
D/OnlineGameActivity: createGame() called
D/OnlineGameService: Creating game with ID: ABC12345
D/OnlineGameService: Game created successfully in Firebase
```

If you see errors instead, note them and check the solutions above.

