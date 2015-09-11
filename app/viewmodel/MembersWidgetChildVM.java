package viewmodel;

public class MembersWidgetChildVM {
	public Long id;
	public String dn;
	public boolean isAdmin;
	public MembersWidgetChildVM(Long id, String dn,boolean isAdmin) {
		this.id = id;
		this.dn = dn;
		this.isAdmin = isAdmin;
	}
}
