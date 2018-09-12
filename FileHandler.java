import java.util.*;
import java.io.*; 
import java.nio.file.*;
import java.nio.charset.StandardCharsets; 

public class FileHandler {


    private List<Instruction> instructionList;
    private int index;

    

    public FileHandler(String filePath){
        List<String> instructionData = readFile(filePath);
        index = -1;
        
        instructionList = new LinkedList<Instruction>();

        for(String line : instructionData) {
            instructionList.add(new Instruction(line));
        } 
    }

    private List<String> readFile(String filePath){
        List<String> lines = new LinkedList<String>();
        
        try{ 
            lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8); 
        }catch (IOException e){ 
            e.printStackTrace(); 
        } 
        return lines;
    }

    public Instruction next(){
        index += 1;
        return instructionList.get(index);
    }

    public void reset(){
        index = -1;
    }
}
