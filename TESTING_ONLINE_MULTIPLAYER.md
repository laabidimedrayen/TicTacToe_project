# Testing Online Multiplayer Guide

This guide will help you test the online multiplayer feature between a physical phone (USB) and an Android Studio emulator.

## Prerequisites

✅ Firebase is already set up (google-services.json exists)
✅ Both devices need internet connection
✅ Both devices need to be able to access Firebase

## Step-by-Step Testing Instructions

### Step 1: Verify Firebase Realtime Database is Enabled

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: `xogame-8a5e1`
3. Navigate to **Realtime Database** in the left menu
4. If not created yet:
   - Click **Create Database**
   - Choose a location (e.g., `us-central1`)
   - Start in **Test mode** (for development)
5. Verify database rules allow read/write:
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

### Step 2: Build and Install on Physical Phone (USB)

1. **Connect your phone via USB**
   - Enable USB debugging on your phone
   - Allow USB debugging when prompted

2. **In Android Studio:**
   - Click on the device selector (top toolbar)
   - Select your physical device from the list
   - If not visible, check USB connection and enable USB debugging

3. **Build and Run:**
   ```
   Click the "Run" button (green play icon) or press Shift+F10
   ```
   - Or use terminal: `./gradlew installDebug`
   - Wait for the app to install and launch on your phone

4. **Verify installation:**
   - The app should open on your phone
   - Make sure your phone has internet connection (WiFi or mobile data)

### Step 3: Build and Install on Emulator

1. **Start an Android Emulator:**
   - In Android Studio, click **Device Manager** (or Tools → Device Manager)
   - Click the **Play** button next to an emulator
   - Wait for it to boot up

2. **Select the emulator as target:**
   - Click on the device selector (top toolbar)
   - Select your emulator from the list

3. **Build and Run:**
   ```
   Click the "Run" button again or press Shift+F10
   ```
   - Or use terminal: `./gradlew installDebug`
   - Wait for the app to install and launch on the emulator

4. **Verify emulator has internet:**
   - Open browser on emulator and check if internet works
   - If not, check emulator network settings

### Step 4: Test Online Multiplayer

#### Test Scenario 1: Create Game on Phone, Join on Emulator

**On Physical Phone:**
1. Open the TicTacToe app
2. Tap **"🌐 Jouer en ligne"** button
3. Select your symbol (X or O)
4. Select number of games (1, 5, 10, or 15)
5. Tap **"Créer une partie"**
6. **IMPORTANT:** Note the Game ID displayed (e.g., "Game ID: ABC12345")
7. Wait for "En attente d'un autre joueur..." message

**On Emulator:**
1. Open the TicTacToe app
2. Tap **"🌐 Jouer en ligne"** button
3. Select your symbol (different from phone - if phone chose X, choose O)
4. In the "Entrez l'ID de la partie" field, enter the Game ID from phone
5. Tap **"Rejoindre"**
6. You should see "Connexion à la partie..." message
7. Both devices should enter the game screen

**Test the game:**
- Make moves on both devices
- Verify moves appear on both screens in real-time
- Play until someone wins or it's a draw
- Check if scores update correctly

#### Test Scenario 2: Create Game on Emulator, Join on Phone

**On Emulator:**
1. Create a new game (follow steps above)
2. Note the Game ID

**On Physical Phone:**
1. Join using the Game ID from emulator
2. Test gameplay

#### Test Scenario 3: Tournament Mode (Multiple Games)

1. **On Phone:** Create a game with 5 games selected
2. **On Emulator:** Join with the Game ID
3. Play through multiple games
4. Verify scores accumulate correctly
5. Check if tournament winner is displayed correctly

### Step 5: Monitor Firebase Database

While testing, you can monitor the Firebase database in real-time:

1. Go to Firebase Console → Realtime Database
2. You should see a `games` node
3. When a game is created, you'll see a new entry with the game ID
4. Watch the game state change as players make moves:
   - `status`: "waiting" → "ready" → "playing" → "finished"
   - `board`: Updates with moves (e.g., "0_0": "X")
   - `currentTurn`: Alternates between "X" and "O"
   - `winner`: Set when game ends

### Step 6: Troubleshooting

#### Problem: Join button does nothing
**Solutions:**
- Check internet connection on both devices
- Verify Firebase database is created and rules allow read/write
- Check Android Studio Logcat for error messages
- Make sure Game ID is entered correctly (case-sensitive, 8 characters)

#### Problem: "Game not found" error
**Solutions:**
- Verify the Game ID is correct (check for typos)
- Make sure the game was created recently (old games may be deleted)
- Check Firebase console to see if game exists

#### Problem: Moves not syncing
**Solutions:**
- Check internet connection on both devices
- Verify Firebase database is accessible
- Check Logcat for Firebase errors
- Make sure both apps are using the same Firebase project

#### Problem: App crashes when joining
**Solutions:**
- Check Logcat for crash logs
- Verify google-services.json is in the correct location (app/ folder)
- Rebuild the project: `./gradlew clean build`
- Check if Firebase dependencies are properly synced

#### Problem: Emulator has no internet
**Solutions:**
- Check emulator network settings
- Try restarting the emulator
- Verify your computer has internet connection
- Check Android Studio emulator network configuration

### Step 7: View Logs for Debugging

**In Android Studio:**
1. Open **Logcat** (bottom panel)
2. Filter by your app package: `com.example.tictactoe`
3. Look for:
   - Firebase connection messages
   - Error messages
   - Game state changes

**Common log messages to look for:**
- `FirebaseApp initialization successful`
- `Database connection established`
- `Game created with ID: ...`
- `Error: ...` (if something goes wrong)

### Step 8: Test Different Scenarios

1. **Single Game (1 game):** Quick test
2. **Tournament (5 games):** Test score tracking
3. **Tournament (15 games):** Test longer sessions
4. **Network interruption:** Disconnect one device, reconnect
5. **Multiple games:** Create several games simultaneously

## Quick Test Checklist

- [ ] Firebase Realtime Database is created and accessible
- [ ] App installed on physical phone via USB
- [ ] App installed on emulator
- [ ] Both devices have internet connection
- [ ] Can create game on one device
- [ ] Can join game on other device using Game ID
- [ ] Moves sync in real-time between devices
- [ ] Game ends correctly (win/draw)
- [ ] Tournament mode works (multiple games)
- [ ] Scores track correctly across games

## Expected Behavior

✅ **Creating a game:**
- Game ID appears immediately
- Status shows "waiting"
- Toast message: "En attente d'un autre joueur..."

✅ **Joining a game:**
- Toast message: "Connexion à la partie..."
- Game screen opens when connection succeeds
- If error, shows error message

✅ **During gameplay:**
- Moves appear on both devices instantly
- Turn indicator works correctly
- Can't make moves when it's not your turn

✅ **Game end:**
- Winner is determined correctly
- For tournaments, scores accumulate
- Final tournament result shows correctly

## Success Indicators

🎉 **You've successfully tested if:**
- Both devices can create and join games
- Moves sync in real-time
- Games complete successfully
- Tournament mode tracks scores correctly
- No crashes or connection errors

## Next Steps

Once testing is successful:
- Test with two physical devices
- Test with different network conditions
- Consider adding authentication for production
- Update Firebase security rules for production use

