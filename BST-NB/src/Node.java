import java.util.concurrent.atomic.AtomicMarkableReference;


public class Node{
		public int key;
		public Type type;
		public AtomicMarkableReference<Node> left;
		public AtomicMarkableReference<Node> right;
		
		public Node(int key){
			this.key = key;
			left = new AtomicMarkableReference<Node>(null, true);
			right = new AtomicMarkableReference<Node>(null, true);
		}
}
