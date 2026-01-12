package brilaunch;

/**
 * Classe représentant un programmeur certifié BRi.
 */
public class Programmer {
    private String login;
    private String password;
    private String ftpUrl;
    
    public Programmer(String login, String password, String ftpUrl) {
        this.login = login;
        this.password = password;
        this.ftpUrl = ftpUrl;
    }
    
    public String getLogin() {
        return login;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getFtpUrl() {
        return ftpUrl;
    }
    
    public void setFtpUrl(String ftpUrl) {
        this.ftpUrl = ftpUrl;
    }
}
