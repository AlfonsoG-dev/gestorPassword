import java.sql.Connection;

import Conexion.Conector;
import Conexion.Migration.MigrationDAO;
import Config.DbConfig;

import Interface.Panels.PanelLogin;
import Model.ModelMethods;
import Mundo.Cuentas.Cuenta;
import Mundo.Users.User;

class gestorPassword {
    public static void main(String[] args) {
        LogginUser();
    }
    private final static DbConfig InitDB(String db_name) {
        DbConfig mConfig = new DbConfig("", "localhost", "3306", "test_user", "5x5W12");
        try {
            Connection con = new Conector(mConfig).conectarMySQL();
            MigrationDAO miDAO = new MigrationDAO("", con);
            miDAO.createDataBase(db_name);
            con.close();
            return new DbConfig(db_name, mConfig.hostname(), mConfig.port(), mConfig.username(), mConfig.password());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private final static void InitTable(String tbName, ModelMethods model, DbConfig miConfig, Connection cursor) {
        MigrationDAO miDAO = new MigrationDAO(tbName, cursor);
        miDAO.createTable(model);
    }
    private final static void LogginUser() {
        try {
            DbConfig miConfig = InitDB("contrasenias");
            Connection cursor = new Conector(miConfig).conectarMySQL();
            InitTable("user", new User(), miConfig, cursor);
            InitTable("cuenta", new Cuenta(), miConfig, cursor);
            new PanelLogin(miConfig, cursor);
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
