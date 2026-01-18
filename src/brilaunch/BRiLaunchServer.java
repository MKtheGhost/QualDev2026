package brilaunch;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serveur BRiLaunch qui gère les connexions des programmeurs et des amateurs.
 */
public class BRiLaunchServer {
    private static final int PORT_PROG = 8888;  // Port pour les programmeurs
    private static final int PORT_AMA = 8889;   // Port pour les amateurs
    
    // Stockage des programmeurs (login -> Programmer)
    private Map<String, Programmer> programmers = new ConcurrentHashMap<>();
    
    // Stockage des services (serviceName -> ServiceInfo)
    private Map<String, ServiceInfo> services = new ConcurrentHashMap<>();
    
    // Serveurs sockets pour pouvoir les fermer
    private ServerSocket programmerServerSocket;
    private ServerSocket amateurServerSocket;
    private volatile boolean running = true;
    
    public static void main(String[] args) {
        BRiLaunchServer server = new BRiLaunchServer();
        server.start();
    }
    
    public void start() {
        System.out.println("=== Serveur BRiLaunch démarré ===");
        System.out.println("Port programmeurs: " + PORT_PROG);
        System.out.println("Port amateurs: " + PORT_AMA);
        System.out.println("\nPour arrêter le serveur, appuyez sur Ctrl+C ou tapez 'quit'");
        
        // Ajouter un shutdown hook pour arrêter proprement
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nArrêt du serveur en cours...");
            ServiceLoader.cleanup(); // Nettoyer les fichiers temporaires
            stop();
        }));
        
        // Thread pour écouter les commandes console
        new Thread(() -> listenForQuitCommand()).start();
        
        // Démarrer le serveur pour les programmeurs
        new Thread(() -> startProgrammerServer()).start();
        
        // Démarrer le serveur pour les amateurs
        new Thread(() -> startAmateurServer()).start();
    }
    
    private void listenForQuitCommand() {
        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while (running && (line = console.readLine()) != null) {
                if ("quit".equalsIgnoreCase(line.trim()) || "exit".equalsIgnoreCase(line.trim())) {
                    System.out.println("Arrêt du serveur demandé...");
                    stop();
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            // Ignorer si la console n'est pas disponible
        }
    }
    
    public void stop() {
        running = false;
        try {
            if (programmerServerSocket != null && !programmerServerSocket.isClosed()) {
                programmerServerSocket.close();
                System.out.println("Serveur programmeurs arrêté");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'arrêt du serveur programmeurs: " + e.getMessage());
        }
        try {
            if (amateurServerSocket != null && !amateurServerSocket.isClosed()) {
                amateurServerSocket.close();
                System.out.println("Serveur amateurs arrêté");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'arrêt du serveur amateurs: " + e.getMessage());
        }
    }
    
    private void startProgrammerServer() {
        try {
            programmerServerSocket = new ServerSocket(PORT_PROG);
            System.out.println("Serveur programmeurs en écoute sur le port " + PORT_PROG);
            while (running) {
                try {
                    Socket clientSocket = programmerServerSocket.accept();
                    new Thread(() -> handleProgrammer(clientSocket)).start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Erreur accept connexion programmeur: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Erreur serveur programmeurs: " + e.getMessage());
            }
        }
    }
    
    private void startAmateurServer() {
        try {
            amateurServerSocket = new ServerSocket(PORT_AMA);
            System.out.println("Serveur amateurs en écoute sur le port " + PORT_AMA);
            while (running) {
                try {
                    Socket clientSocket = amateurServerSocket.accept();
                    new Thread(() -> handleAmateur(clientSocket)).start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Erreur accept connexion amateur: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Erreur serveur amateurs: " + e.getMessage());
            }
        }
    }
    
    private void handleProgrammer(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            // Authentification ou création de compte
            out.println("BRiLaunch - Connexion programmeur");
            out.println("1. Se connecter");
            out.println("2. Créer un nouveau compte");
            out.println("Choix:");
            String authChoice = in.readLine();
            
            String login, password, ftpUrl;
            Programmer programmer = null;
            
            if ("2".equals(authChoice)) {
                // Création de compte
                out.println("Login:");
                login = in.readLine();
                if (programmers.containsKey(login)) {
                    out.println("ERREUR: Ce login existe déjà");
                    return;
                }
                out.println("Password:");
                password = in.readLine();
                out.println("Adresse FTP:");
                ftpUrl = in.readLine();
                programmer = new Programmer(login, password, ftpUrl);
                programmers.put(login, programmer);
                out.println("OK: Compte créé avec succès");
            } else {
                // Authentification
                out.println("Login:");
                login = in.readLine();
                out.println("Password:");
                password = in.readLine();
                
                programmer = programmers.get(login);
                if (programmer == null || !programmer.getPassword().equals(password)) {
                    out.println("ERREUR: Authentification échouée");
                    return;
                }
                
                out.println("OK: Authentification réussie");
            }
            
            // Menu principal
            final Programmer finalProgrammer = programmer;
            while (true) {
                out.println("\n=== Menu Programmeur ===");
                out.println("1. Fournir un nouveau service");
                out.println("2. Mettre à jour un service");
                out.println("3. Changer l'adresse FTP");
                out.println("4. Quitter");
                out.println("Choix:");
                
                String choice = in.readLine();
                
                switch (choice) {
                    case "1":
                        provideNewService(in, out, finalProgrammer);
                        break;
                    case "2":
                        updateService(in, out, finalProgrammer);
                        break;
                    case "3":
                        changeFtpUrl(in, out, finalProgrammer);
                        break;
                    case "4":
                        out.println("Au revoir!");
                        return;
                    default:
                        out.println("Choix invalide");
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erreur avec programmeur: " + e.getMessage());
        }
    }
    
    private void provideNewService(BufferedReader in, PrintWriter out, Programmer programmer) throws IOException {
        out.println("Nom de la classe du service (avec package, ex: " + programmer.getLogin() + ".InversionService):");
        String className = in.readLine();
        
        // Vérifier que le package commence par le login du programmeur
        if (!className.startsWith(programmer.getLogin() + ".")) {
            out.println("ERREUR: La classe doit être dans le package " + programmer.getLogin());
            return;
        }
        
        // Télécharger et charger la classe depuis le serveur FTP du programmeur
        try {
            Class<?> serviceClass;
            try {
                // Essayer de télécharger depuis le serveur FTP
                serviceClass = ServiceLoader.loadServiceClass(className, programmer.getFtpUrl());
            } catch (Exception e) {
                // Si le téléchargement échoue, essayer de charger depuis le classpath local
                // (fallback pour compatibilité et tests)
                System.err.println("Avertissement: Téléchargement FTP échoué, chargement depuis classpath local: " + e.getMessage());
                serviceClass = Class.forName(className);
            }
            if (!BRiService.class.isAssignableFrom(serviceClass)) {
                out.println("ERREUR: La classe doit implémenter BRiService");
                return;
            }
            
            BRiService service = (BRiService) serviceClass.getDeclaredConstructor().newInstance();
            String serviceName = service.getServiceName();
            
            if (serviceName == null || serviceName.trim().isEmpty()) {
                out.println("ERREUR: Le nom du service ne peut pas être vide");
                return;
            }
            
            if (services.containsKey(serviceName)) {
                out.println("ERREUR: Un service avec ce nom existe déjà");
                return;
            }
            
            ServiceInfo serviceInfo = new ServiceInfo(serviceName, programmer.getLogin(), className, service);
            services.put(serviceName, serviceInfo);
            out.println("OK: Service '" + serviceName + "' installé avec succès");
            
        } catch (Exception e) {
            out.println("ERREUR: Impossible de charger le service: " + e.getMessage());
        }
    }
    
    private void updateService(BufferedReader in, PrintWriter out, Programmer programmer) throws IOException {
        out.println("Nom du service à mettre à jour:");
        String serviceName = in.readLine();
        
        ServiceInfo serviceInfo = services.get(serviceName);
        if (serviceInfo == null) {
            out.println("ERREUR: Service non trouvé");
            return;
        }
        
        if (!serviceInfo.getProgrammerLogin().equals(programmer.getLogin())) {
            out.println("ERREUR: Vous n'êtes pas le propriétaire de ce service");
            return;
        }
        
        out.println("Nouvelle classe du service:");
        String newClassName = in.readLine();
        
        if (!newClassName.startsWith(programmer.getLogin() + ".")) {
            out.println("ERREUR: La classe doit être dans le package " + programmer.getLogin());
            return;
        }
        
        try {
            Class<?> serviceClass;
            try {
                // Essayer de télécharger depuis le serveur FTP
                serviceClass = ServiceLoader.loadServiceClass(newClassName, programmer.getFtpUrl());
            } catch (Exception e) {
                // Si le téléchargement échoue, essayer de charger depuis le classpath local
                System.err.println("Avertissement: Téléchargement FTP échoué, chargement depuis classpath local: " + e.getMessage());
                serviceClass = Class.forName(newClassName);
            }
            
            if (!BRiService.class.isAssignableFrom(serviceClass)) {
                out.println("ERREUR: La classe doit implémenter BRiService");
                return;
            }
            
            BRiService newService = (BRiService) serviceClass.getDeclaredConstructor().newInstance();
            String newServiceName = newService.getServiceName();
            
            if (newServiceName == null || newServiceName.trim().isEmpty()) {
                out.println("ERREUR: Le nom du service ne peut pas être vide");
                return;
            }
            
            // Si le nom a changé, vérifier qu'il n'existe pas déjà
            if (!newServiceName.equals(serviceName) && services.containsKey(newServiceName)) {
                out.println("ERREUR: Un service avec ce nouveau nom existe déjà");
                return;
            }
            
            // Si le nom a changé, retirer l'ancien et ajouter le nouveau
            if (!newServiceName.equals(serviceName)) {
                services.remove(serviceName);
            }
            
            serviceInfo = new ServiceInfo(newServiceName, programmer.getLogin(), newClassName, newService);
            services.put(newServiceName, serviceInfo);
            out.println("OK: Service '" + newServiceName + "' mis à jour avec succès");
            
        } catch (Exception e) {
            out.println("ERREUR: Impossible de mettre à jour le service: " + e.getMessage());
        }
    }
    
    private void changeFtpUrl(BufferedReader in, PrintWriter out, Programmer programmer) throws IOException {
        out.println("Nouvelle adresse FTP:");
        String newFtpUrl = in.readLine();
        programmer.setFtpUrl(newFtpUrl);
        out.println("OK: Adresse FTP mise à jour");
    }
    
    private void handleAmateur(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            out.println("BRiLaunch - Connexion amateur");
            out.println("Bienvenue!");
            
            // Boucle principale pour utiliser plusieurs services
            while (true) {
                out.println("\n=== Services disponibles ===");
                
                // Lister les services actifs
                List<String> activeServices = new ArrayList<>();
                for (ServiceInfo serviceInfo : services.values()) {
                    if (serviceInfo.isActive()) {
                        activeServices.add(serviceInfo.getServiceName());
                    }
                }
                
                if (activeServices.isEmpty()) {
                    out.println("Aucun service disponible pour le moment.");
                    out.println("Appuyez sur Entrée pour actualiser...");
                    in.readLine();
                    continue;
                }
                
                // Afficher la liste
                for (int i = 0; i < activeServices.size(); i++) {
                    out.println((i + 1) + ". " + activeServices.get(i));
                }
                out.println("0. Quitter");
                out.println("Choisissez un service (numéro):");
                
                String choice = in.readLine();
                try {
                    int serviceIndex = Integer.parseInt(choice) - 1;
                    if (serviceIndex >= 0 && serviceIndex < activeServices.size()) {
                        String selectedServiceName = activeServices.get(serviceIndex);
                        ServiceInfo serviceInfo = services.get(selectedServiceName);
                        
                        out.println("Service '" + selectedServiceName + "' sélectionné");
                        out.println("Entrez les données d'entrée:");
                        String input = in.readLine();
                        
                        // Exécuter le service
                        try {
                            String result = serviceInfo.getServiceInstance().execute(input);
                            out.println("Résultat: " + result);
                        } catch (Exception e) {
                            out.println("ERREUR lors de l'exécution: " + e.getMessage());
                        }
                        
                    } else if (serviceIndex == -1) {
                        out.println("Au revoir!");
                        return;
                    } else {
                        out.println("Choix invalide");
                    }
                } catch (NumberFormatException e) {
                    out.println("Choix invalide");
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erreur avec amateur: " + e.getMessage());
        }
    }
    
    // Méthode pour créer un compte programmeur (pour les tests)
    public void registerProgrammer(String login, String password, String ftpUrl) {
        programmers.put(login, new Programmer(login, password, ftpUrl));
    }
}
