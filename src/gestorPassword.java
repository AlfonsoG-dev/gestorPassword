import java.sql.Connection;

import Interface.Panels.PanelLogin;


import ORM.DbConnection.Connector;
import ORM.DbConnection.DAO.MigrationDAO;

import ORM.Utils.Formats.DbConfig;
import ORM.Utils.Formats.UsableMethods;

import Models.Cuenta.CuentaModel;
import Models.User.UserModel;

public class gestorPassword {
    public static void main(String[] args) {
        LogginUser();
    }
    private final static DbConfig InitDB(String db_name) {
        DbConfig mConfig = new DbConfig(
                "",
                "localhost",
                3306,
                "test_user",
                "5x5W12"
        );
        try {
            Connection con = new Connector(mConfig).mysqlConnection();
            MigrationDAO miDAO = new MigrationDAO(con, "");
            miDAO.createDatabase(db_name);
            con.close();
            return new DbConfig(
                    db_name,
                    mConfig.getHost(),
                    mConfig.getPort(),
                    mConfig.getUser(),
                    mConfig.getPassword()
            );
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private final static void InitTable(String tbName, UsableMethods model, Connection cursor) {
        MigrationDAO miDAO = new MigrationDAO(cursor, tbName);
        miDAO.createTable(model, "n");
    }
    private final static void LogginUser() {
        try {
            DbConfig miConfig = InitDB("contrasenias");
            Connection cursor = new Connector(miConfig).mysqlConnection();
            InitTable("user", new UserModel(), cursor);
            InitTable("cuenta", new CuentaModel(), cursor);
            new PanelLogin(miConfig, cursor);
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
