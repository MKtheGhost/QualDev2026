package brilaunch;

/**
 * Classe contenant les informations sur un service installé.
 */
public class ServiceInfo {
    private String serviceName;
    private String programmerLogin;
    private String className;
    private BRiService serviceInstance;
    private boolean isActive;
    
    public ServiceInfo(String serviceName, String programmerLogin, String className, BRiService serviceInstance) {
        this.serviceName = serviceName;
        this.programmerLogin = programmerLogin;
        this.className = className;
        this.serviceInstance = serviceInstance;
        this.isActive = true;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public String getProgrammerLogin() {
        return programmerLogin;
    }
    
    public String getClassName() {
        return className;
    }
    
    public BRiService getServiceInstance() {
        return serviceInstance;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
}
