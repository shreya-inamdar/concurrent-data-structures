import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.*;

public class FineGrainedHeap {

	public int ROOT = 1;
	public static final int NO_ONE = -1;
	private Lock heapLock;
	private int next;
	HeapNode [] heap;
	private Lock nextLock;
	
	
	
	public FineGrainedHeap(int capacity){
		heapLock = new ReentrantLock();
//		nextLock  = new ReentrantLock();
		next = ROOT;
		heap = new HeapNode [capacity+1];
		for(int i =0;i < capacity+1;i++)
			heap[i] = new HeapNode();
	}
	
	public void add(int data){
		
		System.out.println(java.lang.Thread.currentThread().getId() + " wants to acquire the heaplock to enqueue");			
		heapLock.lock();
		int child = next++;
		System.out.println(java.lang.Thread.currentThread().getId() + " now acquired the heaplock to enqueue");	
		
		
		heap[child].lock();
		heap[child].init(data);
		System.out.println(java.lang.Thread.currentThread().getId() + " now released the heaplock");
		heapLock.unlock();

		heap[child].unlock();
		
		while(child > ROOT){
			int parent = child/2;
			heap[parent].lock();
			heap[child].lock();
			int oldChild = child;
			try{
				if (heap[parent].tag == Status.AVAILABLE && heap[child].amOwner() ){
					if(heap[child].item < heap[parent].item){
					
						//swap item
						int temp = heap[parent].item;
						heap[parent].item = heap[child].item;
						heap[child].item = temp;						
						
						//swap owner
						long tempOwner = heap[parent].owner;
						heap[parent].owner = heap[child].owner;
						heap[child].owner = tempOwner;
						
						//swap status
						Status tempStatus = heap[parent].tag;
						heap[parent].tag = heap[child].tag;
						heap[child].tag = tempStatus;
						
						child = parent;
					}
					else{
						heap[child].tag = Status.AVAILABLE;
						heap[child].owner = NO_ONE;
						return;
					}
				}
				else if(!heap[child].amOwner()){
					child = parent;					
				}
			}
			finally{
				heap[oldChild].unlock();
				heap[parent].unlock();
			}
		}
 
		if (child == ROOT){
			heap[ROOT].lock();
			if(heap[ROOT].amOwner()){
				heap[ROOT].tag = Status.AVAILABLE;
				heap[child].owner = NO_ONE;
			}
			heap[ROOT].unlock();
		}
	}
	

	public HeapNode removeMin(){
		System.out.println(java.lang.Thread.currentThread().getId() + " wants to acquire heaplock to dequeue");	
		heapLock.lock();

		if(next <= 1){
			return null;
		}
		else{		
		HeapNode retNode = heap[ROOT];
		int bottom = --next;

			System.out.println(java.lang.Thread.currentThread().getId() + " now has the heaplock to dequeue");		
			
	
			heap[bottom].lock();
			heap[ROOT].lock();
			
			System.out.println(java.lang.Thread.currentThread().getId() + " now released the heaplock");
			heapLock.unlock();

			retNode = heap[ROOT];
			heap[ROOT].tag = Status.EMPTY;
			heap[ROOT].owner = NO_ONE;			
			
			heap[ROOT].item = heap[bottom].item;
			heap[ROOT].tag = heap[bottom].tag;
			heap[ROOT].owner = heap[bottom].owner;
			
			heap[bottom].unlock();

			if (heap[ROOT].tag == Status.EMPTY){	//heap had only root item
				heap[ROOT].unlock();
				return retNode;
			}
		
			int HeapLength = next;
			int child = 0;
			int parent = ROOT;
		
			while(parent <= HeapLength/2){
				int left = parent*2;
				int right = parent*2+1;
				heap[left].lock();
				heap[right].lock();
				if(heap[left].tag == Status.EMPTY){
					heap[right].unlock();
					heap[left].unlock();
					break;
				}
				else if (heap[right].tag == Status.EMPTY || heap[right].item > heap[left].item){
					heap[right].unlock();
					child = left;
				}
				else{
					heap[left].unlock();
					child = right;
				}

				if(heap[child].item < heap[parent].item){
		
					//swap all fields of parent and child
						// item swap 
						int temp1 = heap[child].item;
						heap[child].item = heap[parent].item;
						heap[parent].item = temp1;
					
						//status swap
						Status tempStatus = heap[child].tag;
						heap[child].tag = heap[parent].tag;
						heap[parent].tag = tempStatus;
				
						//owner swap
						long tempOwner = heap[child].owner;
						heap[child].owner = heap[parent].owner;
						heap[parent].owner = tempOwner;
				
						heap[parent].unlock();
						parent = child;
					}
					else{
						heap[child].unlock();
						break;
					}
				}
				heap[parent].unlock();
				return retNode;
			}

		}

	public synchronized void print(){
		heapLock.lock();
		for (int i=1;i<next;i++){
			System.out.println(heap[i].item);
		}
		heapLock.unlock();
	}
}
