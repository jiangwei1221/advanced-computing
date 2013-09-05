package advcomp.multithreading;
class Requestor {
	String request;

	public Requestor(String request) {
		this.request = request;
	}

	public String execute(String param, long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			return error(param);
		}
		return execute(param);
	}

	private String execute(String param) {
		return "reply-to:<" + param + ">";
	}

	private String error(String param) {
		return "timeout:<" + param + ">";
	}

	public void processResponse(String response) {
		System.out.println(String.format("Response for request [%s]: %s",
				request, response));
	}
}

public class AsynchronousCallbacks {

	public static final long TIMEOUT = 3000L;

	public static void dispatch(final Requestor requestor, final long delay) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Runnable innerTask = new Runnable() {
					@Override
					public void run() {
						requestor.processResponse(requestor.execute(
								requestor.request, delay));
					}
				};
				Thread thread = new Thread(innerTask);
				thread.start();
				try {
					Thread.sleep(TIMEOUT);
					thread.interrupt();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(task).start();
	}

	public static void main(String[] args) {
		dispatch(new Requestor("t1"), 4000L);
		dispatch(new Requestor("t2"), 2000L);
		dispatch(new Requestor("t3"), 1L);
		dispatch(new Requestor("t4"), 1L);
		dispatch(new Requestor("t5"), 2L);
	}

}
