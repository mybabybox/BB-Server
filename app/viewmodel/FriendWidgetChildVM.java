package viewmodel;

import models.Location;

import org.codehaus.jackson.annotate.JsonProperty;

public class FriendWidgetChildVM {
	public Long id;
	public String dn;           // display name
	public Long nid;            // notification id
	public Location ln;
    @JsonProperty("foN") public String frdOfName;       // friend of display name
	@JsonProperty("isf") public boolean isFriend;
	@JsonProperty("isP") public boolean isFriendRequestPending;
	
	public FriendWidgetChildVM(Long id, String dn, Location ln) {
		this(id, dn, 0L, ln, null);
	}

    public FriendWidgetChildVM(Long id, String dn, Location ln, String frdOfName) {
		this(id, dn, 0L, ln, frdOfName);
	}
	
	public FriendWidgetChildVM(Long id, String dn, Long nid) {
        this(id, dn, nid, null, null);
    }
	
	public FriendWidgetChildVM(Long id, String dn, Long nid, Location ln, String frdOfName) {
		this.id = id;
		this.dn = dn;
		this.nid = nid;
		this.ln = ln;
        this.frdOfName = frdOfName;
	}
}
