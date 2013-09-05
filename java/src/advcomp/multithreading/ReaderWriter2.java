package advcomp.multithreading;
import java.util.Date;


/**
 * The second Reader-Writer problem.<br>
 * Writers are protected with preferences.
 *  
 * @author amethystlei
 *
 */
public class ReaderWriter2 {
	
	static class Lock {
		public static Object LR = new Object();
		public static Object LW = new Object();
		public static int readerCount = 0;
	}

	static class Reader extends Thread {
		int count = 0;
		@Override
		public void run() {
			while (++count <= 100) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				synchronized (Lock.LR) {
					++Lock.readerCount;
				}
				// Read
				System.out.println("Reader: " + ReaderWriter2.data);
				synchronized (Lock.LR) {
					--Lock.readerCount;
				}
			}
		}
	}

	static class Writer extends Thread {
		int count = 0;
		@Override
		public void run() {
			while (++count <= 100)	 {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				synchronized (Lock.LW) {
					synchronized (Lock.LR) {
						if (Lock.readerCount == 0) {
							// Write
							ReaderWriter2.data = new Date().toString();
						}
					}
				}
			}
		}
	}
	
	public static String data = new Date().toString();

	public static void main(String[] args) {
		Reader r1 = new Reader();
		Reader r2 = new Reader();
		Writer w1 = new Writer();
		Writer w2 = new Writer();
		r1.start();
		r2.start();
		w1.start();
		w2.start();
		try {
			w1.join();
			System.out.println("writer 1 ends.");
			w2.join();
			System.out.println("writer 2 ends.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
