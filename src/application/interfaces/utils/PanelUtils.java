package application.interfaces.utils;


import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

import java.security.SecureRandom;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import orm.connection.dao.QueryDAO;

import orm.utils.formats.UsableMethods;
import orm.utils.formats.ParamValue;
import orm.utils.model.ModelUtils;


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

    public List<T> myDataList() {
        return myQueryDAO.readAll();
    }
    public String[] getModelColumn(UsableMethods model) {
        return modelUtils.getColumns(model.initModel(), true).split(",");
    }
    public String getModelType(UsableMethods model) {
        return modelUtils.getTypes(model.getInstanceData(), true);
    }
    public List<T> findOperation(ParamValue condition) {
        return myQueryDAO.preparedSelect(condition);
    }

    public boolean insertOperation(UsableMethods model) {
        return myQueryDAO.preparedInsert(model);
    }

    public boolean updateOperation(UsableMethods model, ParamValue condition) {
        return myQueryDAO.preparedUpdate(model, condition);
    }
    public boolean deleteOperation(ParamValue condition) {
        return myQueryDAO.preparedDelete(condition);
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
     * build the object using the table row and column
     * @param row: table row
     * @param column: table column
     * @return the object fron the table using row and column
     */
    public T buildObjectFromTable(int row, int column, int loggedUser, JTable mTable) {
        String columName = mTable.getColumnName(column);
        String[] c = {columName, "user_id_fk"};
        String[] v = {mTable.getValueAt(row, column).toString(), String.valueOf(loggedUser)};
        ParamValue condition = new ParamValue(c, v, "and");
        T myObject    = findOperation(condition).get(0);
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

