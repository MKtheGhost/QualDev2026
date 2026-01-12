# Plateforme de Services Dynamiques BRi

## Description

Ce projet implémente une plateforme de services dynamiques inspirée d'OSGi, permettant à des programmeurs de fournir des services et à des amateurs de les utiliser.

## Architecture

- **BRiLaunchServer** : Serveur principal qui gère les connexions
  - Port 8888 : Pour les programmeurs
  - Port 8889 : Pour les amateurs

- **ClientProg** : Client pour les programmeurs
- **ClientAma** : Client pour les amateurs

- **BRiService** : Interface que tous les services doivent implémenter

## Compilation

```bash
javac -d bin -sourcepath src src/brilaunch/*.java src/exemple/*.java
```

## Exécution

### 1. Démarrer le serveur

```bash
java -cp bin brilaunch.ServerLauncher
```

### 2. Lancer un client programmeur (dans un autre terminal)

```bash
java -cp bin brilaunch.ClientProg
```

**Comptes de test disponibles :**
- Login: `exemple` / Password: `password123`
- Login: `test` / Password: `test123`

**Actions disponibles :**
1. Fournir un nouveau service
   - Entrer le nom complet de la classe (ex: `exemple.InversionService`)
2. Mettre à jour un service
3. Changer l'adresse FTP

### 3. Lancer un client amateur (dans un autre terminal)

```bash
java -cp bin brilaunch.ClientAma
```

Choisir un service dans la liste et entrer les données d'entrée.

## Services d'exemple

Deux services d'exemple sont fournis dans le package `exemple` :

1. **InversionService** : Inverse un texte
   - Entrée : "non ? si !"
   - Sortie : "! is ? non"

2. **ComptageMotsService** : Compte le nombre de mots dans un texte

## Structure des packages

- `brilaunch` : Code du serveur et des clients
- `exemple` : Services d'exemple (doivent être dans un package correspondant au login du programmeur)

## Notes importantes

- Les services doivent être dans un package correspondant au login du programmeur
- Les services doivent implémenter l'interface `BRiService`
- Pour simplifier, les services sont chargés depuis le classpath local (pas de téléchargement FTP dans cette version simplifiée)
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
   - Entrer: `exemple.InversionService`

4. **Utiliser le service** : Lancer `run-client-ama.bat`
   - Choisir le service "Inversion de texte"
   - Entrer: `non ? si !`
   - Résultat: `! is ? non`

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
│   └── ServerLauncher.java      # Lanceur avec comptes de test
└── exemple/
    ├── InversionService.java    # Service d'exemple : inversion de texte
    └── ComptageMotsService.java # Service d'exemple : comptage de mots
```

## Améliorations possibles

- Implémentation du téléchargement depuis FTP
- Gestion des services .jar
- Gestion des ressources partagées
- Services avec échange de fichiers
- Authentification des amateurs pour certains services
- Persistance des données (programmeurs et services)
- Interface graphique pour les clients
