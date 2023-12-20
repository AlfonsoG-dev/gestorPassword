package Interface.Panels;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Config.DbConfig;
import Mundo.Cuentas.Cuenta;

public class PanelUpdate {

    private JFrame myFrame;
    private JPanel pPrincipal;
    private JTextField txtIdPk;
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JTextField txtUserId;
    private JTextField txtPassword;

    public PanelUpdate(String frameTitle, int hight, int height, Cuenta updateCuenta, DbConfig myConfig) {
        CreateUI(frameTitle, hight, height, updateCuenta, myConfig);
    }
    private JPanel OptionsComponent(Cuenta updateCuenta) {

        txtUserId = new JTextField(String.valueOf(updateCuenta.getUser_id_fk()));
        txtUserId.setEditable(false);

        txtIdPk = new JTextField(String.valueOf(updateCuenta.getId_pk()));
        txtIdPk.setEditable(false);

        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(5, 2));
        pOptions.add(new JLabel("ID_pk"));
        pOptions.add(txtIdPk);
        pOptions.add(new JLabel("Nombre"));
        pOptions.add(txtNombre = new JTextField(updateCuenta.getNombre()));
        pOptions.add(new JLabel("Email"));
        pOptions.add(txtEmail = new JTextField(updateCuenta.getEmail()));
        pOptions.add(new JLabel("User_Id_fk"));
        pOptions.add(txtUserId);
        pOptions.add(new JLabel("Password"));
        pOptions.add(txtPassword = new JTextField(updateCuenta.getPassword()));
        return pOptions;
    }

    private void OkButtonHandler(JButton OKButton, DbConfig myConfig) {
        String registro = "";
    }
    private void CancelButtonHandler(JButton cancelButton, DbConfig myConfig) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(myFrame, "Do you want to cancel?", "Register operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                    myFrame.setVisible(false);
                    new PanelPrincipal(myConfig);
                }
            }
        });
    }
    private JPanel OperationsComponent(DbConfig myConfig) {
        // TODO: add functiolality of ok and cancel button
        JPanel pOperations = new JPanel();
        pOperations.setLayout(new FlowLayout());
        JButton oKButton = new JButton("OK");
        pOperations.add(oKButton);
        JButton cancelButton = new JButton("Cancel");
        pOperations.add(cancelButton);
        CancelButtonHandler(cancelButton, myConfig);
        return pOperations;
    }
    public void CreateUI(String frameTitle, int hight, int height, Cuenta updateCuenta, DbConfig myConfig) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));

        JLabel headerLabel = new JLabel("Update", JLabel.CENTER);

        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 1));

        pPrincipal.add(OptionsComponent(updateCuenta));
        pPrincipal.add(OperationsComponent(myConfig));

        myFrame.add(headerLabel);
        myFrame.add(pPrincipal);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
