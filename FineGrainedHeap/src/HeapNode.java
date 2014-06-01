import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.*;

public class HeapNode{
	private static final int ROOT = 1;
	private static final int NO_ONE = -1;
	Status tag;
	int item;
	Lock lock;
	long owner;
	
	public void init(int data)	{
		this.item = data;
		tag = Status.BUSY;
		owner = java.lang.Thread.currentThread().getId();
	}

	public HeapNode(){
		tag = Status.EMPTY;
		lock = new ReentrantLock();
	}
	
	public void lock(){ lock.lock(); }
	public void unlock(){ lock.unlock(); }
	public boolean amOwner(){
		return (this.owner == java.lang.Thread.currentThread().getId());
	}
}