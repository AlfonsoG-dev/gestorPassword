package Interface.Panels;

import java.sql.Connection;

import java.util.ArrayList;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Conexion.Query.QueryDAO;
import Config.DbConfig;
import Mundo.Cuentas.Cuenta;
import Mundo.Cuentas.CuentaBuilder;
import Mundo.Users.User;
import Mundo.Users.UserBuilder;

public class PanelRegistro {
    private JFrame myFrame;
    private QueryDAO<User> userDAO;
    private int loggedUser;
    private JLabel headerLabel;
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JTextField txtPassword;
    private JComboBox<String> cbxUser;
    private JPanel pPrincipal;
    private Connection cursor;
    private JFrame mainFrame;
    public PanelRegistro(String frameTitle, int hight, int height, DbConfig myConfig, int pLoggedUser, Connection miCursor, JFrame nMainFrame) {
        loggedUser = pLoggedUser;
        cursor = miCursor;
        mainFrame = nMainFrame;
        userDAO = new QueryDAO<User>("user", myConfig);
        CreateUI(frameTitle, hight, height, myConfig);
    }
    private String[] ComboBoxUsers() {
        String result = "select a user...,";
        ArrayList<User> users = userDAO.ReadAll(new UserBuilder());
        for(User u: users) {
            if(u.getId_pk() == loggedUser) {
                result += u.getNombre() + ",";
            }
        }
        return result.split(",");
    }
    private JPanel OptionsComponent() {
        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(4, 2));
        pOptions.add(new JLabel(" Nombre"));
        pOptions.add(txtNombre = new JTextField());
        pOptions.add(new JLabel(" Email"));
        pOptions.add(txtEmail = new JTextField());
        pOptions.add(new JLabel(" User_id"));
        pOptions.add(cbxUser = new JComboBox<String>(ComboBoxUsers()));
        pOptions.add(new JLabel(" Password"));
        pOptions.add(txtPassword = new JTextField());

        return pOptions;
    }
    // inserts a new element to the database
    private void OkButtonHandler(JButton OKButton, DbConfig myConfig) {
        QueryDAO<Cuenta> cuentaDAO = new QueryDAO<Cuenta>("cuenta", myConfig);
        OKButton.setMnemonic(KeyEvent.VK_ENTER);
            OKButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        String nombre = txtNombre.getText();
                        String email = txtEmail.getText();
                        String selectedUser = cbxUser.getSelectedItem().toString();
                        int selectedUserId = userDAO.FindByColumnName("nombre: " + selectedUser, "and", new UserBuilder()).getId_pk();
                        String password = txtPassword.getText();
                        Cuenta nueva = new Cuenta(nombre, email, selectedUserId, password);
                        nueva.setCreate_at();
                        String condition = "nombre: "  + nueva.getNombre() + ", user_id_fk" + nueva.getUser_id_fk();
                        if(JOptionPane.showConfirmDialog(myFrame, "Do you want to register?", "Register operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                            cuentaDAO.InsertNewRegister(nueva, condition, "and", new CuentaBuilder(), cursor);
                            mainFrame.setEnabled(true);
                            myFrame.setVisible(false);
                        }
                    } catch(Exception ex) {
                        System.out.println(ex);
                    } finally {
                        JOptionPane.showMessageDialog(myFrame, "reload the window to see the changes", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
    }
    private void CancelButtonHandler(JButton cancelButton, DbConfig myConfig) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(myFrame, "Do you want to cancel?", "Register operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                    mainFrame.setEnabled(true);
                    myFrame.setVisible(false);
                }
            }
        });
    }
    public void CreateUI(String frameTitle,int hight, int height, DbConfig myConfig) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));

        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                mainFrame.setEnabled(true);
                myFrame.setVisible(false);
            }
        });

        headerLabel = new JLabel("Register", JLabel.CENTER);

        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 1));

        pPrincipal.add(OptionsComponent());

        JPanel options = new JPanel();
        options.setLayout(new FlowLayout());

        JButton OKButton = new JButton("OK");
        options.add(OKButton);
        OkButtonHandler(OKButton, myConfig);

        JButton cancelButton = new JButton("cancel");
        options.add(cancelButton);
        CancelButtonHandler(cancelButton, myConfig);
        pPrincipal.add(options);

        myFrame.add(headerLabel);
        myFrame.add(pPrincipal);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
