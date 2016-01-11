import java.util.concurrent.atomic.AtomicInteger;
public class CASConsensus extends ConsensusProtocol{
//The subclass of Consensus protocol, which implements Consensus
//Its methods are called by LFUniversal class

	private final int FIRST = -1;	
	AtomicInteger r = new AtomicInteger(FIRST);
	
	public Node decide(Node value){
		int i = ThreadID.get();
		propose(value);
		if (r.compareAndSet(FIRST,i)){	
			return proposed[i];
		}
		else 
			return proposed[r.get()];
	}
	
	public TNode decide(TNode value){
		int i = ThreadID.get();
		propose(value);
		if (r.compareAndSet(FIRST,i)){
			return proposedT[i];
		}
		else 
			return proposedT[r.get()];
	}
}