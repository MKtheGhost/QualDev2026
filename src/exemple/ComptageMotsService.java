package exemple;

import brilaunch.BRiService;

/**
 * Service d'exemple : compter le nombre de mots dans un texte.
 */
public class ComptageMotsService implements BRiService {
    
    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "0";
        }
        String[] words = input.trim().split("\\s+");
        return String.valueOf(words.length);
    }
    
    @Override
    public String getServiceName() {
        return "Comptage de mots";
    }
}
