package hashdb.main.tasks.response;

public abstract class BooleanResponseTask
		extends ResponseTask {
	boolean status;

	public void setStatus(boolean status) {
		this.status = status;
	}
}
