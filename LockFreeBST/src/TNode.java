import java.util.concurrent.atomic.*;
//independent of choice of N
public class TNode {
	public int key;
	public int level;
	public AtomicBoolean freeze;
	public CASConsensus decideRight;
	public TNode right;
	public CASConsensus decideLeft;
	public TNode left;
	
	public TNode(int key){
		this.key = key;
		level = 0;
		freeze = new AtomicBoolean(false);
		decideLeft = new CASConsensus();
		left = null;
		decideRight = new CASConsensus();
		right = null;
	}
	
	
	public TNode [] search (int key){
		TNode curr = this;
		TNode parent=curr;
		TNode [] ret = new TNode[2];
		while(curr != null){
			if (key == curr.key){
				ret[0] = curr; 
				ret[1] = parent; 
				return ret;
			}
			else if(key < curr.key){
				parent = curr;
				curr = curr.left;
			}
			else{
				parent = curr;
				curr = curr.right;
			}
		}
		ret[0] = null;
		ret[1] = parent;
		return ret;
	}
	
}
