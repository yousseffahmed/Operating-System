import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Memory {
	ArrayList<Object> word = new ArrayList<>();
	int size = 40;
	int i = 1;
	public boolean hasSpace() {
		if (this.word.size() < size)
			return true;
		return false;
	}
	public void readProgram(String program) {
		i++;
		 String filePath = "src/programs/" + program;
		 int start = this.word.size();
	        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                this.word.add("Line "+line);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        PCB pcb = new PCB(program,"running",start,start,this.word.size()+6);
	        this.word.add(pcb.processID);
	        this.word.add("ProcessState " + pcb.processState);
	        this.word.add("PC " + pcb.PC);
	        this.word.add(pcb.memBound[0] + " " + pcb.memBound[1]);
	        this.word.add("null");
	        this.word.add("null");
	        this.word.add("null");
	}
	public static void main (String [] args) {
		Memory mem = new Memory();
		mem.readProgram("Program_1.txt");
		mem.readProgram("Program_2.txt");
		mem.readProgram("Program_3.txt");
		for (int i = 0; i < mem.word.size(); i++)
			System.out.println("Word"+ i + ": "+ mem.word.get(i));
	}
}
