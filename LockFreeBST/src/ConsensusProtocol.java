
public abstract class ConsensusProtocol implements Consensus{
	
	static final int N = 100;	
	protected Node [] proposed = new Node [N];
	protected TNode [] proposedT = new TNode [N];
	
	abstract public Node decide(Node value);
	abstract public TNode decide(TNode value);
	public void propose(Node value){
		proposed[ThreadID.get()] = value;
	}
	public void propose(TNode value){
		proposedT[ThreadID.get()] = value;
	}
	
}