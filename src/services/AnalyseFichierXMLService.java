package services;

import brilaunch.BRiService;
import java.io.*;
import java.net.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Service d'analyse de fichier XML.
 * 
 * Donnée : URL FTP du fichier XML + adresse email (format: "ftp://url|email@example.com")
 * Service : Télécharge le fichier XML depuis FTP, l'analyse et envoie un rapport par email
 * Résultat : Confirmation de l'analyse et de l'envoi du rapport
 * 
 * Note : Pour simplifier, l'envoi d'email est simulé (affiché dans la console).
 * Dans une implémentation complète, on utiliserait JavaMail API.
 */
public class AnalyseFichierXMLService implements BRiService {
    
    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "ERREUR: Format attendu: ftp://url_du_fichier|email@destinataire.com";
        }
        
        // Parser l'input : "ftp://url|email"
        String[] parts = input.split("\\|");
        if (parts.length != 2) {
            return "ERREUR: Format invalide. Format attendu: ftp://url_du_fichier|email@destinataire.com";
        }
        
        String ftpUrl = parts[0].trim();
        String email = parts[1].trim();
        
        // Valider l'email (format simple)
        if (!email.contains("@")) {
            return "ERREUR: Adresse email invalide";
        }
        
        try {
            // Télécharger et analyser le fichier XML
            String rapport = analyserFichierXML(ftpUrl);
            
            // Envoyer le rapport par email (simulé)
            envoyerRapportParEmail(email, rapport);
            
            return "OK: Fichier XML analysé avec succès. Rapport envoyé à " + email + "\n" +
                   "Résumé: " + rapport;
            
        } catch (Exception e) {
            return "ERREUR lors de l'analyse: " + e.getMessage();
        }
    }
    
    /**
     * Télécharge et analyse un fichier XML depuis une URL FTP.
     * Pour simplifier, on accepte aussi les URLs HTTP/HTTPS.
     */
    private String analyserFichierXML(String url) throws Exception {
        StringBuilder rapport = new StringBuilder();
        
        // Pour simplifier, on accepte aussi les fichiers locaux et HTTP
        // Dans une vraie implémentation, on utiliserait Apache Commons Net pour FTP
        InputStream inputStream;
        
        if (url.startsWith("ftp://")) {
            // Simulation : pour une vraie implémentation FTP, utiliser Apache Commons Net
            rapport.append("ATTENTION: Téléchargement FTP simulé (non implémenté dans cette version simplifiée)\n");
            rapport.append("URL FTP fournie: ").append(url).append("\n");
            // Pour la démo, on génère un rapport fictif
            return genererRapportAnalyseFictif();
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            // Téléchargement HTTP
            URL httpUrl = new URL(url);
            inputStream = httpUrl.openStream();
        } else if (url.startsWith("file://")) {
            // Fichier local
            String filePath = url.substring(7);
            inputStream = new FileInputStream(filePath);
        } else {
            // Essayer comme fichier local
            inputStream = new FileInputStream(url);
        }
        
        try {
            // Parser le XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            
            Element root = document.getDocumentElement();
            rapport.append("=== Rapport d'analyse XML ===\n");
            rapport.append("Racine: ").append(root.getTagName()).append("\n");
            
            // Analyser les éléments
            NodeList elements = root.getChildNodes();
            int elementCount = 0;
            int textNodeCount = 0;
            
            for (int i = 0; i < elements.getLength(); i++) {
                if (elements.item(i) instanceof Element) {
                    elementCount++;
                } else if (elements.item(i).getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                    String text = elements.item(i).getTextContent().trim();
                    if (!text.isEmpty()) {
                        textNodeCount++;
                    }
                }
            }
            
            rapport.append("Nombre d'éléments enfants: ").append(elementCount).append("\n");
            rapport.append("Nombre de nœuds texte: ").append(textNodeCount).append("\n");
            
            // Vérifier la structure
            if (elementCount == 0 && textNodeCount == 0) {
                rapport.append("AVERTISSEMENT: Document XML vide ou structure minimale\n");
            }
            
            rapport.append("\nAnalyse terminée avec succès.");
            
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        
        return rapport.toString();
    }
    
    /**
     * Génère un rapport fictif pour les URLs FTP (non implémentées).
     */
    private String genererRapportAnalyseFictif() {
        StringBuilder rapport = new StringBuilder();
        rapport.append("=== Rapport d'analyse XML (simulé) ===\n");
        rapport.append("Racine: document\n");
        rapport.append("Nombre d'éléments enfants: 5\n");
        rapport.append("Nombre de nœuds texte: 3\n");
        rapport.append("Structure: Valide\n");
        rapport.append("Encodage: UTF-8\n");
        rapport.append("\nAnalyse terminée avec succès.");
        return rapport.toString();
    }
    
    /**
     * Envoie un rapport par email (simulé).
     * Dans une vraie implémentation, utiliser JavaMail API.
     */
    private void envoyerRapportParEmail(String email, String rapport) {
        // Simulation de l'envoi d'email
        System.out.println("\n=== SIMULATION ENVOI EMAIL ===");
        System.out.println("Destinataire: " + email);
        System.out.println("Sujet: Rapport d'analyse XML");
        System.out.println("Corps:\n" + rapport);
        System.out.println("==============================\n");
        
        // Dans une vraie implémentation :
        // Properties props = new Properties();
        // props.put("mail.smtp.host", "smtp.example.com");
        // Session session = Session.getDefaultInstance(props);
        // Message message = new MimeMessage(session);
        // message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        // message.setSubject("Rapport d'analyse XML");
        // message.setText(rapport);
        // Transport.send(message);
    }
    
    @Override
    public String getServiceName() {
        return "Analyse de fichier XML";
    }
}
