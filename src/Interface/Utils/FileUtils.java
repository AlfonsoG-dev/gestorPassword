package Interface.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import Mundo.Cuentas.Cuenta;

public class FileUtils {
    public static ArrayList<String> readFileLines(String filePath) {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader myReader = null;
        try {
            File miFile = new File(filePath);
            myReader = new BufferedReader(new FileReader(miFile));
            while(myReader.ready()) {
                lines.add(myReader.readLine());
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
        return lines;
    }
    public static ArrayList<Cuenta> getData(String filePath) {
        ArrayList<String> fileLines = readFileLines(filePath);
        ArrayList<Cuenta> data = new ArrayList<>();
        fileLines
            .parallelStream()
            .forEach(e -> {
                if(e.contains(",")) {
                    Cuenta myImportCuenta = new Cuenta();
                    String[] accounts = e.split(",");
                    for(String a: accounts) {
                        String[] accountData = a.split(":");
                        String 
                            name  = accountData[0].trim(),
                                  value = accountData[1].trim();
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
            });
        return data;
    }
    public static void exportSaveData(String destination, String fileName, ArrayList<Cuenta> misCuentas) {
        FileWriter myWriter = null;
        try {
            String 
                nFile = new File(fileName).isFile() && fileName.contains(".txt") ? fileName : fileName + ".txt",
                build = "";
            File miFile = new File(destination + "\\" + nFile);
            myWriter = new FileWriter(miFile, false);
            for(int i=0; i<misCuentas.size(); ++i) {
                Cuenta mia = misCuentas.get(i);
                String 
                    nombre   = "nombre: " + mia.getNombre(),
                    email    = "email: " + mia.getEmail(),
                    password = "password: " + mia.getPassword();
                build       += nombre + ", " + email + ", " + password + "\n";
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
