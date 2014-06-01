import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;
import java.lang.management.*;
public class ConTest1 {
	public AtomicInteger finish_count;
	public static int N = 625;
	Tree myTree;

	public ConTest1() {
   		myTree = new Tree();
		finish_count = new AtomicInteger(0);
		
		User user;
		for (int i = 0; i < N; i++) {
			user = new User(i);
			new Thread(user).start();
		}
		while (finish_count.get() < N) ;
	//	myTree.print();
	}

	class User implements Runnable {
		int id;
		
		public User(int id) {
			this.id = id;
		}
		public void run() {
			Random rnd = new Random();
			for (int  j = 0; j  < 1000; j++) {
				int op = rnd.nextInt(3);
				int val = rnd.nextInt(1000);
				switch (op) {
					case 0: 
					myTree.insert(val);
				//	System.out.println(id + " inserted " + val );
					break;
				
					case 1:
					myTree.delete(val);
				//	System.out.println(id + " deleted " + val );
					break;
				
					case 2:
					myTree.find(val);
				//	System.out.println(id + " finds " + val );
					break;
				
					default:
				//	System.out.println("error");
				}
			}
			finish_count.getAndIncrement();
		}		
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		new ConTest1();
		long end = System.currentTimeMillis();
		System.out.println("total run time : " + (end - start));
	}

}