import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.*;

public class AVLRTest implements Runnable{
	static AVLTree Root;
	public static Lock printLock = new ReentrantLock();
	public static void main(String[] args){
		Root = new AVLTree();
		for(int i=0;i<3;i++)
			new Thread(new AVLRTest()).start();
	}
	
	public void run(){
		Random rnd = new Random();
		for(int loop = 0; loop < 10; loop++){
			int seed = rnd.nextInt(50);
			if(seed%3 == 0){
				if(Root.get(seed) == -1)
				{
					Root.put(seed, seed);
				}
				else{
					printLock.lock();
					System.out.println(seed+ " Already exists");
					printLock.unlock();
				}
					
			}
			else if(seed%3 == 1){
				if (Root.get(seed) != -1)
				{	
					Root.remove(seed);
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
				System.out.println("Printing the tree");
				Root.printTree(Root.rootHolder);
				System.out.println("Printed the tree");
				printLock.unlock();
			}
				
		}
	}

}
