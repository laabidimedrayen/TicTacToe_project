# Firebase Setup Instructions

To enable the online multiplayer feature, you need to set up Firebase Realtime Database.

## Steps:

1. **Create a Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click "Add project" and follow the setup wizard
   - Register your Android app with package name: `com.example.tictactoe`

2. **Download google-services.json**
   - After registering your app, download the `google-services.json` file
   - Place it in the `app/` directory (same level as `build.gradle.kts`)

3. **Enable Realtime Database**
   - In Firebase Console, go to "Realtime Database"
   - Click "Create Database"
   - Choose your preferred location
   - Start in **test mode** for development (you can secure it later)

4. **Set Database Rules** (for test mode, you can use these rules temporarily):
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

5. **Build and Run**
   - Sync your Gradle files
   - Build and run the app
   - The online multiplayer feature should now work!

## Security Note:
For production, update your database rules to be more secure. The test mode rules allow anyone to read/write, which is fine for development but not for production apps.

