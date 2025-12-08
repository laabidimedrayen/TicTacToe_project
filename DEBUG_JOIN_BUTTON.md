# Debug: Bouton Rejoindre ne fonctionne pas

## Problème
Quand vous cliquez sur "Rejoindre", rien ne se passe.

## Solutions à vérifier

### 1. Vérifier les logs dans Logcat

**Dans Android Studio:**
1. Ouvrez **Logcat** (panneau en bas)
2. Filtrez par: `OnlineGameActivity` ou `OnlineGameService`
3. Cliquez sur "Rejoindre"
4. Regardez les messages dans Logcat

**Messages attendus:**
```
D/OnlineGameActivity: Join game button clicked
D/OnlineGameActivity: Game ID from EditText: 'ABC12345'
D/OnlineGameActivity: Calling joinGame()
D/OnlineGameActivity: joinGame() called with ID: 'ABC12345'
D/OnlineGameService: Attempting to join game: ABC12345
```

**Si vous ne voyez RIEN:**
- Le listener du bouton ne fonctionne pas
- Vérifiez que le bouton est bien cliquable
- Vérifiez que l'app n'est pas en pause

**Si vous voyez des erreurs:**
- Notez le message d'erreur exact
- Vérifiez les solutions ci-dessous

### 2. Vérifier que Firebase est initialisé

**Dans Logcat, cherchez:**
```
D/OnlineGameService: Firebase database initialized successfully
```

**Si vous ne voyez pas ce message:**
- Firebase n'est pas initialisé
- Vérifiez `google-services.json` dans le dossier `app/`
- Rebuild le projet: `./gradlew clean build`

### 3. Vérifier l'ID de partie

**L'ID doit:**
- Être exactement 8 caractères
- Être en majuscules (converti automatiquement)
- Exister dans Firebase

**Test:**
1. Créez une partie sur un appareil
2. Notez l'ID (ex: ABC12345)
3. Sur l'autre appareil, entrez exactement cet ID
4. Vérifiez dans Logcat si l'ID est bien lu

### 4. Vérifier Firebase Console

1. Allez sur [Firebase Console](https://console.firebase.google.com/)
2. Sélectionnez votre projet: `xogame-8a5e1`
3. Allez dans **Realtime Database**
4. Vous devriez voir un nœud `games`
5. Quand quelqu'un crée une partie, vous devriez voir l'ID apparaître

**Si le nœud `games` n'existe pas:**
- La base de données n'est pas créée
- Créez-la dans Firebase Console
- Mode test pour le développement

### 5. Vérifier les permissions Internet

**Dans AndroidManifest.xml, vous devez avoir:**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 6. Test rapide

**Testez dans cet ordre:**

1. **Ouvrez l'app sur les deux appareils**
2. **Sur appareil 1:**
   - Cliquez "🌐 Jouer en ligne"
   - Cliquez "Créer une partie"
   - Notez l'ID affiché (ex: ABC12345)
   - Vérifiez Logcat pour voir si la partie est créée

3. **Sur appareil 2:**
   - Cliquez "🌐 Jouer en ligne"
   - Entrez l'ID exact (ABC12345)
   - Cliquez "Rejoindre"
   - **Regardez Logcat immédiatement**

4. **Dans Logcat, vous devriez voir:**
   ```
   D/OnlineGameActivity: Join game button clicked
   D/OnlineGameActivity: Calling joinGame()
   D/OnlineGameService: Attempting to join game: ABC12345
   D/OnlineGameService: onDataChange called for game: ABC12345
   ```

### 7. Erreurs communes

**Erreur: "Partie non trouvée"**
- L'ID n'existe pas dans Firebase
- Vérifiez Firebase Console → Realtime Database → games
- Créez une nouvelle partie

**Erreur: "Service non initialisé"**
- Firebase n'est pas initialisé
- Vérifiez `google-services.json`
- Rebuild le projet

**Erreur: "Permission denied"**
- Les règles Firebase bloquent l'accès
- Allez dans Firebase Console → Rules
- Mettez: `{ "rules": { ".read": true, ".write": true } }`
- Cliquez "Publish"

**Rien ne se passe (pas d'erreur)**
- Vérifiez Logcat pour voir si le bouton est cliqué
- Vérifiez que l'EditText contient bien l'ID
- Vérifiez que gameService n'est pas null

### 8. Commandes de debug

```bash
# Voir les logs en temps réel
adb logcat | grep "OnlineGameActivity\|OnlineGameService"

# Voir toutes les erreurs
adb logcat | grep "ERROR\|Exception"

# Nettoyer et rebuild
./gradlew clean
./gradlew build
./gradlew installDebug
```

## Checklist de debug

- [ ] Logcat montre "Join game button clicked" quand vous cliquez
- [ ] Logcat montre "Game ID from EditText: ..." avec l'ID correct
- [ ] Logcat montre "Firebase database initialized successfully"
- [ ] Firebase Console montre la partie créée dans `games`
- [ ] L'ID de partie est exactement 8 caractères
- [ ] Les deux appareils ont internet
- [ ] Les règles Firebase permettent read/write

## Si rien ne fonctionne

1. **Partagez les logs Logcat:**
   - Filtrez par `OnlineGameActivity` ou `OnlineGameService`
   - Copiez tous les messages (surtout les erreurs en rouge)
   - Partagez-les

2. **Vérifiez Firebase Console:**
   - Est-ce que la base de données existe?
   - Est-ce que vous voyez des données quand vous créez une partie?
   - Quelles sont les règles actuelles?

3. **Testez la connexion Firebase:**
   - L'app teste automatiquement Firebase au démarrage
   - Regardez Logcat pour voir si les tests passent

