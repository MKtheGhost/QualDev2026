package brilaunch;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/**
 * Classe utilitaire pour charger dynamiquement les services depuis le serveur FTP du programmeur.
 */
public class ServiceLoader {
    
    // Répertoire temporaire pour stocker les classes téléchargées
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "bri_services";
    
    static {
        // Créer le répertoire temporaire s'il n'existe pas
        new File(TEMP_DIR).mkdirs();
    }
    
    /**
     * Charge une classe de service depuis le serveur FTP du programmeur.
     * 
     * @param className Le nom complet de la classe (avec package)
     * @param ftpUrl L'URL du serveur FTP du programmeur
     * @return La classe chargée
     * @throws Exception Si le chargement échoue
     */
    public static Class<?> loadServiceClass(String className, String ftpUrl) throws Exception {
        // Construire le chemin du fichier .class sur le serveur
        // Exemple: services.InversionService -> services/InversionService.class
        String classPath = className.replace('.', '/') + ".class";
        
        // Construire l'URL complète
        String fullUrl = buildServiceUrl(ftpUrl, classPath);
        
        // Télécharger le fichier .class
        File classFile = downloadClassFile(fullUrl, className);
        
        // Charger la classe depuis le fichier téléchargé
        return loadClassFromFile(classFile, className);
    }
    
    /**
     * Construit l'URL complète pour télécharger le fichier .class.
     */
    private static String buildServiceUrl(String ftpUrl, String classPath) {
        // Nettoyer l'URL FTP (enlever le trailing slash si présent)
        String baseUrl = ftpUrl.trim();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        
        // Construire l'URL complète
        if (baseUrl.startsWith("http://") || baseUrl.startsWith("https://")) {
            // Support HTTP/HTTPS directement
            return baseUrl + "/" + classPath;
        } else if (baseUrl.startsWith("ftp://")) {
            // Pour FTP, on essaie de convertir en HTTP si possible
            // Sinon, on retourne l'URL FTP (nécessiterait Apache Commons Net pour fonctionner)
            // Pour cette implémentation, on essaie HTTP en remplaçant ftp:// par http://
            String httpUrl = baseUrl.replaceFirst("^ftp://", "http://");
            return httpUrl + "/" + classPath;
        } else if (baseUrl.startsWith("file://")) {
            // Support des fichiers locaux
            String localPath = baseUrl.substring(7);
            return "file://" + localPath + "/" + classPath;
        } else {
            // Traiter comme une URL HTTP
            if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
                baseUrl = "http://" + baseUrl;
            }
            return baseUrl + "/" + classPath;
        }
    }
    
    /**
     * Télécharge le fichier .class depuis l'URL.
     */
    private static File downloadClassFile(String url, String className) throws Exception {
        // Créer un nom de fichier unique pour éviter les conflits
        String fileName = className.replace('.', '_') + "_" + System.currentTimeMillis() + ".class";
        File localFile = new File(TEMP_DIR, fileName);
        
        try {
            InputStream inputStream = null;
            
            if (url.startsWith("file://")) {
                // Fichier local
                String filePath = url.substring(7);
                inputStream = new FileInputStream(filePath);
            } else if (url.startsWith("http://") || url.startsWith("https://")) {
                // Téléchargement HTTP/HTTPS
                URL httpUrl = new URL(url);
                URLConnection connection = httpUrl.openConnection();
                connection.setConnectTimeout(5000); // 5 secondes
                connection.setReadTimeout(10000); // 10 secondes
                inputStream = connection.getInputStream();
            } else if (url.startsWith("ftp://")) {
                // FTP réel nécessiterait Apache Commons Net
                // Pour cette implémentation, on lance une exception
                throw new IOException("Téléchargement FTP réel non implémenté. " +
                    "Utilisez HTTP/HTTPS ou installez Apache Commons Net pour le support FTP complet.");
            } else {
                throw new IOException("Protocole non supporté: " + url);
            }
            
            // Copier le fichier
            try (FileOutputStream outputStream = new FileOutputStream(localFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            
            return localFile;
            
        } catch (Exception e) {
            // Si le téléchargement échoue, essayer de charger depuis le classpath local
            // (fallback pour compatibilité)
            System.err.println("Avertissement: Impossible de télécharger depuis " + url + 
                ". Tentative de chargement depuis le classpath local: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Charge une classe depuis un fichier .class.
     */
    private static Class<?> loadClassFromFile(File classFile, String className) throws Exception {
        // Créer un URLClassLoader avec le répertoire parent du fichier
        File parentDir = classFile.getParentFile();
        URL[] urls = {parentDir.toURI().toURL()};
        
        try (URLClassLoader classLoader = new URLClassLoader(urls, ServiceLoader.class.getClassLoader())) {
            // Charger la classe
            Class<?> loadedClass = classLoader.loadClass(className);
            return loadedClass;
        }
    }
    
    /**
     * Nettoie les fichiers temporaires téléchargés.
     */
    public static void cleanup() {
        File tempDir = new File(TEMP_DIR);
        if (tempDir.exists()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
}
