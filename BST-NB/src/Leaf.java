
public class Leaf extends Node{

	int key;
	
	public Leaf(int key) {
		super(key);
		super.type = Type.EXTERNAL;
	}
	
}
