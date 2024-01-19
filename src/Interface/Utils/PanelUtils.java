package Interface.Utils;


import java.sql.SQLException;
import java.sql.Statement;
import java.security.SecureRandom;
import java.sql.Connection;

import java.util.ArrayList;

import Conexion.Query.QueryDAO;
import Model.ModelBuilderMethods;
import Model.ModelMethods;

public final class PanelUtils<T> {

    private QueryDAO<T> myQueryDAO;
    private Connection cursor;

    public PanelUtils(QueryDAO<T> nQueryDAO, Connection nCursor) {
        myQueryDAO = nQueryDAO;
        cursor = nCursor;
    }

    public ArrayList<T> DataList() {
        ArrayList<T> myDataList = new ArrayList<T>();
        return myDataList;
    }

    public T FindOperation(String condition, String type, ModelBuilderMethods<T> builer) {
        return myQueryDAO.FindByColumnName(condition, type, builer);
    }

    public boolean InsertOperation(ModelMethods model, String condition, String type, ModelBuilderMethods<T> builder) throws SQLException {
        return myQueryDAO.InsertNewRegister(model, condition, type, builder);
    }

    public boolean UpdateOperation(ModelMethods model, String condition, String type, ModelBuilderMethods<T> builder) throws SQLException {
        return myQueryDAO.UpdateRegister(model, condition, type, builder);
    }
    public boolean DeleteOperation(String condition, String type, ModelBuilderMethods<T> builder) throws SQLException {
        return myQueryDAO.EliminarRegistro(condition, type, builder);
    }

    public void SetAutoImcrement(ModelBuilderMethods<T> builder) throws SQLException {
        int tableSize = myQueryDAO.ReadAll(builder).size()+1;
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

}

