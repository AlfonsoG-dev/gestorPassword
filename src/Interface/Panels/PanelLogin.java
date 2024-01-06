package Interface.Panels;


import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Conexion.Query.QueryDAO;
import Config.DbConfig;
import Mundo.Users.User;
import Mundo.Users.UserBuilder;

public class PanelLogin {

    private JFrame myFrame;
    private JPanel pPrincipal;
    private JTextField txtUserName;
    private JTextField txtUserPassword;
    private JButton btnIngreso;
    private JButton btnCancel;

    private QueryDAO<User> userDAO;

    public PanelLogin(DbConfig myConfig) {
        userDAO = new QueryDAO<>("user", myConfig);
        CreateUI("Loggin", myConfig);
    }

    private JPanel LoginContent() {
        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 2));
        
        txtUserName = new JTextField();
        pPrincipal.add(new JLabel("name"));
        pPrincipal.add(txtUserName);

        txtUserPassword = new JTextField();
        pPrincipal.add(new JLabel("password"));
        pPrincipal.add(txtUserPassword);

        return pPrincipal;
    }

    private JPanel LoginOptions(DbConfig myConfig) {

        JPanel option = new JPanel();
        option.setLayout(new FlowLayout());
        btnIngreso = new JButton("OK");
        option.add(btnIngreso);

        btnIngreso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userName = "";
                String userPassword = "";
                if(txtUserName.getText().isEmpty() || txtUserPassword.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(myFrame, "invalid user or password", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    userName = txtUserName.getText();
                    userPassword = txtUserPassword.getText();
                    String condition = "nombre: " + userName + ", password: " + userPassword;
                    User mio = userDAO.FindByColumnName(condition, "and", new UserBuilder());
                    if(mio != null) {
                        myFrame.setVisible(false);
                        new PanelPrincipal(myConfig, mio.getId_pk());
                    } else {
                        JOptionPane.showMessageDialog(myFrame, "invalid user or password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnCancel = new JButton("Cancel");
        option.add(btnCancel);

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(myFrame, "are you sure?", "Exit", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                    System.exit(0);
                }
            }
        });
        return option;
    }

    public void CreateUI(String frameTitle, DbConfig myConfig) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(400, 200);
        myFrame.setLayout(new BorderLayout());

        myFrame.add(LoginContent(), BorderLayout.CENTER);
        myFrame.add(LoginOptions(myConfig), BorderLayout.SOUTH);


        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.setResizable(false);
    }
}
