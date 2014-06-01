
public class DInfo extends Info{

	public Internal gp;
	public Update pupdate;
	
	public DInfo(Internal p, Internal gp, Leaf l, Update pupdate, Update gpupdate){
		super(p,l);		
		this.gp = gp;
		this.pupdate = pupdate;
		this.p = p;
		this.l = l;
		this.p.update.set(pupdate, true);
		this.gp.update.set(gpupdate, true);
	}
}
