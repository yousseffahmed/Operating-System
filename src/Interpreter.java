import java.util.ArrayList;

public class Interpreter {
	Mutex userInput = new Mutex();
	Mutex userOutput = new Mutex();
	Mutex file = new Mutex();
	public void interpret (String programLine, Memory mem, int Start, int End, Scheduler sch, String processID) {
		SystemCall sc = new SystemCall();
		if (programLine.contains("print") && !programLine.contains("FromTo")) {
			String[] words = programLine.split(" ");
			Object value = sc.readMem(mem, words[1], Start, End); 
			sc.print(value);
		}
		
		else if (programLine.contains("print") && programLine.contains("FromTo")) {
			String[] words = programLine.split(" ");
			int start = Integer.parseInt((String) sc.readMem(mem, words[1], Start, End));
			int end = Integer.parseInt((String) sc.readMem(mem, words[2], Start, End));
			for (int i = start; i <= end; i++) {
				sc.print(i);
			}
		}
		
		else if (programLine.contains("assign")) {
			if (programLine.contains("input")) {
				String[] words = programLine.split(" ");
				System.out.println("Please enter a value ");
				Object o = sc.input();
				sc.writeMem(mem, words[1], o, Start, End);
			}
			else if (programLine.contains("readFile")) {
				String[] words = programLine.split(" ");
				String fileName = (String) sc.readMem(mem, words[3], Start, End);
				ArrayList <String> result = sc.read(fileName);
				sc.writeMem(mem, words[1], result.get(0), Start, End);
			}
			else {
				String[] words = programLine.split(" ");
				for (int i = 0; i < mem.word.size(); i++) {
					if (mem.word.get(i).equals("null")) {
						Variable v = new Variable (words[1],words[2]);
						mem.word.set(i, v);
						break;
					}
				}
			}
		}
		
		else if (programLine.contains("readFile")) {
			String[] words = programLine.split(" ");
			ArrayList <String> result = sc.read(words[1]);
			for (int i = 0; i < result.size(); i++)
				sc.print(result.get(i));
		}
		
		else if (programLine.contains("writeFile")) {
			String[] words = programLine.split(" ");
			String fileName = (String) sc.readMem(mem, words[1], Start, End);
			String value = (String) sc.readMem(mem, words[2], Start, End);
			sc.write(fileName, value);
		}
		
		else if (programLine.contains("semWait")) {
			if (programLine.contains("userInput"))
				userInput.semWait(processID, sch);
			else if (programLine.contains("userOutput"))
				userOutput.semWait(processID, sch);
			else if (programLine.contains("file"))
				file.semWait(processID, sch);
		}
		
		else if (programLine.contains("semSignal")) {
			if (programLine.contains("userInput"))
				userInput.semSignal(sch);
			else if (programLine.contains("userOutput"))
				userOutput.semSignal(sch);
			else if (programLine.contains("file"))
				file.semSignal(sch);
		}
	}
}
