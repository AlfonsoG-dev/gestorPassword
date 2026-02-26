package application.interfaces.panels;

import java.sql.Connection;

import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import application.interfaces.utils.FileUtils;
import application.interfaces.utils.PanelUtils;
import application.models.cuenta.CuentaModel;
import application.models.cuenta.CuentaODM;
import orm.utils.formats.DbConfig;
import orm.utils.formats.ParamValue;

public class PanelPrincipal {
    private static final String USER_FK_STRING = "user_id_fk";
    /**
     * the data table for the panel
     */
    private JTable mTable;
    /**
     * the model for the table
     */
    private DefaultTableModel tableModel;
    /**
     * the panel frame
     */
    private JFrame myFrame;
    /**
     * the main frame of login
     */
    private JFrame mainFrame;
    /**
     * label that describes the panel
     */
    private JLabel headerLabel;
    /**
     * panel of options
     */
    private JPanel controlPanel;
    /**
     * class for account management
     */
    private PanelUtils<CuentaModel> cuentaUtils;
    /**
     * database configuration
     */
    private DbConfig myConfig;
    /**
     * user that had been logged
     */
    private int loggedUser;
    /**
     * Connection instance for transaction use
     */
    private Connection cursor;
    /**
     * constructor
     */
    public PanelPrincipal(DbConfig mConfig, int pLoggedUser, JFrame nMainFrame, Connection miConnection, PanelUtils<CuentaModel> nCuentaUtils) {
        myConfig    = mConfig;
        loggedUser  = pLoggedUser;
        cursor      = miConnection;
        cuentaUtils = nCuentaUtils;
        mainFrame   = nMainFrame;
        // set the save point to rollback or commit changes
        try {
            cursor.setAutoCommit(false);
        } catch(Exception e) {
            e.printStackTrace();
            cuentaUtils.errorMessage(null, "error while trying to create the connection to DB", "Connection Error");
        }
    }
    /**
     * list of cuentas that verify the user_id_fk with the loggedUser
     * @return the cuentas with the same user_id_fk
     */
    private List<CuentaModel> misCuentas() {
        List<CuentaModel> nuevas = new ArrayList<>();
        List<CuentaModel> nCuentas = cuentaUtils.myDataList();
        if(!nCuentas.isEmpty()) {
            for(CuentaModel model: nCuentas) {
                if(model.getUser_id_fk() == loggedUser) {
                    nuevas.add(model);
                }
            }
        }
        return nuevas;
    }
    /**
     * creates the table content from database
     * @param columns: the columns of the table cuenta
     * @return the table content like Object[][]
     */
    private Object[][] tableContent(String[] columns) {
        List<CuentaModel> cuentaList = misCuentas();
        StringBuilder results = new StringBuilder();
        for(CuentaModel miCuenta: cuentaList) {
            if(miCuenta.getUpdate_at() != null && !miCuenta.getUpdate_at().isEmpty()) {
                results.append(cuentaUtils.getModelType(miCuenta).replace("'", ""));
                results.append("\n");
            } else if(miCuenta.getUpdate_at() == null) {
                results.append(cuentaUtils.getModelType(miCuenta).replace("'", ""));
                results.append(",null\n");
            }
        }
        String[] datos = results.toString().split("\n");
        Object[][] data = new String[datos.length][columns.length];
        for(int i=0; i<datos.length; ++i) {
            String[] mData = datos[i].split(",");
            data[i] = mData;
        }
        return data;
    }
    /**
     * list the created cuentas from table that are not present in the database
     * the list of cuentas only works when you insert a new row with its content directly in the table
     * @return the list of cuentas that are not present in the database
     */
    private List<CuentaODM> listaFaltantes() {
        List<CuentaODM> faltante = new ArrayList<>();
        int rows = mTable.getRowCount();
        List<CuentaModel> nCuentas = misCuentas();
        CuentaODM mia = null;
        if(rows > nCuentas.size()) {
            for(int i=0; i<rows; ++i) {
                String cNombre   = mTable.getValueAt(i, 1).toString();
                String cEmail    = mTable.getValueAt(i, 2).toString();
                String cUserFk   = mTable.getValueAt(i, 3).toString();
                String cPassword = mTable.getValueAt(i, 4).toString();
                if(cNombre.isBlank() || cEmail.isBlank() || cPassword.isBlank()) {
                    cuentaUtils.errorMessage(myFrame, "invalid empty fields", "Table Error");
                    break;
                }
                String[] c = {"nombre", USER_FK_STRING};
                String[] v = {cNombre, cUserFk};
                ParamValue condition = new ParamValue(c, v, "and");
                List<CuentaModel> buscada = cuentaUtils.findOperation(condition);
                if(buscada.isEmpty()) {
                    mia = new CuentaODM(cNombre, cEmail, Integer.parseInt(cUserFk), cPassword);
                    mia.makeCreate_at();
                    faltante.add(mia);
                }
            }
        }
        return faltante;
    }
    private void allowCopyToClipBoard(int row, int column) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Object value = mTable.getValueAt(row, column);
        String msg = String.format("%s has been copied to system clip board.", mTable.getColumnName(column));
        cuentaUtils.infoMessage(myFrame, msg, "Copy");
        StringSelection selection = new StringSelection(value.toString());
        clip.setContents(selection, null);
    }
    /**
     * sets the panel with the table component and its content
     * @param tableText: table component title
     */
    private JPanel tableComponent(String tableText) {

        headerLabel.setText(tableText);
        String[] columns = cuentaUtils.getModelColumn(new CuentaModel());

        tableModel = new DefaultTableModel(tableContent(columns), columns);
        mTable = new JTable(tableModel);
        mTable.setDragEnabled(true);
        mTable.setDropMode(DropMode.INSERT_ROWS);
        mTable.setTransferHandler(new TableTransferable());
        mTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    int row    = mTable.rowAtPoint(e.getPoint());
                    int column = mTable.columnAtPoint(e.getPoint());
                    if(row != -1 && column != -1) {
                        allowCopyToClipBoard(row, column);
                    }
                }
            }
        });
        mTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override 
            public void mouseMoved(MouseEvent e) {
                int column = mTable.columnAtPoint(e.getPoint());
                int row = mTable.rowAtPoint(e.getPoint());
                if(row != -1 && column != -1) {
                    mTable.setToolTipText("Hover over cell: (" + row + ", " + column + ")");
                } else{
                    mTable.setToolTipText(null);
                }
            }
        });
        /** 
         * disable the PK and FK columns for edition.
         * 1. id_pk
         * 2. user_id_fk
         * 3. create_at
         * 4. update_at
         */
        mTable.getColumnModel().getColumn(0).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(3).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(5).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(6).setCellEditor(new NonEditableCell());

        JScrollPane scroll = new JScrollPane(mTable);
        scroll.setSize(300, 300);
        mTable.setFillsViewportHeight(true);
        controlPanel.add(scroll);
        return controlPanel;
    }
    /**
     * changes the data for the table model, making a request to the database
     */
    private void setNewDataTableModel() {
        String[] columns = cuentaUtils.getModelColumn(new CuentaModel());
        Object[][] contenido = tableContent(columns);
        tableModel = new DefaultTableModel(contenido, columns);
        mTable.setModel(tableModel);

        /** 
         * disable the PK and FK columns for edition.
         * 0. id_pk
         * 3. user_id_fk
         * 5. create_at
         * 6. update_at
         */
        mTable.getColumnModel().getColumn(0).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(3).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(5).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(6).setCellEditor(new NonEditableCell());
    }
    /**
     * set the panel with the options to manipulate the table like add rows, delete rows or reload
     * @return the panel with the table options
     */
    private JPanel tableOptionComponents() {
        JPanel tableOptions = new JPanel();
        tableOptions.setLayout(new GridLayout(3, 1));

        JButton reloadButton = new JButton("R");
        tableOptions.add(reloadButton);
        // reload the table content
        reloadButton.addActionListener(e -> {
            try {
                int option = JOptionPane.showConfirmDialog(myFrame,
                        "apply changes before reload?", "REALOAD", JOptionPane.YES_NO_CANCEL_OPTION);
                if(option == JOptionPane.YES_OPTION) {
                    cursor.commit();
                    setNewDataTableModel();
                } else if(option == JOptionPane.NO_OPTION) {
                    cursor.rollback();
                    cuentaUtils.setAutoImcrement();
                    setNewDataTableModel();
                } else if(option == JOptionPane.CANCEL_OPTION) {
                    // do nothing
                }
            } catch(Exception er) {
                er.printStackTrace();
                cuentaUtils.errorMessage(myFrame, "Error while trying to reload the data", "Reload Error");
            }
        });

        JButton agregarButton = new JButton("+");
        tableOptions.add(agregarButton);
        // add a new row for the table
        agregarButton.addActionListener(e -> {
            String[] columns = {
                "", "", "",
                String.valueOf(loggedUser),
                "", "", ""
            };
            tableModel.addRow(columns);
        });


        JButton eliminarButton = new JButton("-");
        tableOptions.add(eliminarButton);
        // delete the row from the table
        eliminarButton.addActionListener(e -> {
            int tableSize   = mTable.getRowCount()-1;
            int selectedRow = mTable.getSelectedRow();
            String cNombre = mTable.getValueAt(tableSize, 1).toString();
            if(cNombre.isEmpty()) {
                tableModel.removeRow(tableSize);
            } else if(selectedRow != -1 && mTable.getValueAt(selectedRow, 1).toString().isEmpty()) {
                tableModel.removeRow(selectedRow);
            } else {
                cuentaUtils.errorMessage(myFrame, "Cannot remove!", "Delete Error");
            }
        });
        return tableOptions;
    }
    public JPanel filePanelOperation(int width, int height) {
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new GridLayout(2, 1));

        JButton importButton = new JButton("I");
        filePanel.add(importButton);
        importButton.addActionListener(e -> {
            myFrame.setEnabled(false);
            ImportPanel iPanel = new ImportPanel(myFrame, loggedUser, tableModel);
            iPanel.createUI(width, height);
        });

        JButton exportButton = new JButton("E");
        filePanel.add(exportButton);
        exportButton.addActionListener(e -> {
            String filePath = JOptionPane.showInputDialog(myFrame,
                    null, "write the path where you want to save.", JOptionPane.INFORMATION_MESSAGE);
            if(filePath != null) {
                String fileName = JOptionPane.showInputDialog(myFrame,
                        null, "write the name of the file.", JOptionPane.INFORMATION_MESSAGE);
                if(fileName != null) {
                    FileUtils.exportSaveData(filePath, fileName, misCuentas());
                }
            }
        });

        return filePanel;
    }
    /**
     * implements the action handler for the delete button.
     * <br> pre: </br> only works if the selected table column is not: create_at, update_at or password
     * @param deleteButton: panel deleteButton to delte or truncate the cuenta for the database
     */
    private void deleteButtonHandler(JButton deleteButton) {
        deleteButton.addActionListener(e -> {
            int row = mTable.getSelectedRow();
            int column = mTable.getSelectedColumn();
            int option = JOptionPane.showConfirmDialog(myFrame,
                    "Do you want to remove?", "Remove operation",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            String columName = mTable.getColumnName(column);
            if(columName.equals("create_at") || columName.equals("update_at") || columName.equals("password")) {
                cuentaUtils.errorMessage(myFrame,
                        "to delete use 'ID' or 'nombre' or 'email' or 'FK' ", "Option Error");
            } else if(mTable.getSelectedRow() != -1 && option == JOptionPane.OK_OPTION && row != -1 && column != -1) {
                String valueOfColumn = mTable.getValueAt(row, column).toString();
                String[] c = {columName, USER_FK_STRING};
                String[] v = {valueOfColumn, mTable.getValueAt(row, 3).toString()};
                ParamValue condition = new ParamValue(c, v, "and");
                boolean eliminado = cuentaUtils.deleteOperation(condition);
                if(eliminado) {
                    tableModel.removeRow(row);
                } else {
                    cuentaUtils.errorMessage(myFrame,
                            String.format("Column: %s with value of: %s not found", columName, valueOfColumn),
                            "Not fount Error");
                }
            } else {
                cuentaUtils.errorMessage(myFrame, "NO TABLE ELEMENT SELECTED", "Select Error");
            }
        });
    }
    /**
     * implements the action handler for the insert button.
     * <br> post: </br> when the table have ListaFaltantes.size() > 0 insert the data, otherwise redirects to PanelRegistro
     * @param insertButton: panel insertButton to register a new cuenta for the database
     * @param width: width of the PanelRegistro
     * @param height: height of the PanelRegistro
     */
    private void insertButtonHandler(JButton insertButton, int width, int height) {
        insertButton.addActionListener(e -> {
            if(!listaFaltantes().isEmpty()) {
                PanelRegistro rPanel = new PanelRegistro(myConfig, loggedUser, cursor, myFrame, cuentaUtils);
                rPanel.createUI("Register", width/2, height-100);
                myFrame.setEnabled(false);
            } else {
                try {
                    for(CuentaODM c: listaFaltantes()) {
                        int option = JOptionPane.showConfirmDialog(myFrame,
                                "Do you want to register?", "Register operation",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(option == JOptionPane.OK_OPTION) {
                            cuentaUtils.insertOperation(c);
                        }
                    }
                } catch(Exception er) {
                    er.printStackTrace();
                    cuentaUtils.errorMessage(myFrame, "Error while trying to inser a register", "Insert Error");
                } finally {
                    cuentaUtils.infoMessage(myFrame, "reload the window to see the changes", "INFO");
                }
            }
        });
    }
    /**
     * implements the action handler for the updateButton.
     * <br> post: </br> redirects to PanelUpdate
     * @param updateButton: panel button to update the cuenta
     * @param width: width of the PanelUpdate
     * @param height: height of the PanelUpdate
     */
    private void updateButtonHandler(JButton updateButton, int width, int height) {
        updateButton.addActionListener(e -> {
            int row    = mTable.getSelectedRow();
            int column = mTable.getSelectedColumn();
            String columName = mTable.getColumnName(column);
            if(columName.equals("create_at") || columName.equals("update_at") || columName.equals("password")) {
                    cuentaUtils.errorMessage(myFrame,
                            "to update use 'ID' or 'nombre' or 'email' or 'FK' ", "Error");
            } else if(row != -1 || column != -1) {
                CuentaModel updateCuenta = cuentaUtils.buildObjectFromTable(
                        row, column, loggedUser, mTable);
                if(updateCuenta != null) {
                    PanelUpdate pUpdate = new PanelUpdate(updateCuenta, myFrame, cuentaUtils);
                    pUpdate.createUI("Update", width/2, height-100, updateCuenta);
                    myFrame.setEnabled(false);
                }
            } else {
                cuentaUtils.errorMessage(myFrame, "NO TABLE ELEMENT SELECTED", "Error");
            }
        });
    }
    /**
     * implements the action hanlder for cancelButton
     * <br> post: </br> rollback to savepoint and redirects to Login or register if its the firts time
     * @param cancelButton: panel button to cancel the operation
     */
    private void cancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(e -> {
            try {
                int option = JOptionPane.showConfirmDialog(myFrame,
                        "Go back to login", "Cancel op", JOptionPane.OK_CANCEL_OPTION);
                if(option == JOptionPane.OK_OPTION) {
                    mainFrame.setVisible(true);
                    cursor.rollback();
                    cuentaUtils.setAutoImcrement();
                    myFrame.dispose();
                }
            } catch(Exception er) {
                er.printStackTrace();
                cuentaUtils.errorMessage(myFrame, "Error while trying to cancel the operation", "Cancel Error");
            }
        });
    }
    /**
     * panel that its content if the panel buttons and its action handlers
     * @param width: width of the panel
     * @param height: height of the panel
     * @return the panel with the content setted
     */
    private JPanel optionsComponent(int height) {
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new FlowLayout());

        JButton insertButton = new JButton("Insert");
        insertButtonHandler(insertButton, height, height);
        optionPanel.add(insertButton);

        JButton updateButton = new JButton("Update");
        updateButtonHandler(updateButton, height, height);
        optionPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButtonHandler(deleteButton);
        optionPanel.add(deleteButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButtonHandler(cancelButton);
        optionPanel.add(cancelButton);

        return optionPanel;
    }
    /**
     * creates the ui for the current frame
     * @param frameTitle: the frame title
     * @param tableTitle: the table name
     * @param width: the frame width
     * @param height: the frame height
     */
    public void createUI(String frameTitle, String tableTitle, int width, int height) {

        if(misCuentas().isEmpty()) {
            PanelRegistro rPanel = new PanelRegistro(myConfig, loggedUser, cursor, myFrame, cuentaUtils);
            rPanel.createUI("Register", 400, 900);
        }
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(width, height);
        myFrame.setLayout(new GridLayout(3, 1));

        myFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                try {
                    int option = JOptionPane.showConfirmDialog(myFrame,
                            "save changes before exit?", "save changes", JOptionPane.YES_NO_CANCEL_OPTION);
                    if(option == JOptionPane.YES_OPTION) {
                        cursor.commit();
                        System.exit(0);
                    } else if(option == JOptionPane.NO_OPTION) {
                        cursor.rollback();
                        cuentaUtils.setAutoImcrement();
                        System.exit(0);
                    } else if(option == JOptionPane.CANCEL_OPTION) {
                        myFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    cuentaUtils.errorMessage(myFrame, "Error while trying to close the window", "Close Error");
                }
            }
        });

        headerLabel = new JLabel("", SwingConstants.CENTER);

        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(tableOptionComponents(), BorderLayout.EAST);
        controlPanel.add(filePanelOperation(width, height), BorderLayout.WEST);

        myFrame.add(headerLabel);
        myFrame.add(tableComponent(tableTitle), BorderLayout.CENTER);
        myFrame.add(optionsComponent(700));
        myFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        myFrame.setVisible(true);
        myFrame.setLocationRelativeTo(mainFrame);
        myFrame.setResizable(true);
    }
}

/**
 * helper class to disable cell edition
 */
@SuppressWarnings({"serial"})
class NonEditableCell extends DefaultCellEditor {

    public NonEditableCell() {
        super(new JTextField());
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return false;
    }
}
/**
 * helper class to enable drag and drop for the table
 */
@SuppressWarnings({"serial"})
class TableTransferable extends TransferHandler {
    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        JTable table = (JTable) support.getComponent();
        Transferable transferable = support.getTransferable();

        try {
            String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            // the data must have this format: id,nombre,email,user_id_fk,password,null,null
            ((DefaultTableModel) table.getModel()).addRow(data.split(","));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
