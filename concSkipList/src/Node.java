import java.util.concurrent.locks.ReentrantLock;

public class Node {
	
	public static int MAX_LEVEL = 10;
	final ReentrantLock Lock = new ReentrantLock();
	final int key;
	final Node [] next ;
	volatile boolean marked = false;
	volatile boolean fullyLinked = false;
	public int toplevel;
	
	public Node(int key){	//sentinel constructor
		this.key = key;
		next = new Node[MAX_LEVEL+1];
		this.toplevel = MAX_LEVEL;
	}
	
	public Node (int key, int level){//non sentinel constructor
		this.key = key;
		next = new Node[level+1];
		this.toplevel = level;
	}
	
	public void lock(){
		Lock.lock();
	}
	
	public void unlock(){
		Lock.unlock();
	}
}
