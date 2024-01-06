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
        CreateUI("Loggin");
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

    private JPanel LoginOptions() {

        JPanel option = new JPanel();
        option.setLayout(new FlowLayout());
        btnIngreso = new JButton("OK");
        option.add(btnIngreso);

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

    public void CreateUI(String frameTitle) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(400, 200);
        myFrame.setLayout(new BorderLayout());

        myFrame.add(LoginContent(), BorderLayout.CENTER);
        myFrame.add(LoginOptions(), BorderLayout.SOUTH);


        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.setResizable(false);
    }
}
