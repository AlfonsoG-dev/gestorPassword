package Interface.Panels;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Conexion.Query.QueryDAO;
import Config.DbConfig;
import Mundo.Users.User;
import Mundo.Users.UserBuilder;

public class PanelRegistro {
    private JFrame myFrame;
    private JLabel headerLabel;
    private JPanel pPrincipal;
    public PanelRegistro(String frameTitle, int hight, int height, DbConfig myConfig) {
        CreateUI(frameTitle, "user", hight, height, myConfig);
    }
    public String[] ComboBoxUsers(String tableName, DbConfig myConfig) {
        QueryDAO<User> userDAO = new QueryDAO<>(tableName, myConfig);
        String result = "select a user...,";
        ArrayList<User> users = userDAO.ReadAll(new UserBuilder());
        for(User u: users) {
            result += u.getNombre() + ",";
        }
        return result.split(",");
    }
    public JPanel OptionsComponent(String tableName, DbConfig myConfig) {
        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(3, 2));
        pOptions.add(new JLabel("nombre"));
        pOptions.add(new JTextField());
        pOptions.add(new JLabel("email"));
        pOptions.add(new JTextField());
        pOptions.add(new JLabel("user_id"));
        pOptions.add(new JComboBox<String>(ComboBoxUsers(tableName, myConfig)));
        return pOptions;
    }
    public void CreateUI(String frameTitle, String tableName,int hight, int height, DbConfig myConfig) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));


        headerLabel = new JLabel("Register", JLabel.CENTER);

        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 1));

        pPrincipal.add(OptionsComponent(tableName, myConfig));
        pPrincipal.add(new JButton("OK"));

        myFrame.add(headerLabel);
        myFrame.add(pPrincipal);
        myFrame.setVisible(true);
    }
}
