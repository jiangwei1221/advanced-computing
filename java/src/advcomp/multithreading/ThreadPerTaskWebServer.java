package advcomp.multithreading;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Chapter 7. Parallel Computing
 * Problem 7.2 - Thread per task web server.
 * 
 * @author amethystlei
 *
 */
public class ThreadPerTaskWebServer {

	private static final int NTHREADS = 10;
	private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);

	private static boolean stopped = false;

	private static boolean isStopped() {
		return stopped;
	}

	public static void main(String[] args) throws IOException {
		final ServerSocket socket = new ServerSocket(12345);
		Runnable dispatcher = new Runnable() {
			@Override
			public void run() {
				try {
					while (!isStopped()) {
						final Socket connection = socket.accept();
						Runnable task = new Runnable() {
							@Override
							public void run() {
								System.out.println("New connection "
										+ connection.getLocalAddress());
								// TODO handle request
							}
						};
						exec.execute(task);						
					}
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		};
		Thread thread = new Thread(dispatcher);
		thread.start();

		Scanner scanner = new Scanner(System.in);
		try {
		while (!stopped) {
				Thread.sleep(1);
				String s = scanner.next().toLowerCase();
				System.out.println(s);
				if ("quit".equals(s)) {
					stopped = true;
					System.out.println("stopped set to true.");
					socket.close();
					System.out.println("socket closed.");
					thread.join();
					System.out.println("thread joined.");
				}
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
		}
	}
}
