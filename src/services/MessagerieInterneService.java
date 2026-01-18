package services;

import brilaunch.BRiService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service de messagerie interne avec ressource partagée.
 * 
 * Ce service permet d'envoyer et de lire des messages entre utilisateurs.
 * Les messages sont stockés dans une ressource partagée (Map statique).
 * 
 * Format d'entrée :
 * - Envoi: "ENVOI:pseudo_destinataire:message"
 * - Lecture: "LECTURE:pseudo_expediteur"
 * 
 * Résultat :
 * - Envoi: Confirmation de l'envoi
 * - Lecture: Liste des messages reçus
 */
public class MessagerieInterneService implements BRiService {
    
    // Ressource partagée : stockage des messages
    // Clé: pseudo destinataire, Valeur: liste des messages
    private static Map<String, List<Message>> boitesAuxLettres = new ConcurrentHashMap<>();
    
    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "ERREUR: Format attendu:\n" +
                   "  - Envoi: ENVOI:pseudo_destinataire:message\n" +
                   "  - Lecture: LECTURE:pseudo_expediteur";
        }
        
        String commande = input.trim().toUpperCase();
        
        if (commande.startsWith("ENVOI:")) {
            return envoyerMessage(input);
        } else if (commande.startsWith("LECTURE:")) {
            return lireMessages(input);
        } else {
            return "ERREUR: Commande invalide. Utilisez ENVOI: ou LECTURE:";
        }
    }
    
    /**
     * Envoie un message à un destinataire.
     * Format: "ENVOI:pseudo_destinataire:message"
     */
    private String envoyerMessage(String input) {
        // Parser: ENVOI:pseudo:message
        String[] parts = input.split(":", 3);
        if (parts.length != 3) {
            return "ERREUR: Format invalide. Format attendu: ENVOI:pseudo_destinataire:message";
        }
        
        String pseudoDestinataire = parts[1].trim();
        String message = parts[2].trim();
        
        if (pseudoDestinataire.isEmpty()) {
            return "ERREUR: Le pseudo du destinataire ne peut pas être vide";
        }
        
        if (message.isEmpty()) {
            return "ERREUR: Le message ne peut pas être vide";
        }
        
        // Créer le message
        Message msg = new Message(pseudoDestinataire, message, new Date());
        
        // Stocker dans la boîte aux lettres du destinataire
        boitesAuxLettres.computeIfAbsent(pseudoDestinataire, k -> new ArrayList<>()).add(msg);
        
        return "OK: Message envoyé à '" + pseudoDestinataire + "' avec succès";
    }
    
    /**
     * Lit les messages d'un utilisateur.
     * Format: "LECTURE:pseudo_expediteur"
     */
    private String lireMessages(String input) {
        // Parser: LECTURE:pseudo
        String[] parts = input.split(":", 2);
        if (parts.length != 2) {
            return "ERREUR: Format invalide. Format attendu: LECTURE:pseudo_expediteur";
        }
        
        String pseudo = parts[1].trim();
        
        if (pseudo.isEmpty()) {
            return "ERREUR: Le pseudo ne peut pas être vide";
        }
        
        // Récupérer les messages
        List<Message> messages = boitesAuxLettres.get(pseudo);
        
        if (messages == null || messages.isEmpty()) {
            return "Aucun message pour '" + pseudo + "'";
        }
        
        // Construire la réponse
        StringBuilder result = new StringBuilder();
        result.append("=== Messages pour '").append(pseudo).append("' ===\n");
        result.append("Nombre de messages: ").append(messages.size()).append("\n\n");
        
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            result.append("Message ").append(i + 1).append(":\n");
            result.append("  Date: ").append(msg.getDate()).append("\n");
            result.append("  Contenu: ").append(msg.getContenu()).append("\n");
            if (i < messages.size() - 1) {
                result.append("\n");
            }
        }
        
        return result.toString();
    }
    
    /**
     * Classe interne représentant un message.
     */
    private static class Message {
        private String destinataire;
        private String contenu;
        private Date date;
        
        public Message(String destinataire, String contenu, Date date) {
            this.destinataire = destinataire;
            this.contenu = contenu;
            this.date = date;
        }
        
        public String getDestinataire() {
            return destinataire;
        }
        
        public String getContenu() {
            return contenu;
        }
        
        public Date getDate() {
            return date;
        }
    }
    
    @Override
    public String getServiceName() {
        return "Messagerie interne";
    }
}
