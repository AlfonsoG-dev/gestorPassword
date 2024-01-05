package Interface.Panels;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import Conexion.Query.QueryDAO;
import Config.DbConfig;
import Mundo.Cuentas.Cuenta;
import Mundo.Cuentas.CuentaBuilder;
import Utils.QueryUtils;



public class PanelPrincipal {

    private JTable mTable;
    private DefaultTableModel tableModel;
    private JFrame myFrame;
    private JLabel headerLabel;
    private JPanel controlPanel;
    private QueryUtils queryUtils;
    private QueryDAO<Cuenta> cuentaDAO;

    public PanelPrincipal(DbConfig myConfig) {
        queryUtils = new QueryUtils();
        cuentaDAO = new QueryDAO<>("cuenta", myConfig);
        CreateUI("table example", "Gestor Password", 1100, 540, myConfig);
    }
    private Object[][] TableContent(String[] columns) {
        ArrayList<Cuenta> cuentaList = cuentaDAO.ReadAll(new CuentaBuilder());
        String results = "";
        for(Cuenta miCuenta: cuentaList) {
            if(miCuenta.getUpdate_at() != null && miCuenta.getUpdate_at().isEmpty() == false) {
                results += queryUtils.GetModelType(miCuenta.GetAllProperties(), true).replace("'", "") + "\n";
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
    private JPanel TableComponent(String tableText) {

        headerLabel.setText(tableText);
        String modelColumns = queryUtils.GetModelColumns(new Cuenta().InitModel(), true);

        String[] columns = modelColumns.split(",");
        
        tableModel = new DefaultTableModel(TableContent(columns), columns);
        mTable = new JTable(tableModel);

        mTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mTable.sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scroll = new JScrollPane(mTable);
        scroll.setSize(300, 300);
        mTable.setFillsViewportHeight(true);
        controlPanel.add(scroll);
        return controlPanel;
    }
    private JPanel TableOptionComponents() {
        JPanel tableOptions = new JPanel();
        tableOptions.setLayout(new GridLayout(2, 1));

        JButton agregar = new JButton("+");
        tableOptions.add(agregar);
        agregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] data = {"", "", "", "", "", "", ""};
                tableModel.addRow(data);
            }
        });


        JButton eliminar = new JButton("-");
        tableOptions.add(eliminar);
        eliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int tableSize = mTable.getRowCount()-1;
                String cNombre = mTable.getValueAt(tableSize, 1).toString();
                if(cNombre.isEmpty() || cNombre.isBlank()) {
                    tableModel.removeRow(tableSize);
                } else {
                    JOptionPane.showMessageDialog(myFrame, "Cannot remove!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return tableOptions;
    }
    private Cuenta BuildCuentaFromTable(int row, int column, DbConfig myConfig) {
        String columName = mTable.getColumnName(column);
        String options = columName + ": " + mTable.getValueAt(row, column).toString();
        Cuenta myCuenta = cuentaDAO.FindByColumnName(options, "or", new CuentaBuilder());
        return myCuenta;
    }
    private void DeleteButtonHandler(JButton deleteButton, DbConfig myConfig) {
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = mTable.getSelectedRow();
                int column = mTable.getSelectedColumn();
                String columName = mTable.getColumnName(column);
                try {
                    if(columName.equals("create_at") || columName.equals("update_at") || columName.equals("password")) {
                        JOptionPane.showMessageDialog(myFrame, "to delete use 'ID' or 'nombre' or 'email' or 'FK' ", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if(mTable.getSelectedRow() != -1 && JOptionPane.showConfirmDialog(myFrame, "Do you want to remove?", "Remove operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION && row != -1 && column != -1) {
                        String options = columName + ": " + mTable.getValueAt(row, column).toString();
                        tableModel.removeRow(row);
                        cuentaDAO.EliminarRegistro(options, "and", new CuentaBuilder());
                    } else {
                        JOptionPane.showMessageDialog(myFrame, "NO TABLE ELEMENT SELECTED");
                    }
                } catch(Exception er) {
                    System.err.println(er);
                }
            }
        });
    }
    private JPanel OptionsComponent(DbConfig myConfig, int hight, int height) {
        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO: if 1 or more rows are not present in the database the click event insert the new data
                // other wise redirects to PanelRegistro
                new PanelRegistro("Register", hight, height, myConfig);
                myFrame.setVisible(false);
            }
        });

        // TODO: if 1 or more columns of row are edited the click event updates the data doesn't redirect to PanelUpdate
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = mTable.getSelectedRow();
                int column = mTable.getSelectedColumn();
                String columName = mTable.getColumnName(column);
                if(columName.equals("create_at") || columName.equals("update_at") || columName.equals("password")) {
                        JOptionPane.showMessageDialog(myFrame, "to update use 'ID' or 'nombre' or 'email' or 'FK' ", "Error", JOptionPane.ERROR_MESSAGE);
                } else if(row != -1 || column != -1) {
                    Cuenta updateCuenta = BuildCuentaFromTable(row, column, myConfig);
                    new PanelUpdate("Update", hight, height, updateCuenta, myConfig);
                    myFrame.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(myFrame, "NO TABLE ELEMENT SELECTED");
                }
            }
        });


        JButton deleteButton = new JButton("Delete");
        DeleteButtonHandler(deleteButton, myConfig);

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

        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        
        headerLabel = new JLabel("", JLabel.CENTER);

        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(TableOptionComponents(), BorderLayout.EAST);

        myFrame.add(headerLabel);
        myFrame.add(TableComponent(tableTitle), BorderLayout.CENTER);
        myFrame.add(OptionsComponent(myConfig, 600, 700));
        
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setResizable(true);
    }
}
