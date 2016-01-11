
public class Node {
	public int seq;
	public CASConsensus decideNext;
	Node next;
	Invoc invoc;
	boolean status;
	
	public Node(){
		this.seq = 1;
		this.decideNext = new CASConsensus();
		next = null;
		invoc = null;
		status = true;
	}
	
	public Node (Invoc invoc){
		this.seq = 0;
		this.decideNext = new CASConsensus();
		next = null;
		this.invoc = invoc;
		status = false;
	}
	
	public static Node max(Node[] head){
		Node retNode = head[0];
		for(int i=0; i < head.length; i++){
			if (head[i].seq > retNode.seq){
				retNode = head[i];
			}
		}
		return retNode; 
	}
}
