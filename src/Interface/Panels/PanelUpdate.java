package Interface.Panels;

import java.awt.GridLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Mundo.Cuentas.Cuenta;

public class PanelUpdate {

    public PanelUpdate(String frameTitle, int hight, int height, Cuenta updateCuenta) {
        CreateUI(frameTitle, hight, height, updateCuenta);
    }
    public JPanel OptionsComponent(Cuenta updateCuenta) {

        JTextField txtUserId = new JTextField();
        txtUserId.setText(String.valueOf(updateCuenta.getUser_id_fk()));
        txtUserId.setEditable(false);

        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(5, 2));
        pOptions.add(new JLabel("Nombre"));
        pOptions.add(new JTextField(updateCuenta.getNombre()));
        pOptions.add(new JLabel("Email"));
        pOptions.add(new JTextField(updateCuenta.getEmail()));
        pOptions.add(new JLabel("User_id"));
        pOptions.add(txtUserId);
        pOptions.add(new JLabel("Password"));
        pOptions.add(new JTextField(updateCuenta.getPassword()));
        return pOptions;
    }
    public JPanel OperationsComponent() {
        // TODO: add functiolality of ok and cancel button
        JPanel pOperations = new JPanel();
        pOperations.setLayout(new FlowLayout());
        JButton oKButton = new JButton("OK");
        pOperations.add(oKButton);
        JButton cancelButton = new JButton("Cancel");
        pOperations.add(cancelButton);
        return pOperations;
    }
    public void CreateUI(String frameTitle, int hight, int height, Cuenta updateCuenta) {
        JFrame myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));

        JLabel headerLabel = new JLabel("Update", JLabel.CENTER);

        JPanel pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 1));

        pPrincipal.add(OptionsComponent(updateCuenta));
        pPrincipal.add(OperationsComponent());

        myFrame.add(headerLabel);
        myFrame.add(pPrincipal);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
