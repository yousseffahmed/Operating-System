
public class Mutex {
	boolean locked = false;
	public void semWait(String processID, Scheduler sch) {
		if (locked) {
			sch.blockedQ.add(processID);
			sch.running = sch.readyQ.remove();
		}
		locked = true;
	}
	public void semSignal(Scheduler sch) {
		if (locked) {
			while (!sch.blockedQ.isEmpty()) {
				sch.readyQ.add(sch.blockedQ.remove());
			}
			locked = false;
		}
	}
}
