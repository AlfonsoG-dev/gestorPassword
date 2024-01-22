package Interface.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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
}
