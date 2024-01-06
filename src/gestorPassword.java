import Config.DbConfig;
import Interface.Panels.PanelPrincipal;
class gestorPassword {
    public static void main(String[] args) {
        try {
            DbConfig mConfig = new DbConfig("contrasenias", "localhost", "3306", "root", "5x5W12%$");
            // TODO: create a login panel, that panel list the accounts for the logged user
            int loggedUser = 1;
            new PanelPrincipal(mConfig, loggedUser);
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
