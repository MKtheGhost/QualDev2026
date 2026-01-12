package exemple;

import brilaunch.BRiService;

/**
 * Service d'exemple : inversion d'un texte.
 * Exemple: "non ? si !" -> "! is ? non"
 */
public class InversionService implements BRiService {
    
    @Override
    public String execute(String input) {
        if (input == null) {
            return "";
        }
        // Inverser le texte
        return new StringBuilder(input).reverse().toString();
    }
    
    @Override
    public String getServiceName() {
        return "Inversion de texte";
    }
}
