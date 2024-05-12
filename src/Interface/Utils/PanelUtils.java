package Interface.Utils;


import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

import java.security.SecureRandom;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import Conexion.Query.QueryDAO;

import Model.ModelMethods;

import Utils.Formats.ParamValue;
import Utils.Model.ModelUtils;

public final class PanelUtils<T> {

    private QueryDAO<T> myQueryDAO;
    private Connection cursor;
    private PasswordOptions setOptions;
    private ModelUtils modelUtils;
    public PanelUtils(QueryDAO<T> nQueryDAO) {
        myQueryDAO = nQueryDAO;
        cursor = myQueryDAO.getConnection();
        modelUtils = new ModelUtils();
    }
    public void setPasswordValues(PasswordOptions pOptions) {
        setOptions = pOptions;
    }
    public PasswordOptions getPasswordOptions() {
        return setOptions;
    }

    public ArrayList<T> myDataList() {
        return myQueryDAO.readAll(new ParamValue("order", "nombre"));
    }
    public String[] getModelColumn(ModelMethods model) {
        return modelUtils.getModelColumns(model.initModel(), true).split(",");
    }
    public String getModelType(ModelMethods model) {
        return modelUtils.getModelTypes(model.getAllProperties(), true);
    }
    public T findOperation(ParamValue condition) {
        return myQueryDAO.findByColumnName(condition);
    }

    public boolean insertOperation(ModelMethods model, ParamValue condition) throws SQLException {
        return myQueryDAO.insertNewRegister(model, condition);
    }

    public boolean updateOperation(ModelMethods model, ParamValue condition) throws SQLException {
        return myQueryDAO.updateRegister(model, condition);
    }
    public boolean deleteOperation(ParamValue condition) throws SQLException {
        return myQueryDAO.deleteRegister(condition);
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
        String 
            letters = options.addLetter() ? "abcdefghijklmnñopqrstuvwxyz" : "",
            simbols = options.addSimbol() ? "!#$%&/()=?¡¿'°|¨+{}[];:_-<>^`~\\¬": "",
            numbers = options.addNumber() ? "0123456789" : "",
            combination = letters + simbols + numbers;
        for(int i=0; i<options.size(); ++i) {
            int index = random.nextInt(combination.length());
            pass.append(combination.charAt(index));
        }
        return pass;
    }
    /**
     * build the object using the table row and column
     * @param row: table row
     * @param column: table column
     * @return the object fron the table using row and column
     */
    public T buildObjectFromTable(int row, int column, int loggedUser, JTable mTable) {
        String columName = mTable.getColumnName(column);
        String[]
            c = {columName, "user_id_fk"},
            v = {mTable.getValueAt(row, column).toString(), String.valueOf(loggedUser)};
        ParamValue condition = new ParamValue(c, v, "and");
        T myObject    = findOperation(condition);
        if(myObject == null) {
            errorMessage(null, "invalid value of field", "Error");
            return null;
        } else {
            return myObject;
        }
    }

    public QueryDAO<T> getMyQueryDAO() {
        return myQueryDAO;
    }

    public void infoMessage(JFrame myFrame, String message, String title) {
        JOptionPane.showMessageDialog(
                myFrame,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    public void errorMessage(JFrame myFrame, String message, String title) {
        JOptionPane.showMessageDialog(
                myFrame,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
}

