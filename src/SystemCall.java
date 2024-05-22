import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SystemCall {
	public ArrayList<String> read(String filePath) {
	        File file = new File("src/secMem/"+filePath);
	        ArrayList<String> result = new ArrayList<>();
	        try {
	            Scanner scanner = new Scanner(file);
	            while (scanner.hasNextLine()) {
	                String line = scanner.nextLine();
	                result.add(line);
	            }
	            scanner.close();

	        } catch (FileNotFoundException e) {
	            e.printStackTrace(); 
	    }
	        return result;
	}
	public void write(String filePath, String input) {
        try {
            FileWriter fileWriter = new FileWriter("src/secMem/"+filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(input);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	public void print(Object o) {
		System.out.println(o);
	}
	public Object input () {
		Scanner scanner = new Scanner(System.in);
		Object o = scanner.nextLine();
		return o;
	}
	public Object readMem(Memory mem, String words, int Start, int End) {
		Object value = null;
		for (int i = Start ; i < End; i++) {
			if (mem.word.get(i) instanceof Variable) {
				Variable v = (Variable) mem.word.get(i);
				if (v.name.equals(words))
					value = v.value;
			}
		}
		return value;
	}
	public void writeMem(Memory mem, String words, Object o, int Start, int End) {
		for (int i = Start; i < End; i++) {
			if (mem.word.get(i).equals("null")) {
				Variable v = new Variable (words,o);
				mem.word.set(i, v);
				break;
			}
		}
	}
}
