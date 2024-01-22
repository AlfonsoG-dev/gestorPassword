package Interface.Utils;


import java.sql.SQLException;
import java.sql.Statement;
import java.security.SecureRandom;
import java.sql.Connection;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Conexion.Query.QueryDAO;
import Model.ModelMethods;

public final class PanelUtils<T> {

    private QueryDAO<T> myQueryDAO;
    private Connection cursor;
    private PasswordOptions setOptions;
    public PanelUtils(QueryDAO<T> nQueryDAO) {
        myQueryDAO = nQueryDAO;
        cursor = myQueryDAO.GetConnection();
    }
    public void SetPasswordValues(PasswordOptions pOptions) {
        setOptions = pOptions;
    }
    public PasswordOptions getPasswrodOptions() {
        return setOptions;
    }

    public ArrayList<T> DataList() {
        return myQueryDAO.ReadAll();
    }

    public T FindOperation(String condition, String type) {
        return myQueryDAO.FindByColumnName(condition, type);
    }

    public boolean InsertOperation(ModelMethods model, String condition, String type) throws SQLException {
        return myQueryDAO.InsertNewRegister(model, condition, type);
    }

    public boolean UpdateOperation(ModelMethods model, String condition, String type) throws SQLException {
        return myQueryDAO.UpdateRegister(model, condition, type);
    }
    public boolean DeleteOperation(String condition, String type) throws SQLException {
        return myQueryDAO.EliminarRegistro(condition, type);
    }

    public void SetAutoImcrement() throws SQLException {
        int tableSize = myQueryDAO.ReadAll().size()+1;
        String sql = "alter table cuenta AUTO_INCREMENT=" + tableSize;
        Statement stm = cursor.createStatement();
        stm.executeUpdate(sql);
    }
    
    public StringBuilder GeneratePassword(PasswordOptions options) {
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

    public QueryDAO<T> getMyQueryDAO() {
        return myQueryDAO;
    }

    public void InfoMessage(JFrame myFrame, String message, String title) {
        JOptionPane.showMessageDialog(myFrame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    public void ErrorMessage(JFrame myFrame, String message, String title) {
        JOptionPane.showMessageDialog(myFrame, message, title, JOptionPane.ERROR_MESSAGE);
    }
}

