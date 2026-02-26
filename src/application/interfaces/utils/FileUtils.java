package application.interfaces.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.List;

import application.models.cuenta.CuentaModel;
import application.models.cuenta.CuentaODM;

import java.util.ArrayList;

public class FileUtils {
    public static List<String> readFileLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File miFile = new File(filePath);
        try(BufferedReader myReader = new BufferedReader(new FileReader(miFile))) {
            while(myReader.ready()) {
                lines.add(myReader.readLine());
            }
        } catch(Exception e) {
            e.printStackTrace();
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
                    String name  = accountData[0].trim();
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
                    myImportCuenta.makeCreate_at();
                }
                data.add(myImportCuenta);
            });
        return data;
    }
    public static void exportSaveData(String destination, String fileName, List<CuentaModel> misCuentas) {
        String nFile ="";
        if(fileName.contains(".txt")) {
            nFile = fileName;
        } else {
            nFile = fileName.concat(".txt");
        }
        StringBuilder build = new StringBuilder();
        File miFile = new File(destination + File.separator + nFile);
        try(FileWriter myWriter = new FileWriter(miFile, false)) {
            for(int i=0; i<misCuentas.size(); ++i) {
                CuentaModel mia = misCuentas.get(i);
                String nombre = "nombre: " + mia.getNombre();
                String email = "email: " + mia.getEmail();
                String password = "password: " + mia.getPassword();
                build.append(nombre);
                build.append(", ");
                build.append(email);
                build.append(", ");
                build.append(password);
                build.append("\n");
            }
            myWriter.write(build.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    private FileUtils() {}
}
