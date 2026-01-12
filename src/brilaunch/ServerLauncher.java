package brilaunch;

/**
 * Classe principale pour lancer le serveur avec quelques programmeurs de test.
 */
public class ServerLauncher {
    public static void main(String[] args) {
        BRiLaunchServer server = new BRiLaunchServer();
        
        // Créer quelques comptes programmeurs de test
        server.registerProgrammer("exemple", "password123", "ftp://exemple.com");
        server.registerProgrammer("test", "test123", "ftp://test.com");
        
        System.out.println("\nComptes programmeurs créés:");
        System.out.println("  - exemple / password123");
        System.out.println("  - test / test123");
        System.out.println("\nVous pouvez maintenant lancer les clients!");
        
        server.start();
    }
}
