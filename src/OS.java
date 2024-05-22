import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class OS {
	Memory mem = new Memory();
	Interpreter inte = new Interpreter();
	Scheduler sch = new Scheduler();
	String diskContent;
	public void execute(String program) {
		int pc = -1;
		int Start = start(program);
		int End = end(program);
		for (int i = Start; i < End;i++) {
			String word = (String) this.mem.word.get(i);
			if (word.contains("PC")) {
				String[] words = word.split(" ");
				pc = Integer.parseInt(words[1]);
				break;
			}
		}
		String word = (String) this.mem.word.get(pc);
		System.out.println(word);
		int firstSpaceIndex = word.indexOf(" ");
		String result = word.substring(firstSpaceIndex + 1);
		this.inte.interpret(result, this.mem, Start, End, this.sch, program);
		updatePC(pc+=1, Start, End);
		String lastLine = lastLine(Start, End);
		if (sch.blockedQ.contains(program))
			updatePC(pc-=1, Start, End);
		if (lastLine.equals(word))
			sch.finishedQ.add(program);
	}
	public int start(String processID) {
		int Start = -1;
		for (int i = 0; i < this.mem.word.size();i++) {
			if (this.mem.word.get(i) instanceof String) {
				String word = (String) this.mem.word.get(i);
				if (word.equals(processID)) {
					String[] words = ((String) this.mem.word.get(i+3)).split(" ");
					Start = Integer.parseInt(words[0]);
					break;
				}
			}
		}
		return Start;
	}
	public int end(String processID) {
		int End = -1;
		for (int i = 0; i < this.mem.word.size();i++) {
			if (this.mem.word.get(i) instanceof String) {
				String word = (String) this.mem.word.get(i);
				if (word.equals(processID)) {
					String[] words = ((String) this.mem.word.get(i+3)).split(" ");
					End = Integer.parseInt(words[1]);
					break;
				}
			}
		}
		return End;
	}
	public String lastLine(int Start, int End) {
		String result = "";
		for (int i = Start; i < End; i++) {
			if (this.mem.word.get(i) instanceof String) {
				String word = (String) this.mem.word.get(i);
				if (word.contains("Line")) {
					result = word;
				}
			}
		}
		return result;
	}
	public void updatePC(int newValue, int Start, int End) {
		for (int i = Start; i < End;i++) {
			String word = (String) this.mem.word.get(i);
			if (word.contains("PC")) {
				this.mem.word.set(i, "PC " + newValue);
				break;
			}
		}
	}
	public void clk() {
		initDisk();
		for (int i = 0; i < 35; i++) {
			System.out.println("CLK: "+i);
			if (i == 0) {
				sch.readyQ.add("Program_1.txt");
				sch.running = sch.readyQ.remove();
				this.mem.readProgram("Program_1.txt");
				if (this.mem.word.size() > this.mem.size)
					writeToDisk("Program_1.txt");
			}
			else if (i == 1) {
				sch.readyQ.add("Program_2.txt");
				this.mem.readProgram("Program_2.txt");
				if (this.mem.word.size() > this.mem.size)
					writeToDisk("Program_2.txt");
			}
			else if (i == 4) {
				sch.readyQ.add("Program_3.txt");
				this.mem.readProgram("Program_3.txt");
				if (this.mem.word.size() > this.mem.size)
					writeToDisk("Program_3.txt");
			}
			if ((i)%Scheduler.timeSlice==0) {
				sch.readyQ.add(sch.running);
				sch.running = sch.readyQ.remove();
			}
			if (sch.finishedQ.size() == 3) {
				sch.running = "None";
				System.out.println("Running: "+sch.running);
				System.out.println("ReadyQ"+Scheduler.queueToString(sch.readyQ));
				System.out.println("BlockedQ"+Scheduler.queueToString(sch.blockedQ));
				System.out.println("FinishedQ"+Scheduler.queueToString(sch.finishedQ));
				System.out.println("In Disk: "+diskContent);
				for (int j=0; j < sch.finishedQ.size(); j++)
					sch.finishedQ.remove();
				clearMem(sch.finishedQ.peek());
				removeNull();
				printMem();
				return;
			}
			if (diskContent == sch.running) {
				writeToMem(diskContent);
				writeToDisk(sch.readyQ.peek());
			}
			System.out.println("Running: "+sch.running);
			System.out.println("ReadyQ"+Scheduler.queueToString(sch.readyQ));
			System.out.println("BlockedQ"+Scheduler.queueToString(sch.blockedQ));
			System.out.println("FinishedQ"+Scheduler.queueToString(sch.finishedQ));
			System.out.println("In Disk: "+diskContent);
			execute(sch.running);
			removeFinishedPrograms();
			if (sch.blockedQ.size()!=0 && (i)%Scheduler.timeSlice!=0) {
				System.out.println("----------------------------------------------------------------------------------------------");
				System.out.println("Running: "+sch.running);
				System.out.println("ReadyQ"+Scheduler.queueToString(sch.readyQ));
				System.out.println("BlockedQ"+Scheduler.queueToString(sch.blockedQ));
				System.out.println("FinishedQ"+Scheduler.queueToString(sch.finishedQ));
				System.out.println("In Disk: "+diskContent);
				if (diskContent != null) {
					writeToMem(diskContent);
					writeToDisk(sch.readyQ.peek());
				}
				execute(sch.running);
			}
			for (String program : sch.finishedQ) {
				if (sch.running!=program)
					clearMem(program);
			}
			printMem();
			System.out.println("----------------------------------------------------------------------------------------------");
		}
	}
	 public void removeFinishedPrograms() {
        ArrayList<String> elementsToRemove = new ArrayList<>();
        for (String element : sch.finishedQ) {
            while (sch.readyQ.contains(element)) {
                sch.readyQ.remove(element);
                elementsToRemove.add(element);
            }
        }
	 }
	 public void printMem() {
		 for (int i = 0; i < mem.word.size(); i++) {
				System.out.println("Word"+ i + ": "+ mem.word.get(i));
				if (mem.word.get(i) instanceof Variable) {
					Variable v = (Variable) mem.word.get(i);
					System.out.println("Word"+ i + ": "+v.name+ " "+ v.value);
				}
		 }
		 	
	 }
	 public void writeToDisk(String program) {
		 int Start = start(program);
		 int End = end(program);
		 String lastLine = lastLine(Start, End);
		 diskContent = program;
		 try {
		        FileWriter fileWriter = new FileWriter("src/secMem/Disk");
		        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		        ArrayList<Object> words = this.mem.word;

		        for (int i = Start; i < End; i++) {
		            bufferedWriter.write((String) words.get(i));
		            bufferedWriter.newLine();  
		            if (words.get(i).equals(lastLine))
		                break;
		        }

		        bufferedWriter.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		 clearMem(program, lastLine);
	 }
	 public void clearMem(String program, String lastLine) {
		 int Start = start(program);
		 int End = end(program);
		 for (int i = Start; i < End; i++) {
			 String word = (String) this.mem.word.get(i);
			 if (word.contains("Line"))
				 this.mem.word.set(i, " ");
			 if (word.equals(lastLine))
	                break;
		 }
	 }
	 public static ArrayList<String> readFromDisk() {
	        ArrayList<String> contents = new ArrayList<>();

	        try (BufferedReader reader = new BufferedReader(new FileReader("src/secMem/Disk"))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                contents.add(line);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return contents;
	    }
	 public void writeToMem(String program) {
		 int Start = start(program);
		 ArrayList<String> lines = readFromDisk();
		 int End = lines.size()+Start;
		 for (int i = Start; i < End; i++) {
			 String word = (String) this.mem.word.get(i);
			 if (word.equals(" "))
				 this.mem.word.set(i, lines.get(i-Start));
	     }
	 }
	 public void clearMem(String program) {
		 int Start = start(program);
		 int End = end(program);
		 for (int i = Start; i < End; i++) {
			 this.mem.word.set(i, " ");
	     }
	 }
	 public void initDisk() {
	        try {
	            FileWriter fileWriter = new FileWriter("src/secMem/Disk");
	            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	            bufferedWriter.newLine();
	            bufferedWriter.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	 public void removeNull() {
		 for (int i = 0; i < this.mem.word.size();i++) {
			 if (this.mem.word.get(i) == "null")
				 this.mem.word.set(i, " ");
		 }
	 }
	public static void main (String [] args) {
		OS os = new OS();
		os.clk();
	}
}
