import Config.DbConfig;
import Interface.Panels.PanelPrincipal;
class gestorPassword {
    public static void main(String[] args) {
        try {
            DbConfig mConfig = new DbConfig("contrasenias", "localhost", "3306", "root", "5x5W12%$");
            new PanelPrincipal(mConfig);
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
