package Interface.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import Mundo.Cuentas.Cuenta;

public class FileUtils {
    public static String readFileLines(String filePath) {
        String fileLines = "";
        BufferedReader myReader = null;
        try {
            File miFile = new File(filePath);
            myReader = new BufferedReader(new FileReader(miFile));
            while(myReader.ready()) {
                fileLines += myReader.readLine() + "\n";
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(myReader != null) {
                try {
                    myReader.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                myReader = null;
            }
        }
        return fileLines;
    }
    public static ArrayList<Cuenta> getData(String filePath) {
        String fileLines = readFileLines(filePath);
        ArrayList<Cuenta> data = new ArrayList<>();
        String[] lines = fileLines.split("\n");
        for(int i=0; i<lines.length; ++i) {
            if(lines[i].contains(",")) {
                Cuenta myImportCuenta = new Cuenta();
                String[] accounts = lines[i].split(",");
                for(String a: accounts) {
                    String[] accountData = a.split(":");
                    String name = accountData[0].trim();
                    String value = accountData[1].trim();
                    if(name.equals("nombre")) {
                        myImportCuenta.setNombre(value);
                    }
                    if(name.equals("email")) {
                        myImportCuenta.setEmail(value);
                    }
                    if(name.equals("password")) {
                        myImportCuenta.setPassword(value);
                    }
                    myImportCuenta.setCreate_at();
                }
                data.add(myImportCuenta);
            }
        }
        return data;
    }
    public static void exportSaveData(String destination, String fileName, ArrayList<Cuenta> misCuentas) {
        FileWriter myWriter = null;
        try {
            String nFile = new File(fileName).isFile() && fileName.contains(".txt") ? fileName : fileName + ".txt";
            File miFile = new File(destination + "\\" + nFile);
            String build = "";
            myWriter = new FileWriter(miFile, false);
            for(int i=0; i<misCuentas.size(); ++i) {
                Cuenta mia = misCuentas.get(i);
                String nombre = "nombre: " + mia.getNombre();
                String email = "email: " + mia.getEmail();
                String password = "password: " + mia.getPassword();
                build += nombre + ", " + email + ", " + password + "\n";
            }
            myWriter.write(build);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(myWriter != null) {
                try {
                    myWriter.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                myWriter = null;
            }
        }
    }
}
