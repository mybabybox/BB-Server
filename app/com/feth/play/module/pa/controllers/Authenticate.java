package com.feth.play.module.pa.controllers;

import java.security.Key;

import javax.crypto.Cipher;

import models.User;

import org.apache.commons.lang.StringUtils;

import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Result;
import Decoder.BASE64Encoder;

import com.feth.play.module.pa.PlayAuthenticate;

import controllers.Application;

public class Authenticate extends Controller {
	private static final play.api.Logger logger = play.api.Logger.apply(Authenticate.class);
	
	private static final String PAYLOAD_KEY = "p";
	
	public static void noCache(final Response response) {
		// http://stackoverflow.com/questions/49547/making-sure-a-web-page-is-not-cached-across-all-browsers
		response.setHeader(Response.CACHE_CONTROL, "no-cache, no-store, must-revalidate");  // HTTP 1.1
		response.setHeader(Response.PRAGMA, "no-cache");  // HTTP 1.0.
		response.setHeader(Response.EXPIRES, "0");  // Proxies.
	}

	@Transactional
	public static Result mobileAuthenticate(final String provider) {
		noCache(response());
		
		final String payload = getQueryString(request(), PAYLOAD_KEY);
		Result result = PlayAuthenticate.handleAuthentication(provider, ctx(), payload);
		play.api.mvc.Result wrappedResult = result.getWrappedResult();
		User user = null;
		if (wrappedResult instanceof play.api.mvc.PlainResult) {
			play.api.mvc.PlainResult plainResult = (play.api.mvc.PlainResult)wrappedResult;
		    session();
		    user = Application.getLocalUser(session());
		    int code = plainResult.header().status();
		    if (code == OK) {
		    	return ok();
		    }
		    // Cache
		    
		}
		
		String encryptedValue = null;
		String providerKey = session().get(PlayAuthenticate.PROVIDER_KEY);
		String userKey = session().get(PlayAuthenticate.USER_KEY);
		String plainData = providerKey + "-" + userKey;
		if (StringUtils.isEmpty(providerKey) || "null".equals(providerKey.trim()) || 
				StringUtils.isEmpty(userKey) || "null".equals(userKey.trim())) {
			logger.underlyingLogger().error((user == null? "" : "[u=" + user.id + "] ") + "mobileAuthenticate login failure key=" + plainData);
			return status(500);			
		}
		
		logger.underlyingLogger().info((user == null? "" : "[u=" + user.id + "] ") + "mobileAuthenticate key=" + plainData);
		try { 
    		Key key = Application.generateKey();
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(plainData.getBytes());
            encryptedValue = new BASE64Encoder().encode(encVal);
            logger.underlyingLogger().info((user == null? "" : "[u=" + user.id + "] ") + "mobileAuthenticate encryptedKey=" + encryptedValue);
    	} catch(Exception e) { 
    		logger.underlyingLogger().error((user == null? "" : "[u=" + user.id + "] ") + "mobileAuthenticate login failure key=" + plainData, e);
    		return status(500);
    	}
		
		// credit for first app login
		if (user != null) {
			//GameAccount.setPointsForAppLogin(user);
		}
		
		return ok(encryptedValue.replace("+", "%2b"));
	}
	
	@Transactional
	public static Result authenticate(final String provider) {
		noCache(response());
		
		final String payload = getQueryString(request(), PAYLOAD_KEY);
		return PlayAuthenticate.handleAuthentication(provider, ctx(), payload);
	}
	
    @Transactional
    public static Result authenticatePopup(final String provider) {
	    DynamicForm form = DynamicForm.form().bindFromRequest();
	
        String redirectURL = form.get("rurl");   // TODO: Need to get actual url from context object
        session().put(PlayAuthenticate.ORIGINAL_URL, redirectURL);
        noCache(response());

        final String payload = getQueryString(request(), PAYLOAD_KEY);
        return PlayAuthenticate.handleAuthentication(provider, ctx(), payload);
	}
	
	public static Result logout() {
		noCache(response());
		
		return PlayAuthenticate.logout(session());
	}

	// TODO remove on Play 2.1
	public static String getQueryString(final Request r, final Object key) {
		final String[] m = r.queryString().get(key);
		if(m != null && m.length > 0) {
			return m[0];
		}
		return null;
	}
}
