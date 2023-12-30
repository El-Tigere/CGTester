package cgtester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
    
    public static String loadFileString(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder sb = new StringBuilder();
        
        while(scanner.hasNextLine()) sb.append(scanner.nextLine() + '\n');
        
        scanner.close();
        return sb.toString();
    }
    
    public static String loadFileString(String filePath) throws FileNotFoundException {
        return loadFileString(new File(filePath));
    }
    
    public static <T> T loadFileObject(File file, Class<T> objectType) throws IOException {
        String json = Util.loadFileString(file);
        
        ObjectMapper om = new ObjectMapper();
        return om.readValue(json, objectType);
    }
    
    public static <T> T loadFileObject(String filePath, Class<T> objectType) throws IOException {
        return loadFileObject(new File(filePath), objectType);
    }
}
