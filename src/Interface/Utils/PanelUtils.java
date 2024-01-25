package Interface.Utils;


import java.sql.SQLException;
import java.sql.Statement;
import java.security.SecureRandom;
import java.sql.Connection;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import Conexion.Query.QueryDAO;
import Model.ModelMethods;
import Utils.QueryUtils;

public final class PanelUtils<T> {

    private QueryDAO<T> myQueryDAO;
    private Connection cursor;
    private PasswordOptions setOptions;
    private QueryUtils queryUtils; 
    public PanelUtils(QueryDAO<T> nQueryDAO) {
        myQueryDAO = nQueryDAO;
        cursor = myQueryDAO.getConnection();
        queryUtils = new QueryUtils();
    }
    public void setPasswordValues(PasswordOptions pOptions) {
        setOptions = pOptions;
    }
    public PasswordOptions getPasswordOptions() {
        return setOptions;
    }

    public ArrayList<T> myDataList() {
        return myQueryDAO.readAll();
    }
    public String[] getModelColumn(ModelMethods model) {
        return queryUtils.getModelColumns(model.initModel(), true).split(",");
    }
    public String getModelType(ModelMethods model) {
        return queryUtils.getModelType(model.getAllProperties(), true);
    }
    public T findOperation(String condition, String type) {
        return myQueryDAO.findByColumnName(condition, type);
    }

    public boolean insertOperation(ModelMethods model, String condition, String type) throws SQLException {
        return myQueryDAO.insertNewRegister(model, condition, type);
    }

    public boolean updateOperation(ModelMethods model, String condition, String type) throws SQLException {
        return myQueryDAO.updateRegister(model, condition, type);
    }
    public boolean deleteOperation(String condition, String type) throws SQLException {
        return myQueryDAO.eliminarRegistro(condition, type);
    }

    public void setAutoImcrement() throws SQLException {
        int tableSize = myDataList().size()+1;
        String sql = "alter table cuenta AUTO_INCREMENT=" + tableSize;
        Statement stm = cursor.createStatement();
        stm.executeUpdate(sql);
    }
    
    public StringBuilder generatePassword(PasswordOptions options) {
        StringBuilder pass = new StringBuilder();
        SecureRandom random = new SecureRandom();
        String letters = options.addLetter() ? "abcdefghijklmnñopqrstuvwxyz" : "";
        String simbols = options.addSimbol() ? "!#$%&/()=?¡¿'°|¨+{}[];:_-<>^`~\\¬": "";
        String numbers = options.addNumber() ? "0123456789" : "";

        String combination = letters + simbols + numbers;
        for(int i=0; i<options.size(); ++i) {
            int index = random.nextInt(combination.length());
            pass.append(combination.charAt(index));
        }
        return pass;
    }
    /**
     * build the cuenta using the table row and column
     * @param row: table row
     * @param column: table column
     * @return the cuenta fron the table using row and column
     */
    public T buildCuentaFromTable(int row, int column, int loggedUser, JTable mTable) {
        String columName = mTable.getColumnName(column);
        String options = columName + ": " + mTable.getValueAt(row, column).toString() + ", user_id_fk: " + loggedUser;
        T myCuenta = findOperation(options, "and");
        if(myCuenta == null) {
            errorMessage(null, "invalid value of field", "Error");
            return null;
        } else {
            return myCuenta;
        }
    }

    public QueryDAO<T> getMyQueryDAO() {
        return myQueryDAO;
    }

    public void infoMessage(JFrame myFrame, String message, String title) {
        JOptionPane.showMessageDialog(myFrame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    public void errorMessage(JFrame myFrame, String message, String title) {
        JOptionPane.showMessageDialog(myFrame, message, title, JOptionPane.ERROR_MESSAGE);
    }
}

