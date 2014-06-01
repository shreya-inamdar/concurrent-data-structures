import java.lang.management.ThreadInfo;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.*;

public class Test implements Runnable{
	
	private int RANGE = 100;
	public static int counter=0;
	public static volatile FineGrainedHeap FGH;
	public static volatile Lock printLock = new ReentrantLock();	// to lock print to prevent interleaved printing of heap
	
	public static void main(String[] args){
		
		int capacity = 100;
		FGH = new FineGrainedHeap(capacity);
		int t = 5;
		for(int j =0 ;j < t;j++){		
			(new Thread(new Test())).start();
		}
		return;
	}
	
	public void run(){
		Random random = new Random();
		for(int loop = 0 ;loop < 10; loop++){
			int seed = random.nextInt(RANGE);
			if (seed%3==0){
				try{

					int next = random.nextInt(RANGE);
					printLock.lock();
					System.out.println("Enq (" + next + ") :" + java.lang.Thread.currentThread().getId());
					printLock.unlock();					
					FGH.add(next);				
					printLock.lock();
					System.out.println("Ok ():" + java.lang.Thread.currentThread().getId());
					printLock.unlock();
				}
				catch(Exception e){}
			}
			else if (seed%3 == 1){		
			
				try{	
					printLock.lock();
					System.out.println("Deq (): " + java.lang.Thread.currentThread().getId());
					printLock.unlock();
					
					HeapNode min = FGH.removeMin();
					
					printLock.lock();
					if (min == null){
						System.out.println(java.lang.Thread.currentThread().getId() + " : Underflow");
					}
					else{
						System.out.println("Ok("+ min.item +"): " + java.lang.Thread.currentThread().getId());	
					}					
					printLock.unlock();
				}
				catch(Exception e){}
			}
			else{
				
				printLock.lock();
				FGH.print();
				printLock.unlock();
					
			}				
		}
	}
}