package email;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import play.Configuration;
import play.Logger;
import play.Play;
import akka.actor.Cancellable;

import com.feth.play.module.mail.Mailer;
import com.feth.play.module.mail.Mailer.Mail;
import com.feth.play.module.mail.Mailer.Mail.Body;
import com.feth.play.module.pa.PlayAuthenticate;

/**
 * EDMUtility (via emails)
 */
public class EDMUtility {
	protected static final String PROVIDER_KEY = "password";
	protected static final String SETTING_KEY_MAIL = "mail";

    /**
     * @param email
     * @param promoCode
     */
	public void sendMailInvitationToUser(final String email, String promoCode) {
		//final boolean isSecure = getConfiguration().getBoolean(SETTING_KEY_VERIFICATION_LINK_SECURE);
	    //final String url = routes.Signup.verify(token).absoluteURL(ctx.request(), isSecure);

		final String text = getEmailTemplate(
				"views.html.account.email.invitation_mail",
				email,promoCode);
		
		final String html = getEmailTemplate(
				"views.html.account.email.invitation_mail",
				email,promoCode);

		Body body =  new Body(text, html);
		
		sendMail("BabyBox Invitation", body, email);
	}

    /**
     * @param subject
     * @param bodyText
     */
    public void sendMailToMB(final String subject, final String bodyText) {
        Body body =  new Body(bodyText);
        sendMail(subject, body, Play.application().configuration().getString("smtp.user"));
    }

    /**
     * @param subject
     * @param bodyText
     * @param email
     */
    public void sendMail(final String subject, final String bodyText, String email) {
        Body body =  new Body(bodyText);
        sendMail(subject, body, email);
    }

	protected Cancellable sendMail(final String subject, final Body body,
			final String recipient) {
		return sendMail(new Mail(subject, body, new String[] { recipient }));
	}
	
	protected Cancellable sendMail(final Mail mail) {
		//Mailer mailer = Mailer.getDefaultMailer();
		Configuration config = PlayAuthenticate.getConfiguration().getConfig(PROVIDER_KEY).getConfig(
				SETTING_KEY_MAIL);
		Mailer mailer = Mailer.getCustomMailer(config);
		
		
		if(mailer == null) return null;
		return mailer.sendMail(mail);
	}
	
	/*protected Configuration getConfiguration() {
		return getConfiguration1().getConfig(getKey());
	}*/
	
	protected String getEmailTemplate(final String template, final String email, String promoCode) {
		Class<?> cls = null;
		String ret = null;
		try {
			cls = Class.forName(template);
		} catch (ClassNotFoundException e) {
			Logger.warn("Template: '"
					+ template
					+ "' was not found! Trying to use English fallback template instead.");
		}
		if (cls == null) {
			try {
				cls = Class.forName(template);
			} catch (ClassNotFoundException e) {
				Logger.error("Fallback template: '" + template 
						+ "' was not found either!");
			}
		}
		if (cls != null) {
			Method htmlRender = null;
			try {
				String url = controllers.Application.APPLICATION_BASE_URL +"/signup-code/"+ promoCode;
				htmlRender = cls.getMethod("render", String.class, String.class);
				ret = htmlRender.invoke(null,  email ,url)
						.toString();

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	protected String getEmailTemplate(final String template, final String name) {
		Class<?> cls = null;
		String ret = null;
		try {
			cls = Class.forName(template);
		} catch (ClassNotFoundException e) {
			Logger.warn("Template: '"
					+ template
					+ "' was not found! Trying to use English fallback template instead.");
		}
		if (cls == null) {
			try {
				cls = Class.forName(template);
			} catch (ClassNotFoundException e) {
				Logger.error("Fallback template: '" + template 
						+ "' was not found either!");
			}
		}
		if (cls != null) {
			Method htmlRender = null;
			try {
				htmlRender = cls.getMethod("render",String.class);
				ret = htmlRender.invoke(null, name)
						.toString();

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}


    private static EDMUtility instance = new EDMUtility();

    public static EDMUtility getInstance() {
        return instance;
    }
}
