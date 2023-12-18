package Interface.Panels;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import Conexion.Query.QueryDAO;
import Config.DbConfig;
import Mundo.Cuentas.Cuenta;
import Mundo.Cuentas.CuentaBuilder;
import Utils.QueryUtils;



public class PanelPrincipal {

    private JTable mTable;
    private JFrame myFrame;
    private JLabel headerLabel;
    private JPanel controlPanel;
    private QueryUtils queryUtils;
    private QueryDAO<Cuenta> cuentaDAO;

    public PanelPrincipal(DbConfig myConfig) {
        queryUtils = new QueryUtils();
        cuentaDAO = new QueryDAO<>("cuenta", myConfig);
        CreateUI("table example", "Gestor Password", 960, 540, myConfig);
    }
    public Object[][] TableContent(String[] columns) {
        ArrayList<Cuenta> cuentaList = cuentaDAO.ReadAll(new CuentaBuilder());
        String results = "";
        for(Cuenta miCuenta: cuentaList) {
            if(miCuenta.getUpdate_at() != null && miCuenta.getUpdate_at().isEmpty() == false) {
                results += queryUtils.GetModelType(miCuenta.GetAllProperties(), true) + "\n";
            } else {
                results += queryUtils.GetModelType(miCuenta.GetAllProperties(), true).replace("'", "") + ",null\n";
            }
        }
        String[] datos = results.split("\n");
        Object[][] data = new String[datos.length][columns.length];
        for(int i=0; i<datos.length; ++i) {
            String[] mData = datos[i].split(",");
            data[i] = mData;

        }
        return data;
    }
    public JPanel TableComponent(String tableText) {
        headerLabel.setText(tableText);
        String modelColumns = queryUtils.GetModelColumns(new Cuenta().InitModel(), true);

        String[] columns = modelColumns.split(",");

        mTable = new JTable(TableContent(columns), columns);
        JScrollPane scroll = new JScrollPane(mTable);
        scroll.setSize(300, 300);
        mTable.setFillsViewportHeight(true);
        controlPanel.add(scroll);
        return controlPanel;
    }
    public JPanel OptionsComponent(DbConfig myConfig, int hight, int height) {
        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new PanelRegistro("Register", hight, height, myConfig);
                myFrame.setVisible(false);
            }
        });

        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new FlowLayout());
        optionPanel.add(insertButton);
        optionPanel.add(updateButton);
        optionPanel.add(deleteButton);
        return optionPanel;
    }
    public void CreateUI(String frameTitle, String tableTitle, int hight, int height, DbConfig myConfig) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));
        myFrame.setLocationRelativeTo(null);

        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        
        headerLabel = new JLabel("", JLabel.CENTER);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        myFrame.add(headerLabel);
        myFrame.add(TableComponent(tableTitle));
        myFrame.add(OptionsComponent(myConfig, hight - 300, height - 100));
        
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
