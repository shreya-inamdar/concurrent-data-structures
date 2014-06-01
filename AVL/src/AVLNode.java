public class AVLNode{


	final int key;
	volatile int value;
	volatile AVLNode parent;
	volatile AVLNode right;
	volatile AVLNode left;
	public volatile int height;
	volatile int version;

	public AVLNode(int key){
		this.key = key;
		this.value = key;
		this.right = null;
		this.left = null;
		this.parent = null;
	}

	public AVLNode child(int dir){
		if (dir == 1)
			return this.right;
		return this.left;
	}

	public void setChild(int dir, AVLNode newChild){
		synchronized(this){
			if (dir == 1){
				this.right = newChild;
			}
			else
				this.left = newChild;

			newChild.parent = this;
			newChild.height = 0;
			
		}
	}

	public int getValue(){
		return this.value;
	}
}