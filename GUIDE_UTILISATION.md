# Guide d'utilisation - Plateforme BRi

## Vue d'ensemble

Cette implémentation simplifiée de la plateforme BRi permet :
- Aux **programmeurs** de créer des comptes, installer et mettre à jour des services
- Aux **amateurs** de consulter et utiliser les services disponibles

## Installation et compilation

### Prérequis
- Java JDK installé
- Java dans le PATH (ou utiliser le chemin complet vers javac)

### Compilation

**Windows:**
```bash
compile.bat
```

**Linux / Mac:**
```bash
# Première fois seulement : rendre les scripts exécutables
chmod +x *.sh
# ou utiliser le script helper
./make-executable.sh

# Compiler
./compile.sh
```

**Manuellement (tous systèmes):**
```bash
javac -d bin -sourcepath src src/brilaunch/*.java src/services/*.java
```

## Utilisation

### 1. Démarrer le serveur

**Windows:**
```bash
run-server.bat
```

**Linux / Mac:**
```bash
./run-server.sh
```

**Manuellement:**
```bash
java -cp bin brilaunch.ServerLauncher
```

Le serveur démarre sur :
- Port 8888 : Programmeurs
- Port 8889 : Amateurs

### 2. Créer un compte programmeur

**Windows:** Lancer `run-client-prog.bat`  
**Linux / Mac:** Lancer `./run-client-prog.sh`  
**Manuellement:** `java -cp bin brilaunch.ClientProg`

Puis :
1. Choisir option **2** (Créer un nouveau compte)
2. Entrer un login (ex: `exemple`)
3. Entrer un password
4. Entrer une adresse FTP (ex: `ftp://exemple.com`)

### 3. Installer un service

Dans le client programmeur :
1. Choisir option **1** (Fournir un nouveau service)
2. Entrer le nom complet de la classe avec package
   - Exemple : `services.InversionService`
   - Le package doit correspondre au login du programmeur

**Important** : Les classes de service doivent être compilées et dans le classpath du serveur.

### 4. Utiliser un service (amateur)

**Windows:** Lancer `run-client-ama.bat`  
**Linux / Mac:** Lancer `./run-client-ama.sh`  
**Manuellement:** `java -cp bin brilaunch.ClientAma`

Puis :
1. Choisir un service dans la liste (numéro)
2. Entrer les données d'entrée
3. Recevoir le résultat
4. Répéter pour utiliser d'autres services
5. Choisir **0** pour quitter le client

### 5. Quitter le client amateur

Dans le menu des services, choisir l'option **0** (Quitter). Le client se fermera proprement.

### 6. Arrêter le serveur

Pour arrêter le serveur BRiLaunch :
- **Méthode 1** : Dans le terminal du serveur, appuyer sur **Ctrl+C**
- **Méthode 2** : Dans le terminal du serveur, taper **`quit`** ou **`exit`** puis Entrée

Le serveur fermera toutes les connexions et s'arrêtera proprement.

## Créer un nouveau service

Pour créer un service personnalisé :

1. Créer une classe dans un package correspondant à votre login
   ```java
   package monlogin;
   
   import brilaunch.BRiService;
   
   public class MonService implements BRiService {
       @Override
       public String execute(String input) {
           // Votre logique ici
           return "Résultat";
       }
       
       @Override
       public String getServiceName() {
           return "Nom du service";
       }
   }
   ```

2. Compiler la classe avec le reste du projet
3. Installer le service via le client programmeur

## Services d'exemple fournis

### InversionService
- **Package** : `services`
- **Nom** : "Inversion de texte"
- **Fonction** : Inverse une chaîne de caractères
- **Exemple** : "non ? si !" → "! is ? non"

### AnalyseFichierXMLService
- **Package** : `services`
- **Nom** : "Analyse de fichier XML"
- **Fonction** : Analyse un fichier XML et envoie un rapport par email
- **Format d'entrée** : `ftp://url_du_fichier|email@destinataire.com` ou `file://chemin/local|email@destinataire.com`
- **Exemple** : `file://test.xml|user@example.com`
- **Note** : 
  - Supporte les fichiers locaux (`file://`) et HTTP/HTTPS
  - Le téléchargement FTP est simulé (génère un rapport fictif)
  - L'envoi d'email est simulé (affiché dans la console du serveur)

### MessagerieInterneService
- **Package** : `services`
- **Nom** : "Messagerie interne"
- **Fonction** : Service de messagerie avec ressource partagée
- **Envoi de message** : `ENVOI:pseudo_destinataire:message`
  - Exemple : `ENVOI:alice:Bonjour Alice !`
- **Lecture des messages** : `LECTURE:pseudo_expediteur`
  - Exemple : `LECTURE:alice`
- **Note** : Les messages sont stockés dans une ressource partagée statique (persistants en mémoire entre les appels)

## Architecture technique

### Communication
- Utilisation de sockets TCP/IP
- Protocole texte simple (ligne par ligne)
- Chaque client a son propre thread sur le serveur

### Stockage
- Programmeurs : Map en mémoire (ConcurrentHashMap)
- Services : Map en mémoire (ConcurrentHashMap)
- **Note** : Les données sont perdues au redémarrage du serveur

### Chargement des services
- Chargement dynamique via `Class.forName()`
- Vérification que la classe implémente `BRiService`
- Vérification du package (doit commencer par le login)

## Limitations de cette version simplifiée

1. **Téléchargement FTP simulé** : Le service d'analyse XML simule le téléchargement FTP (génère un rapport fictif)
2. **Envoi d'email simulé** : L'envoi d'email est simulé (affiché dans la console du serveur)
3. **Pas de persistance** : Données perdues au redémarrage (sauf messages en mémoire pendant l'exécution)
4. **Pas de gestion .jar** : Seules les classes individuelles sont supportées
5. **Ressources partagées limitées** : La messagerie utilise une ressource partagée en mémoire (perdue au redémarrage)
6. **Pas d'authentification amateur** : Tous les amateurs sont anonymes

## Comment quitter

### Quitter le client amateur

1. Dans le menu des services, choisir l'option **0** (Quitter)
2. Le client se fermera automatiquement

### Quitter le client programmeur

1. Dans le menu principal, choisir l'option **4** (Quitter)
2. Le client se fermera automatiquement

### Arrêter le serveur

Le serveur peut être arrêté de deux façons :

**Méthode 1 - Ctrl+C :**
- Dans le terminal où le serveur tourne, appuyer sur **Ctrl+C**
- Le serveur fermera toutes les connexions et s'arrêtera

**Méthode 2 - Commande quit :**
- Dans le terminal où le serveur tourne, taper **`quit`** ou **`exit`**
- Appuyer sur Entrée
- Le serveur s'arrêtera proprement

**Note :** Si des clients sont connectés, ils seront déconnectés lorsque le serveur s'arrête.

## Dépannage

### Erreur "Classe non trouvée"
- Vérifier que la classe est compilée
- Vérifier que le package est correct
- Vérifier que la classe est dans le classpath du serveur

### Erreur "Package incorrect"
- Le package doit commencer par le login du programmeur
- Exemple : login `exemple` → package `exemple.xxx`

### Erreur "Authentification échouée"
- Vérifier le login et le password
- Créer un nouveau compte si nécessaire

### Port déjà utilisé
- Changer les ports dans `BRiLaunchServer.java`
- Ou arrêter l'application utilisant le port

## Prochaines étapes possibles

1. Ajouter la persistance (fichier JSON ou base de données)
2. Implémenter le téléchargement FTP
3. Gérer les fichiers .jar
4. Ajouter l'authentification pour les amateurs
5. Créer une interface graphique
6. Ajouter la gestion des ressources partagées
