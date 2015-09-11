package providers;

import providers.MyUsernamePasswordAuthProvider.MySignup;

import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.FirstLastNameIdentity;
import com.feth.play.module.pa.user.NameIdentity;

public class MyUsernamePasswordAuthUser extends UsernamePasswordAuthUser
		implements NameIdentity, FirstLastNameIdentity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String fname;
	private final String lname;


	public MyUsernamePasswordAuthUser(final MySignup signup) {
		super(signup.password, signup.email);
		this.fname = signup.fname;
		this.lname = signup.lname;
	}

	/**
	 * Used for password reset only - do not use this to signup a user!
	 * @param password
	 */
	public MyUsernamePasswordAuthUser(final String password) {
		super(password, null);
		fname = null;
		lname = null;
	}

	@Override
	public String getName() {
		return fname + " " + lname;
	}

	@Override
	public String getFirstName() {
		
		return fname;
	}

	@Override
	public String getLastName() {
		
		return lname;
	}
}
