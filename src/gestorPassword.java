import Config.DbConfig;
import Interface.Panels.PanelLogin;
class gestorPassword {
    public static void main(String[] args) {
        LogginUser();
    }
    private final static void LogginUser() {
        try {
            DbConfig mConfig = new DbConfig("contrasenias", "localhost", "3306", "test_user", "5x5W12");
            new PanelLogin(mConfig);
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
