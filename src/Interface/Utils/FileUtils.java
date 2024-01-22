package Interface.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import Mundo.Cuentas.Cuenta;

public class FileUtils {
    public static String ReadFileLines(String filePath) {
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
    public static String GetData(String filePath) {
        String fileLines = ReadFileLines(filePath);
        String data = "";
        String[] lines = fileLines.split("\n");
        for(int i=0; i<lines.length; ++i) {
            String[] accounts = lines[i].split(",");
            for(String a: accounts) {
                String[] accountData = a.split(":");
                String name = accountData[0].trim();
                String value = accountData[1].trim();
                if(name.equals("nombre")) {
                    data += value + ", ";
                }
                if(name.equals("email")) {
                    data += value + ", ";
                }
                if(name.equals("password")) {
                    data += value + "\n";
                }
            }
        }
        return data;
    }
    public static void ExportSaveData(String destination, String fileName, ArrayList<Cuenta> misCuentas) {
        FileWriter myWriter = null;
        try {
            File miFile = new File(destination + "\\" + fileName);
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
