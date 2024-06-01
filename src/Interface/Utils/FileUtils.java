package Interface.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.List;
import java.util.ArrayList;

import Models.Cuenta.CuentaModel;
import Models.Cuenta.CuentaODM;

public class FileUtils {
    public static List<String> readFileLines(String filePath) {
        List<String> lines = new ArrayList<>();
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
    public static List<CuentaModel> getData(String filePath) {
        List<String> fileLines = readFileLines(filePath);
        List<CuentaModel> data = new ArrayList<>();
        fileLines
            .parallelStream()
            .filter(e -> e.contains(","))
            .forEach(e -> {
                CuentaODM myImportCuenta = new CuentaODM();
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
                    myImportCuenta.makeCreate_at();
                }
                data.add(myImportCuenta);
            });
        return data;
    }
    public static void exportSaveData(String destination, String fileName, List<CuentaModel> misCuentas) {
        FileWriter myWriter = null;
        try {
            String nFile = new File(fileName).isFile() && fileName.contains(".txt") ?
                fileName : fileName + ".txt";
            StringBuffer build = new StringBuffer();
            File miFile = new File(destination + "\\" + nFile);
            myWriter = new FileWriter(miFile, false);
            for(int i=0; i<misCuentas.size(); ++i) {
                CuentaModel mia = misCuentas.get(i);
                String 
                    nombre   = "nombre: " + mia.getNombre(),
                    email    = "email: " + mia.getEmail(),
                    password = "password: " + mia.getPassword();
                build.append(nombre + ", " + email + ", " + password + "\n");
            }
            myWriter.write(build.toString());
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
