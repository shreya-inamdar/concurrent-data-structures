import java.util.concurrent.atomic.*;

public class Tree {
	public Internal root;
	public static int Inf1 = 10001;
	public static int Inf2 = 10000; 
	
	//init tree with inf1 and inf2
	public Tree(){
		root = new Internal(Inf1);
		root.left = new AtomicMarkableReference<Node>(new Leaf(Inf2),true);
		root.right = new AtomicMarkableReference<Node>(new Leaf(Inf1),true);
	}
	
	public SearchRet Search(int key){
		//returns the nearest leaf node to the key value
		Internal gp=root, p=root;
		Internal l = root;
		Leaf L=null;
		Update pupdate = p.update.getReference();
		Update gpupdate = gp.update.getReference();
		while(l.type == Type.INTERNAL ){
			gp = p;
			p = l;
			gpupdate = pupdate;
			pupdate = p.update.getReference();
			if(key < l.key){
				if(l.left.getReference() instanceof Internal){					
					l = (Internal)l.left.getReference();					
				}
				else if(l.left.getReference() instanceof Leaf ){
					L = (Leaf) (l.left.getReference());
					L = new Leaf(l.left.getReference().key);
					L.key = l.left.getReference().key;
					L.type = Type.EXTERNAL;
					break;
				}
			}
			else{
				if(l.right.getReference().type == Type.INTERNAL)
					l = (Internal)l.right.getReference();
				else if(l.right.getReference().type == Type.EXTERNAL){ 
					L = (Leaf) l.right.getReference();
					L.key = l.right.getReference().key;
					L.type = Type.EXTERNAL;
					break;
				}
			}
		}
		SearchRet result = new SearchRet(gp,p,L,pupdate, gpupdate);
		return result; 
	}	
	
	public boolean find(int key){
		SearchRet result = Search(key);
		if (result.l.key == key)
			return true;
		return false;		
	}
	
	public boolean insert(int key){
		while(true){
			SearchRet result = Search(key);
			if (result.l.key == key){
				return false; 
			}
			if (result.pupdate.state != State.CLEAN)
				help(result.pupdate);
			else{
				Leaf newSibling = new Leaf(result.l.key);
				Leaf newNode = new Leaf(key);
				Internal newInternal = new Internal(Math.max(result.l.key, key));
			
				if(key < result.l.key){
					newInternal.left.set(newNode, true);
					newInternal.right.set(newSibling, true);				
				}
				else{
					newInternal.left.set(newSibling, true);
					newInternal.right.set(newNode, true);
				}
				IInfo op = new IInfo(result.p, result.l, newInternal);
				boolean output = result.p.update.compareAndSet(result.pupdate, new Update(State.iFLAG,op),true, true);///doubt prove correctness
				if (output == true){
					helpInsert(op);	
					return true;
				}
				else
					help(result.pupdate);
			}
		}
	}
	
	public void CASChild(Internal parent, Node old, Node newNode){
				
		if(newNode.key < parent.key){
			parent.left.compareAndSet(old, newNode, true, true);			
		}
		else{
			parent.right.compareAndSet(old, newNode, true, true);
		}
	}
	
	public void helpInsert(IInfo op){
		CASChild(op.p, op.l, op.newInternal);
		op.p.update.set(new Update(State.CLEAN,op), true);
	}
	
	
	public void help(Update update){
		if(update.state == State.iFLAG)
			helpInsert((IInfo)update.info);
		else if(update.state == State.MARK)
			helpMarked((DInfo)update.info);
		else if (update.state == State.dFLAG)
			helpDelete((DInfo)update.info);
	}
	
	public boolean delete(int key){
		
		SearchRet result;
		while(true){			
			result = Search(key);
			if (result.l.key != key)
				return false;
			if (result.gpupdate.state != State.CLEAN){
				help(result.gpupdate);
			}
				
			
			else if(result.pupdate.state != State.CLEAN){
				help(result.pupdate);
			}
			
			else{
				DInfo op = new DInfo(result.p, result.gp, result.l, result.pupdate, result.gpupdate );
				boolean out = op.gp.update.compareAndSet(result.gpupdate, new Update(State.dFLAG,op), true, true);
				if (out)
				{	if (helpDelete(op))
						return true;
				}
				else
					help(result.gpupdate);
			}
		}
	}
	
	public boolean helpDelete(DInfo op){
	
		boolean res = op.p.update.compareAndSet(op.pupdate, new Update(State.MARK,op), true, true);
		if(res==true){
			helpMarked(op);
			return true;
		}
		else{
			help(op.pupdate);
			op.gp.update.compareAndSet(op.gp.update.getReference(), new Update(State.CLEAN,op), true, true);
			return false;
		}
	}
	
	public void helpMarked(DInfo op){
		if(op != null){
			Node other;
			if(op.p.right.getReference() == op.l)
				other = op.p.left.getReference();
			else
				other = op.p.right.getReference();
			
			CASChild(op.gp, op.p,other);
			op.gp.update.compareAndSet(op.gp.update.getReference(), new Update(State.CLEAN,op), true, true); 
		}
	}
	
	
	
	void printTree(Internal n){
		if (n.left.getReference().type == Type.EXTERNAL)
			System.out.println(n.left.getReference().key + " c/o " + n.key);
		else
			printTree((Internal) n.left.getReference());
		
		if(n.right.getReference().type == Type.EXTERNAL)
			System.out.println(n.right.getReference().key + " c/o " + n.key);
		else
			printTree((Internal) n.right.getReference());	
	}
}
