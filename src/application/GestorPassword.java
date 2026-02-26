package application;
import java.sql.Connection;

import application.interfaces.panels.PanelLogin;
import application.models.cuenta.CuentaModel;
import application.models.user.UserModel;
import orm.connection.Connector;
import orm.connection.dao.MigrationDAO;

import orm.utils.formats.DbConfig;
import orm.utils.formats.UsableMethods;

public class GestorPassword {
    public static void main(String[] args) {
        logginUser();
    }
    private static final DbConfig initDataBase(String dbName) {
        DbConfig mConfig = new DbConfig("", "localhost", 3306, "root", "5x5W12%$asd");
        try {
            Connection con = new Connector(mConfig).mysqlConnection();
            MigrationDAO miDAO = new MigrationDAO(con, "");
            miDAO.createDatabase(dbName);
            con.close();
            return new DbConfig(dbName, mConfig.getHost(), mConfig.getPort(), mConfig.getUser(), mConfig.getPassword());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static final void initTable(String tbName, UsableMethods model, Connection cursor) {
        MigrationDAO miDAO = new MigrationDAO(cursor, tbName);
        miDAO.createTable(model, "n");
    }
    private static final void logginUser() {
        try {
            DbConfig miConfig = initDataBase("contrasenias");
            Connection cursor = new Connector(miConfig).mysqlConnection();
            initTable("user", new UserModel(), cursor);
            initTable("cuenta", new CuentaModel(), cursor);
            new PanelLogin(miConfig, cursor).createUI("Login");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
