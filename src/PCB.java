
public class PCB {
	String processID;
	String processState;
	int PC;
	int [] memBound = new int [2];
	public PCB (String processID, String processState, int PC, int min, int max) {
		this.processID = processID;
		this.processState = processState;
		this.PC = PC;
		this.memBound[0] = min;
		this.memBound[1] = max;
	}
}
