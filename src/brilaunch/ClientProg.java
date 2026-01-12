package brilaunch;

import java.io.*;
import java.net.*;

/**
 * Client pour les programmeurs.
 */
public class ClientProg {
    private static final String SERVER_HOST = "localhost";
    private static final int PORT_PROG = 8888;
    
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, PORT_PROG);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            
            // Thread pour lire les messages du serveur
            Thread readThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                        
                        // Quitter si nécessaire
                        if (serverMessage.contains("Au revoir")) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                // Ignorer
                            }
                            return;
                        }
                    }
                } catch (IOException e) {
                    // Connexion fermée normalement
                }
            });
            readThread.setDaemon(true);
            readThread.start();
            
            // Thread pour envoyer les entrées utilisateur
            String userInput;
            while ((userInput = console.readLine()) != null) {
                if (socket.isClosed()) {
                    break;
                }
                out.println(userInput);
            }
            
            // Fermer proprement
            try {
                socket.close();
            } catch (IOException e) {
                // Ignorer
            }
            
        } catch (IOException e) {
            System.err.println("Erreur client programmeur: " + e.getMessage());
        }
    }
}
