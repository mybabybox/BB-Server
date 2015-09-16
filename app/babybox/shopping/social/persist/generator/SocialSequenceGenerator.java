package babybox.shopping.social.persist.generator;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

public class SocialSequenceGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(SessionImplementor arg0, Object arg1)
			throws HibernateException {
		
		return Math.round(System.currentTimeMillis() * (Math.random()*10000));
	}

}
