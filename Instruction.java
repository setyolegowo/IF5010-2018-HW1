
import java.util.Scanner;

public class Instruction{
    private String instruction;
    private String branchTarget;
    private Boolean isTaken;

    public Instruction(String line){
        Scanner sc = new Scanner(line).useDelimiter(" ");

        this.instruction = sc.next();
        this.branchTarget = sc.next();
        this.isTaken = (sc.next() == "1") ? true : false;
        sc.close();
    }

    public String getInstruction() {
        return instruction;
    }
    public String getBranchTarger() {
        return branchTarget;
    }
    public boolean getIsTaken() {
        return isTaken;
    }
}