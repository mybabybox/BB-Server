package babybox.shopping.social.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import play.libs.Akka;
import scala.concurrent.duration.Duration;
import akka.actor.Cancellable;
import babybox.shopping.social.utils.MailJob.Mail.Body;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

public class MailJob implements Runnable {

    private static final String MAIL_FROM = "info@baby-box.com.hk";

	private final Mail mail;

	public MailJob(final Mail m) {
		mail = m;
	}
	
	public static Cancellable sendMail(final Mail email) {
		email.setFrom(MAIL_FROM);
		return Akka
				.system()
				.scheduler()
				.scheduleOnce(Duration.create(1, TimeUnit.SECONDS), new MailJob(email),
						Akka.system().dispatcher());
	}

	public static Cancellable sendMail(final String subject, final Body body,
			final String recipient) {
		final Mail mail = new Mail(subject, body, new String[] { recipient });
		return sendMail(mail);
	}
	
	@Override
	public void run() {
		
		final MailerAPI api = play.Play.application().plugin(MailerPlugin.class).email();
        api.setCharset("UTF-8");
		api.setSubject(mail.getSubject());
		api.addRecipient(mail.getRecipients());
		api.addFrom(mail.getFrom());

		for (final Entry<String, List<String>> entry : mail
				.getCustomHeaders().entrySet()) {
			final String headerName = entry.getKey();
			for (final String headerValue : entry.getValue()) {
				api.addHeader(headerName, headerValue);
			}
		}

		if (mail.getBody().isBoth()) {
			api.send(mail.getBody().getText(), mail.getBody().getHtml());
		} else if (mail.getBody().isText()) {
			api.send(mail.getBody().getText());
		} else {
			api.sendHtml(mail.getBody().getHtml());
		}
	}
		
	
	
	public static class Mail {

		public static class Body {
			private final String html;
			private final String text;
			private final boolean isHtml;
			private final boolean isText;

			public Body(final String text) {
				this(text, null);
			}

			public Body(final String text, final String html) {
				this.isHtml = html != null && !html.trim().isEmpty();
				this.isText = text != null && !text.trim().isEmpty();

				if (!this.isHtml && !this.isText) {
					throw new RuntimeException(
							"Text and HTML cannot both be empty or null");
				}
				this.html = (this.isHtml) ? html : null;
				this.text = (this.isText) ? text : null;
			}

			public boolean isHtml() {
				return isHtml;
			}

			public boolean isText() {
				return isText;
			}

			public boolean isBoth() {
				return isText() && isHtml();
			}

			public String getHtml() {
				return html;
			}

			public String getText() {
				return text;
			}
		}
		
		private final String subject;
		private final String[] recipients;
		private String from;
		private final Body body;
		private final Map<String, List<String>> customHeaders;

		public Mail(final String subject, final Body body,
				final String[] recipients) {
			this(subject, body, recipients, null);
		}

		public Mail(final String subject, final Body body,
				final String[] recipients,
				final Map<String, List<String>> customHeaders) {
			if (subject == null || subject.trim().isEmpty()) {
				throw new RuntimeException("Subject must not be null or empty");
			}
			this.subject = subject;

			if (body == null) {
				throw new RuntimeException("Body must not be null or empty");
			}

			this.body = body;

			if (recipients == null || recipients.length == 0) {
				throw new RuntimeException(
						"There must be at least one recipient");
			}
			this.recipients = recipients;

			if (customHeaders != null) {
				this.customHeaders = customHeaders;
			} else {
				this.customHeaders = Collections.emptyMap();
			}
		}

		public String getSubject() {
			return subject;
		}

		public String[] getRecipients() {
			return recipients;
		}

		public String getFrom() {
			return from;
		}

		private void setFrom(final String from) {
			this.from = from;
		}

		public Body getBody() {
			return body;
		}

		public Map<String, List<String>> getCustomHeaders() {
			return customHeaders;
		}

		public void addCustomHeader(String name, String... values) {
			this.customHeaders.put(name, Arrays.asList(values));
		}
	}

	
}
