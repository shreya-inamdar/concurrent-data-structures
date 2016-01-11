import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class WaitFreeConstruct {
	public static TNode ROOT;
	public static int N = 60;
	public static Lock printLock = new ReentrantLock();
	private static Node [] announce = new Node[N];
	private static Node [] head = new Node[N];
	private static Node tail = new Node();
	WaitFreeConstruct(){
		ROOT = new TNode(1000);
		for(int i = 0 ; i < N; i++){
			announce[i] = tail;
			head[i] = tail;
		}
	}
	
	boolean apply(Invoc invoc){
		int i = ThreadID.get();
		announce[i] = new Node(invoc);
		head[i] = Node.max(head);
		Node optiTail = head[i];
		while(announce[i].seq == 0){
			Node before = head[i];
			Node prefer = announce[i];
			Node help = announce[(before.seq+1)%N];
			if(help.seq == 0)
				prefer = help;
			
			Node after = before.decideNext.decide(prefer);
			before.next = after;
			after.seq = before.seq+1;
			head[i] = after;
		}
		
		Node current = optiTail;
		while(current != announce[i]){
			if(current.status == false){
				if(current.invoc.opId == 1){
					insert(current.invoc.key);
				}
				else if(current.invoc.opId == 2){
					delete(current.invoc.key);
				}
				else
					find(current.invoc.key);
				current.status = true;
			}
			current = current.next;
		}
		
		head[i] = announce[i];
		boolean output = true;
		if (announce[i].status == false){
			if(invoc.opId == 1){
				output = insert(invoc.key);
			}
			else if(invoc.opId == 2)
				output = delete(invoc.key);
			else	
				output = find(invoc.key);
		}
		announce[i].status = true;

		return output;
	}
	
	boolean insert(int key){
		TNode [] ret = ROOT.search(key);
		if(ret[0] != null){
			return ret[0].freeze.compareAndSet(true, false);
		}
		//if node is not found in the tree already
		TNode parent = ret[1];
		TNode toInsert = new TNode(key);
		TNode q;
		while(toInsert.level == 0){
			if(key > parent.key){
				q = parent.decideRight.decide(toInsert);
				parent.right = q;
				q.level = parent.level + 1;
				parent = q;
			}
			else if (key < parent.key){
				q = parent.decideLeft.decide(toInsert);
				parent.left = q;
				q.level = parent.level + 1;
				parent = q;
			}
			else{
				return false;
			}
		}
		return true;
	}	
	
	public boolean delete(int key){
		TNode [] ret = ROOT.search(key);
		if(ret[0] == null)
			return false;
		else{
			return ret[0].freeze.compareAndSet(false, true);
		}
	}
	
	public boolean find(int key){
		TNode [] ret = ROOT.search(key);
		if(ret[0] == null)
			return false;

		else{
			return !ret[0].freeze.get();
		}
	}
	
	public void print(){
		Node curr = tail.next;
		while(curr != null){			
			System.out.println("No. "+curr.seq + " | " + "ID: " + curr.invoc.opId + " | " + "key: " + curr.invoc.key);
			curr = curr.next;
		}
		TNode start = ROOT;
		System.out.println("Inorder printing...");
		inorder(start);
	}
	
	private void inorder(TNode root){
		if(root == null)
			return;
		inorder(root.left);
		if(root.freeze.get() == false)
			System.out.println(root.key + " @ " + root.level);
		inorder(root.right);
	}
}
