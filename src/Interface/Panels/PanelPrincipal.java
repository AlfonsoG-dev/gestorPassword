package Interface.Panels;

import Interface.Utils.PanelUtils;
import Interface.Utils.FileUtils;

import java.sql.Connection;

import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
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
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

import Config.DbConfig;

import Mundo.Cuentas.Cuenta;
import Utils.Formats.ParamValue;

public class PanelPrincipal {
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
    private PanelUtils<Cuenta> cuentaUtils;
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
    public PanelPrincipal(DbConfig mConfig, int pLoggedUser, JFrame nMainFrame, Connection miConnection,
            PanelUtils<Cuenta> nCuentaUtils) {
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
            cuentaUtils.errorMessage(
                    null,
                    "error while trying to create the connection to DB",
                    "Connection Error"
            );
        }

        if(misCuentas().size() > 0) {
            createUI(
                    "table example",
                    "Gestor Password",
                    1100,
                    540
            );
        } else {
            new PanelRegistro(
                    "Register",
                    400,
                    900,
                    myConfig,
                    loggedUser,
                    cursor,
                    myFrame,
                    cuentaUtils
            );
        }
    }
    /**
     * list of cuentas that verify the user_id_fk with the loggedUser
     * @return the cuentas with the same user_id_fk
     */
    private ArrayList<Cuenta> misCuentas() {
        ArrayList<Cuenta> nuevas = new ArrayList<>();
        ArrayList<Cuenta> nCuentas = cuentaUtils.myDataList();
        nCuentas
            .parallelStream()
            .filter(e -> e.getUser_id_fk() == loggedUser)
            .forEach(e -> {
                nuevas.add(e);
            });
        return nuevas;
    }
    /**
     * creates the table content from database
     * @param columns: the columns of the table cuenta
     * @return the table content like Object[][]
     */
    private Object[][] tableContent(String[] columns) {
        ArrayList<Cuenta> cuentaList = misCuentas();
        StringBuffer results = new StringBuffer();
        for(Cuenta miCuenta: cuentaList) {
            if(miCuenta.getUpdate_at() != null && miCuenta.getUpdate_at().isEmpty() == false) {
                results.append(cuentaUtils.getModelType(miCuenta).replace("'", "") + "\n");
            } else if(miCuenta.getUpdate_at() == null) {
                results.append(cuentaUtils.getModelType(miCuenta).replace("'", "") + ",null\n");
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
    private ArrayList<Cuenta> listaFaltantes() {
        ArrayList<Cuenta> faltante = new ArrayList<>();
        int rows = mTable.getRowCount();
        ArrayList<Cuenta> nCuentas = misCuentas();
        Cuenta mia = null;
        if(nCuentas.size() < rows) {
        outter: for(int i=0; i<rows; ++i) {
                String 
                    cNombre   = mTable.getValueAt(i, 1).toString(),
                    cEmail    = mTable.getValueAt(i, 2).toString(),
                    cUserFk   = mTable.getValueAt(i, 3).toString(),
                    cPassword = mTable.getValueAt(i, 4).toString();
                if(cNombre.isEmpty() || cEmail.isEmpty() || cPassword.isEmpty()) {
                    cuentaUtils.errorMessage(
                            myFrame,
                            "invalid empty fields",
                            "Error"
                    );
                    break outter;
                }
                if(cNombre == null || cEmail == null || cPassword == null) {
                    cuentaUtils.errorMessage(
                            myFrame,
                            "invalid empty fields",
                            "Error"
                    );
                    break outter;
                }
                String[] 
                    c = {"nombre", "user_id_fk"},
                    v = {cNombre, cUserFk};
                ParamValue condition = new ParamValue(c, v, "and");
                Cuenta buscada = cuentaUtils.findOperation(condition);
                if(buscada == null) {
                    mia = new Cuenta(
                            cNombre,
                            cEmail,
                            Integer.parseInt(cUserFk),
                            cPassword
                    );
                    mia.setCreate_at();
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

        String[] columns = cuentaUtils.getModelColumn(new Cuenta());
        
        tableModel = new DefaultTableModel(tableContent(columns), columns);
        mTable = new JTable(tableModel);
        mTable.setDragEnabled(true);
        mTable.setDropMode(DropMode.INSERT_ROWS);
        mTable.setTransferHandler(new TableTransferable());
        mTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    int 
                        row    = mTable.rowAtPoint(e.getPoint()),
                        column = mTable.columnAtPoint(e.getPoint());
                    if(row != -1 && column != -1) {
                        allowCopyToClipBoard(row, column);
                    }
                }
            }
        });
        mTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override 
            public void mouseMoved(MouseEvent e) {
                int
                    column = mTable.columnAtPoint(e.getPoint()),
                    row = mTable.rowAtPoint(e.getPoint());
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
        String[] columns     = cuentaUtils.getModelColumn(new Cuenta());
        Object[][] contenido = tableContent(columns);
        tableModel           = new DefaultTableModel(contenido, columns);
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
        reloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int option = JOptionPane.showConfirmDialog(
                            myFrame,
                            "apply changes before reload?",
                            "REALOAD",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );
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
                    cuentaUtils.errorMessage(
                            myFrame,
                            "Error while trying to reload the data",
                            "Reload Error"
                    );
                }
            }
        });

        JButton agregarButton = new JButton("+");
        tableOptions.add(agregarButton);
        // add a new row for the table
        agregarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] columns = {
                    "",
                    "",
                    "",
                    String.valueOf(loggedUser),
                    "",
                    "",
                    ""
                };
                tableModel.addRow(columns);
            }
        });


        JButton eliminarButton = new JButton("-");
        tableOptions.add(eliminarButton);
        // delete the row from the table
        eliminarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int 
                    tableSize   = mTable.getRowCount()-1,
                    selectedRow = mTable.getSelectedRow();
                String cNombre = mTable.getValueAt(tableSize, 1).toString();
                if(cNombre.isEmpty()) {
                    tableModel.removeRow(tableSize);
                } else if(selectedRow != -1 && mTable.getValueAt(selectedRow, 1).toString().isEmpty()) {
                    tableModel.removeRow(selectedRow);
                } else {
                    cuentaUtils.errorMessage(
                            myFrame,
                            "Cannot remove!",
                            "Error"
                    );
                }
            }
        });
        return tableOptions;
    }
    public JPanel filePanelOperation(int width, int height) {
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new GridLayout(2, 1));

        JButton importButton = new JButton("I");
        filePanel.add(importButton);
        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myFrame.setEnabled(false);
                new ImportPanel(myFrame, width, height, loggedUser, tableModel);
            }
        });

        JButton exportButton = new JButton("E");
        filePanel.add(exportButton);
        exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filePath = JOptionPane.showInputDialog(
                        myFrame,
                        null,
                        "write the path where you want to save.",
                        JOptionPane.INFORMATION_MESSAGE
                );
                if(filePath != null) {
                    String fileName = JOptionPane.showInputDialog(
                            myFrame,
                            null,
                            "write the name of the file.",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    if(fileName != null) {
                        FileUtils.exportSaveData(
                                filePath,
                                fileName,
                                misCuentas()
                        );
                    }
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
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int 
                    row    = mTable.getSelectedRow(),
                    column = mTable.getSelectedColumn(),
                    option = JOptionPane.showConfirmDialog(
                            myFrame,
                            "Do you want to remove?",
                            "Remove operation",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                String columName = mTable.getColumnName(column);
                try {
                    if(columName.equals("create_at") || columName.equals("update_at") || columName.equals("password")) {
                        cuentaUtils.errorMessage(
                                myFrame,
                                "to delete use 'ID' or 'nombre' or 'email' or 'FK' ",
                                "Error"
                        );
                    } else if(mTable.getSelectedRow() != -1 && option == JOptionPane.OK_OPTION && row != -1 && column != -1) {
                        String valueOfColumn = mTable.getValueAt(row, column).toString();
                        String[]
                            c = {columName, "user_id_fk"},
                            v = {valueOfColumn, mTable.getValueAt(row, 3).toString()};
                        ParamValue condition = new ParamValue(c, v, "and");
                        boolean eliminado = cuentaUtils.deleteOperation(condition);
                        if(eliminado == true) {
                            tableModel.removeRow(row);
                        } else {
                            cuentaUtils.errorMessage(
                                    myFrame,
                                    String.format("Column: %s with value of: %s not found"),
                                    "Error"
                            );
                        }
                    } else {
                        cuentaUtils.errorMessage(
                                myFrame,
                                "NO TABLE ELEMENT SELECTED",
                                "Error"
                        );
                    }
                } catch(Exception er) {
                    er.printStackTrace();
                    cuentaUtils.errorMessage(
                            myFrame,
                            "Error while trying to delete a register",
                            "Delete Error"
                    );
                }
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
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(listaFaltantes().size() == 0) {
                    new PanelRegistro(
                            "Register",
                            width/2,
                            height-100,
                            myConfig,
                            loggedUser,
                            cursor,
                            myFrame,
                            cuentaUtils
                    );
                    myFrame.setEnabled(false);
                } else {
                    try {
                        for(Cuenta c: listaFaltantes()) {
                            String[]
                                co = {"nombre", "user_id_fk"},
                                va = {c.getNombre(), String.valueOf(c.getUser_id_fk())};
                            int option = JOptionPane.showConfirmDialog(
                                    myFrame,
                                    "Do you want to register?",
                                    "Register operation",
                                    JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE
                            );
                            if(option == JOptionPane.OK_OPTION) {
                                ParamValue condition = new ParamValue(co, va, "and");
                                cuentaUtils.insertOperation(c, condition);
                            }
                        }
                    } catch(Exception er) {
                        er.printStackTrace();
                        cuentaUtils.errorMessage(
                                myFrame,
                                "Error while trying to inser a register",
                                "Insert Error"
                        );
                    } finally {
                        cuentaUtils.infoMessage(
                                myFrame,
                                "reload the window to see the changes",
                                "INFO"
                        );
                    }
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
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int 
                    row    = mTable.getSelectedRow(),
                    column = mTable.getSelectedColumn();
                String columName = mTable.getColumnName(column);
                if(columName.equals("create_at") || columName.equals("update_at") || columName.equals("password")) {
                        cuentaUtils.errorMessage(
                                myFrame,
                                "to update use 'ID' or 'nombre' or 'email' or 'FK' ",
                                "Error"
                        );
                } else if(row != -1 || column != -1) {
                    Cuenta updateCuenta = cuentaUtils.buildObjectFromTable(
                            row,
                            column,
                            loggedUser,
                            mTable
                    );
                    if(updateCuenta != null) {
                        new PanelUpdate(
                                "Update",
                                width/2,
                                height-100,
                                updateCuenta,
                                myConfig,
                                myFrame,
                                cuentaUtils
                        );
                        myFrame.setEnabled(false);
                    }
                } else {
                    cuentaUtils.errorMessage(
                            myFrame,
                            "NO TABLE ELEMENT SELECTED",
                            "Error"
                    );
                }
            }
        });
    }
    /**
     * implements the action hanlder for cancelButton
     * <br> post: </br> rollback to savepoint and redirects to Login or register if its the firts time
     * @param cancelButton: panel button to cancel the operation
     */
    private void cancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int option = JOptionPane.showConfirmDialog(
                            myFrame,
                            "Go back to login",
                            "Cancel op",
                            JOptionPane.OK_CANCEL_OPTION
                    );
                    if(option == JOptionPane.OK_OPTION) {
                        mainFrame.setVisible(true);
                        cursor.rollback();
                        cuentaUtils.setAutoImcrement();
                        myFrame.dispose();
                    }
                } catch(Exception er) {
                    er.printStackTrace();
                    cuentaUtils.errorMessage(
                            myFrame,
                            "Error while trying to cancel the operation",
                            "Cancel Error"
                    );
                }
            }
        });
    }
    /**
     * panel that its content if the panel buttons and its action handlers
     * @param width: width of the panel
     * @param height: height of the panel
     * @return the panel with the content setted
     */
    private JPanel optionsComponent(int width, int height) {
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
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(width, height);
        myFrame.setLayout(new GridLayout(3, 1));

        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                try {
                    int option = JOptionPane.showConfirmDialog(
                            myFrame,
                            "save changes before exit?",
                            "save changes",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );
                    if(option == JOptionPane.YES_OPTION) {
                        cursor.commit();
                        System.exit(0);
                    } else if(option == JOptionPane.NO_OPTION) {
                        cursor.rollback();
                        cuentaUtils.setAutoImcrement();
                        System.exit(0);
                    } else if(option == JOptionPane.CANCEL_OPTION) {
                        myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    cuentaUtils.errorMessage(
                            myFrame,
                            "Error while trying to close the window",
                            "Close Error"
                    );
                }
            }
        });

        headerLabel = new JLabel("", JLabel.CENTER);

        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(tableOptionComponents(), BorderLayout.EAST);
        controlPanel.add(filePanelOperation(width, height), BorderLayout.WEST);

        myFrame.add(headerLabel);
        myFrame.add(tableComponent(tableTitle), BorderLayout.CENTER);
        myFrame.add(optionsComponent(600, 700));
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
