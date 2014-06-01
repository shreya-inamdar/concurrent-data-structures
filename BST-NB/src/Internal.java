import java.util.concurrent.atomic.*;
public class Internal extends Node {
	
	public AtomicMarkableReference<Update> update;
	
	
	public Internal(int key){
		super(key);
		update = new AtomicMarkableReference<Update>(new Update(State.CLEAN, new Info(null, null)),true);
		super.type = Type.INTERNAL;
	}
}
