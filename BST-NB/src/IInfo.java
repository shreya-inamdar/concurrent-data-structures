
public class IInfo extends Info{
	
	Internal newInternal;
	
	public IInfo(Internal p, Leaf l, Internal newInternal ){
		super(p,l);
		this.newInternal = newInternal;
	}
}
