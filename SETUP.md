# Guide de configuration rapide

## Première utilisation

### Linux / Mac

1. **Rendre les scripts exécutables** (une seule fois) :
   ```bash
   chmod +x *.sh
   ```
   Ou utilisez le script helper :
   ```bash
   chmod +x make-executable.sh
   ./make-executable.sh
   ```

2. **Compiler le projet** :
   ```bash
   ./compile.sh
   ```

3. **Lancer le serveur** :
   ```bash
   ./run-server.sh
   ```

4. **Dans d'autres terminaux, lancer les clients** :
   ```bash
   ./run-client-prog.sh    # Client programmeur
   ./run-client-ama.sh      # Client amateur
   ```

### Windows

1. **Compiler le projet** :
   ```bash
   compile.bat
   ```

2. **Lancer le serveur** :
   ```bash
   run-server.bat
   ```

3. **Dans d'autres terminaux, lancer les clients** :
   ```bash
   run-client-prog.bat    # Client programmeur
   run-client-ama.bat     # Client amateur
   ```

## Scripts disponibles

### Compilation
- **Windows** : `compile.bat`
- **Linux/Mac** : `compile.sh`

### Serveur
- **Windows** : `run-server.bat`
- **Linux/Mac** : `run-server.sh`

### Clients
- **Windows** : `run-client-prog.bat`, `run-client-ama.bat`
- **Linux/Mac** : `run-client-prog.sh`, `run-client-ama.sh`

## Exécution manuelle (tous systèmes)

Si vous préférez exécuter manuellement sans scripts :

```bash
# Compilation
javac -d bin -sourcepath src src/brilaunch/*.java src/services/*.java

# Serveur
java -cp bin brilaunch.ServerLauncher

# Client programmeur
java -cp bin brilaunch.ClientProg

# Client amateur
java -cp bin brilaunch.ClientAma
```
