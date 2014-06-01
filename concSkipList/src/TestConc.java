import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class TestConc implements Runnable{
	
	static LazyList l; 
	public static Lock printLock = new ReentrantLock();
	public static void main(String[] args){
		l = new LazyList();
		for(int i=0;i<3;i++)
			new Thread(new TestConc()).start();	
	}
	public void run(){
		Random rnd = new Random();
		for(int loop = 0; loop < 10; loop++){
			int seed = rnd.nextInt(50);
			if(seed%3 == 0){
				if(l.contains(seed) == false){
					l.add(seed);
				}
				else{
					printLock.lock();
					System.out.println(seed+ " Already exists");
					printLock.unlock();
				}
					
			}
			else if(seed%3 == 1){
				if (l.contains(seed) != false)
				{	
					l.remove(seed);
				}
				else
				{
					printLock.lock();
					System.out.println(seed + " Not found!");
					printLock.unlock();
				}
			}
			else{
				printLock.lock();
				System.out.println("Printing the skipList");
				l.printList();
				System.out.println("Printed the skipList");
				printLock.unlock();
			}
			
	}
}}