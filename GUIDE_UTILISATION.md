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

```bash
# Windows
compile.bat

# Ou manuellement
javac -d bin -sourcepath src src/brilaunch/*.java src/exemple/*.java
```

## Utilisation

### 1. Démarrer le serveur

```bash
run-server.bat
```

Le serveur démarre sur :
- Port 8888 : Programmeurs
- Port 8889 : Amateurs

### 2. Créer un compte programmeur

Lancer `run-client-prog.bat` et :
1. Choisir option **2** (Créer un nouveau compte)
2. Entrer un login (ex: `exemple`)
3. Entrer un password
4. Entrer une adresse FTP (ex: `ftp://exemple.com`)

### 3. Installer un service

Dans le client programmeur :
1. Choisir option **1** (Fournir un nouveau service)
2. Entrer le nom complet de la classe avec package
   - Exemple : `exemple.InversionService`
   - Le package doit correspondre au login du programmeur

**Important** : Les classes de service doivent être compilées et dans le classpath du serveur.

### 4. Utiliser un service (amateur)

Lancer `run-client-ama.bat` :
1. Choisir un service dans la liste (numéro)
2. Entrer les données d'entrée
3. Recevoir le résultat
4. Répéter pour utiliser d'autres services
5. Choisir **0** pour quitter

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
- **Package** : `exemple`
- **Nom** : "Inversion de texte"
- **Fonction** : Inverse une chaîne de caractères
- **Exemple** : "non ? si !" → "! is ? non"

### ComptageMotsService
- **Package** : `exemple`
- **Nom** : "Comptage de mots"
- **Fonction** : Compte le nombre de mots dans un texte

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

1. **Pas de téléchargement FTP** : Les services doivent être dans le classpath local
2. **Pas de persistance** : Données perdues au redémarrage
3. **Pas de gestion .jar** : Seules les classes individuelles sont supportées
4. **Pas de ressources partagées** : Chaque service est indépendant
5. **Pas d'authentification amateur** : Tous les amateurs sont anonymes

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
