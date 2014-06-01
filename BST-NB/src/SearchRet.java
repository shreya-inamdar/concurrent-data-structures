
public class SearchRet {
		Internal gp;
		Internal p;
		Update pupdate;
		Update gpupdate;
		Leaf l;
		
		public SearchRet(Internal gp, Internal p, Leaf l, Update pupdate, Update gpupdate ){
			this.gp = gp;
			this.p = p;
			this.l = l;
			this.pupdate = pupdate;
			this.gpupdate = gpupdate;
		}
}
