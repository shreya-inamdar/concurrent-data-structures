public class AVLTree{

	//version manipulation constants
	// made long to int. justification - long bitwise operations weren't going well
	static int Unlinked = 1;
	static int Growing = 2;
	static int GrowCountIncr = 1 << 3;
	static int GrowCountMask = 0xff << 3;
	static int Shrinking = 4;
	static int ShrinkCountIncr = 1 << 11;
	static int IgnoreGrow = ~(Growing | GrowCountMask);
	final int unlink = -1;
	final int rotate = -2;
	final int noChange = -3;
	
	AVLNode rootHolder;

	//to return on detecting concurrent incompatible change
	static Object Retry = new Object();

	public AVLTree(){
		this.rootHolder = new AVLNode(-100);
	}

	public Integer get(int key){
		Integer ret = (Integer) attemptGet(key, rootHolder, 1, 0);
		return ret;
	}

	public Object attemptGet(int key, AVLNode node, int dir, int nodeV){
		//input key, node to get from, direction to explore - -1->left, +1->right, nodeV is the version passed by parent
		while(true){
			AVLNode child = node.child(dir);
			int a = versionMatch(nodeV,node);
			int b = a&IgnoreGrow; 
			if ( b != 0)
				return Retry;
			if (child == null)
				return -1;
			int nextD = key > child.key ? +1 : key < child.key ? -1 : 0;
			if (nextD == 0)
				return child.value;
			
			//else
			int chV = child.version;
			if (isShrinking(chV) != 0)
				waitUntilNotChanging(child);
			else if (chV != Unlinked && child == node.child(dir)){
				int a4 = (node.version^nodeV);
				int b4 = a4&IgnoreGrow;
				if( b4 != 0)
					return Retry;
				Object p = attemptGet(key, child, nextD, chV);
				if (p != Retry)
					return p;
			}

		}
	}

	public Integer put(int  key, int value){
		return ((Integer) attemptPut(key, value, rootHolder, 1, 0));
	}
	
	public Object attemptPut(int  key, int value, AVLNode node, int dir, int nodeV){
		Object p = Retry;
		do{
		//	if (node == rootHolder){
		//		System.out.println("printing rootholder details: \n value :" + rootHolder.getValue());
		//	}
			AVLNode child = node.child(dir);
			if (child != null)
				System.out.println(child.value);
			int a = versionMatch(nodeV, node);
			int b = a&IgnoreGrow;
			if( b != 0){
				return Retry;
			}	
			if (child == null){
				p = attemptInsert(key, value, node, dir, nodeV);
			}
			else{
					int nextD = key > child.key ? +1 : key < child.key ? -1 : 0;
					
					if(nextD == 0)
						p = attemptUpdate(child, value);
					else{
						
						int chV = child.version;
					//	if (isShrinking(chV) != 0){
					//		System.out.println("Infy here?");
					//		waitUntilNotChanging(child);
					//	}
					//	else
						if(chV != Unlinked && node.child(dir) == child){
							
							int a3 = versionMatch(nodeV,node);
							int b3 = a3&IgnoreGrow; 
							if ( b3 != 0){
								return Retry;
							}
						p = attemptPut(key, value, child, nextD, chV);
						}
					}
				}
		}while(p == Retry);
		
		return p;
	}
	
	public int isShrinking (int value){
		int a = value&2;
		if (a != 0)
			return 1;
		return 0;
	}
	
	public int versionMatch(int nodeV, AVLNode node){
		int a = nodeV^(node.version);
		if (a != 0)
			return 1;
		return 0;
	}
	
	
	public Object attemptInsert(int key, int value, AVLNode node, int dir, int nodeV){
		synchronized(node){
			int a = versionMatch(nodeV,node);	
			int b = a & IgnoreGrow; 
			if ( b != 0 || node.child(dir) != null)
				return Retry;
			node.setChild(dir, new AVLNode(key));
		}
		rootHolder.height = height(rootHolder);
		fixHeightAndRotate(rootHolder.right);
		return null;
	}
	
	public Object attemptUpdate(AVLNode node, int value){
		synchronized(node){
			if(node.version == Unlinked)
				return Retry;
				
			Object prev = node.value;
			node.value = value;
			return prev;
		}
	}

	public Integer remove(int key){
		return ((Integer) attemptRemove(key, rootHolder, 1, 0));
	}
	
	public Object attemptRemove(int key, AVLNode node, int dir, int nodeV){
			//similar to attempt put
			Object p = Retry;
			do{
				
				AVLNode child = node.child(dir);
				int a = versionMatch(nodeV,node);
				int b = a&IgnoreGrow;
				if ( b != 0)
				{
					return Retry;
				}
				if (child == null)
					return null;	//returns the previous value
				
				else{
					
					int nextD = key > child.key ? +1 : key < child.key ? -1 : 0;
					
					if(nextD == 0){
						p = attemptRmNode(node, child);
					}
					else{
						
						int chV = child.version;
					//	int a1 = isShrinking(chV);
					//	if (a1 != 0){
					//		waitUntilNotChanging(child);
					//	}
					//	else
						if(chV != Unlinked && node.child(dir) == child){
							
							int a2 = versionMatch(nodeV,node);
							int b2 = a2&IgnoreGrow; 
							if ( b2 != 0){
								return Retry;
							}
							
					//		p = attemptRmNode(child,child.child(nextD));
							System.out.println("key");
							p = attemptRemove(key, child, nextD, chV);
						}
					}
				}
				
			}while(p == Retry);
		return p;
	}

	public Object attemptRmNode(AVLNode par, AVLNode n){
		
		if(n == null)
			return null;
			
		Object prev;
		if (canUnlink(n) == 0){	// cannot unlink directly. has two children. set self value to null -> routing node
			
			synchronized(n){
				if (n.version == Unlinked || canUnlink(n) != 0){
					return Retry;
				}
				prev = n.value;
				n.value = -1;
			}
		}
		
		else{	//can unlink directly.
			synchronized(par){
				if (par.version == Unlinked || n.parent != par || n.version == Unlinked)
				{		//System.out.println("Details of node: " + "\n" + n.value + "\n" + n.parent.value + "\n" + n.version);
					System.out.println(par.version);
					System.out.println(n.parent != par);
					System.out.println(n.version);
					return Retry;
				}
				synchronized(n){	//get intrinsic lock on node. check for unlinkablility. 
					prev = n.value;
					n.value = -1;
					// if still unlinkable, change the pointer.
					if (canUnlink(n)!=0){
						AVLNode c = n.left == null ? n.right : n.left;
						if (par.left == n){
							par.left = c;
						}
						else{
							par.right = c;
						}
						
						if (c != null){
							c.parent = par;
							n.version = Unlinked;
						}
					}
				}
			}
			rootHolder.height = height(rootHolder);
			fixHeightAndRotate(rootHolder.right);
		}
		return prev;
	}
	

	public int canUnlink(AVLNode node){
		if (node != null)
		{	if (node.right == null || node.left == null)
				return  1;
			return 0;
		}
		return 1;
			
	}
////end of removal methods

//print tree method
	public void printTree(AVLNode c){
		//in-order print
		if(c == null){
			return;
		}
		else{
			printTree(c.left);
			System.out.println(c.value + " @ " + c.height );
			printTree(c.right);
		}
	}
////rotate method
////height and re-balancing
	public int height(AVLNode n){
		if (n == null) return 0;
		n.height = 1+ Math.max(height(n.left),height(n.right)); //self updating heights. volatile variables are synchronised
		return n.height;
	}
	
	public int nodeStatus(AVLNode n){
		if ((n.left == null || n.right == null) && n.value == -1 ){
			return unlink; //unlinkingRequired
		}
		//height balance check
		if (n.right != null && n.left != null){
			if (Math.abs(n.right.height - n.left.height) > 1){
				return rotate;
			}
			else return noChange;	//node is balanced. No need to rotate
		}
		else if(n.right == null){
			if (n.left == null || n.left.height <= 1)
				return noChange;
			else 
				return rotate;
		}
		else{
			if (n.right.height <= 1)
				return noChange;
			return rotate; 
		}
		//return noChange;
	}
	
	public void fixHeightAndRotate(AVLNode node){
		int status = nodeStatus(node);
		if (status == unlink){
				UnlinkNode(node);
			}

		else if (status == rotate){
			//check which type of rotation is required
			//balance = n.left.height - n.right.height
			//if n.balance >= +2: 
			////if n.left.balance <= 0: left rotation -> right rotation
			//else if n.balance <= -2: 
			////if n.right.balance >=0: right rotation -> left rotation
			if (node == null)
				return;
			synchronized(node){
				if (balance(node) >= 2)
				{	if(balance(node.left) < 0)
						rotateLeft(node.left);
					rotateRight(node);
				}
				else if(balance(node) <= -2){
					if(balance(node.right) > 0)
						rotateRight(node.right);
					rotateLeft(node);
				}
			}
		}
		else 
			return;
	}
	
	public int balance(AVLNode n){
		int left = n.left == null ? 0 : n.left.height;
		int right = n.right == null? 0: n.right.height;
		return left - right;
	}
	static int SpinCount = 100;
	public void waitUntilNotChanging(AVLNode n){
/*		int v = n.version;
		int temp = (Growing|Shrinking);
		temp = temp&v;
		if(temp != 0){
			int i =0 ; 
			while(n.version == v && i < SpinCount)
				++i;
			if (i == SpinCount)
				synchronized(n){};
		}
*/
	}

	public void UnlinkNode(AVLNode node){
		//get non-null child to point to parent's parent
		AVLNode newNode = node;
		if (node.right != null && node.left == null){
			node.right.parent = node.parent;
			newNode = node.right;//storing the node pointer for parent assignment
		}
		else if(node.left != null && node.right == null){
			node.left.parent = node.parent;
			newNode = node.left;
		}
		
		//get parent pointer to point to non-null child
		if (node.parent.right == node){
			node.parent.right = newNode;
		}
		else if(node.parent.left == node){
			node.parent.left = newNode;
		}
		//reset heights
		node.parent.height -= 1;
		//updating heights by running height function from root
		rootHolder.height = height(rootHolder.right)+1;		
	}
	
	public void rotateRight(AVLNode n){
		AVLNode nP = n.parent;
		AVLNode nL = n.left;
		AVLNode nLR = nL.right;
		
		synchronized(n){
			synchronized(nP){
				synchronized(nL){
					n.version |= Shrinking;
					nL.version |= Growing;
					
					n.left = nLR;
					nL.right = n;
					
					if (nP.left == n){
						nP.left = nL;
					}
					else{
						nP.right = nL; 
					}
					
					nL.parent = nP;
					n.parent = nL;
					if (nLR != null) nLR.parent = n;
					
					int h =  1 + Math.max(height(nLR), height(n.right));
					n.height = h;
					nL.height = 1 + Math.max(height(nL.left),h);
					
					nL.version += GrowCountIncr;
					n.version += ShrinkCountIncr;
				}
			}
		}
	}
	
	public void rotateLeft(AVLNode n){
		AVLNode nP = n.parent;
		AVLNode nR = n.right;
		AVLNode nRL = nR.left;
	
		synchronized(nP){
			synchronized(n){
				synchronized(nR){
					n.version |= Shrinking;
					nR.version |= Growing;
					
					n.right = nRL;
					nR.left = n;
					if (nP.left == n)
						nP.left = nR;
					else
						nP.right = nR;
					
					nR.parent = nP;
					n.parent = nR;
					if (nRL != null)
						nRL.parent = n;
					
					int h = 1+Math.max(height(nRL), height(n.left));
					n.height = h;
					nR.height = Math.max(h,height(nR.right));
					
					nR.version += GrowCountIncr;
					n.version += ShrinkCountIncr;
				}
			}
		}
	}
}