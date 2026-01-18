# Plateforme de Services Dynamiques BRi

## Description

Ce projet implémente une plateforme de services dynamiques inspirée d'OSGi, permettant à des programmeurs de fournir des services et à des amateurs de les utiliser.

## Compatibilité

Ce projet fonctionne sur **Windows**, **Linux** et **macOS**. Des scripts sont fournis pour chaque système d'exploitation :
- **Windows** : fichiers `.bat`
- **Linux / macOS** : fichiers `.sh`

## Architecture

- **BRiLaunchServer** : Serveur principal qui gère les connexions
  - Port 8888 : Pour les programmeurs
  - Port 8889 : Pour les amateurs
  - Télécharge les classes de services depuis le serveur FTP du programmeur
  - Fallback automatique sur classpath local si le téléchargement échoue

- **ClientProg** : Client pour les programmeurs
- **ClientAma** : Client pour les amateurs

- **BRiService** : Interface que tous les services doivent implémenter
- **ServiceLoader** : Utilitaire pour télécharger et charger dynamiquement les classes de services

## Compilation

### Windows
```bash
compile.bat
```

### Linux / Mac
```bash
# Première fois seulement : rendre les scripts exécutables
chmod +x *.sh
# ou utiliser le script helper
chmod +x make-executable.sh
./make-executable.sh

# Compiler
./compile.sh
```

### Manuellement
```bash
javac -d bin -sourcepath src src/brilaunch/*.java src/services/*.java
```

## Exécution

### 1. Démarrer le serveur

**Windows:**
```bash
run-server.bat
```

**Linux / Mac:**
```bash
chmod +x run-server.sh
./run-server.sh
```

**Manuellement:**
```bash
java -cp bin brilaunch.ServerLauncher
```

### 2. Lancer un client programmeur (dans un autre terminal)

**Windows:**
```bash
run-client-prog.bat
```

**Linux / Mac:**
```bash
chmod +x run-client-prog.sh
./run-client-prog.sh
```

**Manuellement:**
```bash
java -cp bin brilaunch.ClientProg
```

**Comptes de test disponibles :**
- Login: `exemple` / Password: `password123`
- Login: `test` / Password: `test123`
- Login: `services` / Password: `services123`

**Actions disponibles :**
1. Fournir un nouveau service
   - Entrer le nom complet de la classe (ex: `services.InversionService`)
   - Le serveur télécharge automatiquement la classe depuis votre serveur FTP
   - Si le téléchargement échoue, utilisation automatique du classpath local
2. Mettre à jour un service
3. Changer l'adresse FTP
4. Quitter (choisir option 4)

**Téléchargement FTP :**
- Le serveur construit l'URL comme suit : `{votre_ftp_url}/{package}/{classe}.class`
- Exemple : Pour `services.InversionService` avec FTP `http://exemple.com`, l'URL sera `http://exemple.com/services/InversionService.class`
- Protocoles supportés : HTTP, HTTPS, file://
- Fallback automatique : Si le téléchargement échoue, le serveur utilise le classpath local

### 3. Lancer un client amateur (dans un autre terminal)

**Windows:**
```bash
run-client-ama.bat
```

**Linux / Mac:**
```bash
chmod +x run-client-ama.sh
./run-client-ama.sh
```

**Manuellement:**
```bash
java -cp bin brilaunch.ClientAma
```

Choisir un service dans la liste et entrer les données d'entrée. Pour quitter, choisir l'option **0** dans le menu.

### 4. Arrêter le serveur

Pour arrêter le serveur proprement :
- Appuyer sur **Ctrl+C** dans le terminal du serveur
- Ou taper **`quit`** ou **`exit`** dans le terminal du serveur

## Services d'exemple

Quatre services d'exemple sont fournis dans le package `services` :

1. **InversionService** : Inverse un texte
   - Entrée : "non ? si !"
   - Sortie : "! is ? non"

2. **AnalyseFichierXMLService** : Analyse un fichier XML et envoie un rapport par email
   - Entrée : `ftp://url_du_fichier|email@destinataire.com` ou `file://chemin/local|email@destinataire.com`
   - Sortie : Confirmation avec rapport d'analyse
   - Note : Le téléchargement FTP et l'envoi d'email sont simulés dans cette version

3. **MessagerieInterneService** : Service de messagerie interne avec ressource partagée
   - Envoi : `ENVOI:pseudo_destinataire:message`
   - Lecture : `LECTURE:pseudo_expediteur`
   - Les messages sont stockés dans une ressource partagée (persistants en mémoire)

## Structure des packages

- `brilaunch` : Code du serveur et des clients
- `services` : Services d'exemple (doivent être dans un package correspondant au login du programmeur)

## Notes importantes

- Les services doivent être dans un package correspondant au login du programmeur
- Les services doivent implémenter l'interface `BRiService`
- Le serveur télécharge automatiquement les classes depuis le serveur FTP du programmeur
- Protocoles supportés pour le téléchargement : HTTP, HTTPS, file://
- Si le téléchargement échoue, le serveur utilise automatiquement le classpath local (fallback)
- Le serveur doit être démarré avant les clients

## Exemple d'utilisation

### Scénario complet

1. **Démarrer le serveur** : `run-server.bat`

2. **Créer un compte programmeur** : Lancer `run-client-prog.bat`
   - Choisir option 2 (Créer un nouveau compte)
   - Login: `exemple`
   - Password: `password123`
   - FTP: `ftp://exemple.com`

3. **Installer un service** : Dans le client programmeur
   - Choisir option 1 (Fournir un nouveau service)
   - Entrer: `services.InversionService`

4. **Utiliser le service** : Lancer `run-client-ama.bat`
   - Choisir le service "Inversion de texte"
   - Entrer: `non ? si !`
   - Résultat: `! is ? non`

### Exemples avec les nouveaux services

**Service d'analyse XML** :
- Installer : `services.AnalyseFichierXMLService` (avec compte `services`)
- Utiliser : `file://test.xml|user@example.com`
- Résultat : Rapport d'analyse affiché et "envoyé" par email (simulé)

**Service de messagerie** :
- Installer : `services.MessagerieInterneService` (avec compte `services`)
- Envoyer un message : `ENVOI:alice:Bonjour !`
- Lire les messages : `LECTURE:alice`

## Structure du code

```
src/
├── brilaunch/
│   ├── BRiService.java          # Interface que tous les services doivent implémenter
│   ├── BRiLaunchServer.java     # Serveur principal
│   ├── ClientProg.java          # Client programmeur
│   ├── ClientAma.java           # Client amateur
│   ├── Programmer.java          # Classe représentant un programmeur
│   ├── ServiceInfo.java         # Informations sur un service installé
│   ├── ServiceLoader.java       # Utilitaire pour télécharger les classes depuis FTP
│   └── ServerLauncher.java      # Lanceur avec comptes de test
└── services/
    ├── InversionService.java           # Service d'exemple : inversion de texte
    ├── AnalyseFichierXMLService.java   # Service avec échange de fichiers : analyse XML
    └── MessagerieInterneService.java   # Service avec ressource partagée : messagerie
```

## Fonctionnalités implémentées

- ✅ Téléchargement des classes de services depuis le serveur FTP du programmeur
- ✅ Support des protocoles HTTP, HTTPS et file://
- ✅ Fallback automatique sur classpath local si le téléchargement échoue
- ✅ Validation des noms de services (non null, non vide)
- ✅ Gestion du changement de nom lors de la mise à jour d'un service
- ✅ Nettoyage automatique des fichiers temporaires au shutdown

## Améliorations possibles

- Support FTP réel complet (nécessiterait Apache Commons Net)
- Implémentation réelle de l'envoi d'email avec JavaMail API (actuellement simulé)
- Gestion des services .jar
- Persistance des messages de la messagerie (fichier ou base de données)
- Authentification des amateurs pour certains services
- Persistance des données (programmeurs et services)
- Interface graphique pour les clients
