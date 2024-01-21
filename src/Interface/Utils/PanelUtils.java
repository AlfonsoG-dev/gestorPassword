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
    private int size;
    private boolean letter;
    private boolean simbol;
    private boolean number;


    public PanelUtils(QueryDAO<T> nQueryDAO) {
        myQueryDAO = nQueryDAO;
        cursor = myQueryDAO.GetConnection();
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @return the number
     */
    public boolean isNumber() {
        return number;
    }
    /**
     * @return the simbol
     */
    public boolean isSimbol() {
        return simbol;
    }

    /**
     * @return the letter
     */
    public boolean isLetter() {
        return letter;
    }
    public void SetPasswordValues(int nSize, boolean nLetter, boolean nSimbol, boolean nNumber) {
        size = nSize;
        letter = nLetter;
        simbol = nSimbol;
        number = nNumber;
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
    
    public StringBuilder GeneratePassword(int size, boolean addNumber, boolean addLetters, boolean addSimbols) {
        StringBuilder pass = new StringBuilder();
        SecureRandom random = new SecureRandom();
        String letters = addLetters ? "abcdefghijklmnñopqrstuvwxyz" : "";
        String simbols = addSimbols ? "!#$%&/()=?¡¿'°|¨+{}[];:_-<>^`~\\¬": "";
        String numbers = addNumber ? "0123456789" : "";

        String combination = letters + simbols + numbers;
        for(int i=0; i<size; ++i) {
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

