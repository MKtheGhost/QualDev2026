package brilaunch;

/**
 * Interface BRi que tous les services doivent implémenter.
 * Un service BRi doit avoir une méthode execute qui prend une String en entrée
 * et retourne une String en résultat.
 */
public interface BRiService {
    /**
     * Exécute le service avec les données fournies.
     * @param input Les données d'entrée (String)
     * @return Le résultat du service (String)
     */
    String execute(String input);
    
    /**
     * Retourne le nom du service.
     * @return Le nom du service
     */
    String getServiceName();
}
