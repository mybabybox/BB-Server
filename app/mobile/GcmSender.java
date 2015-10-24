package mobile;

import models.Gcm;

import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;

/**
 * Created by IntelliJ IDEA.
 * Date: 20/9/15
 * Time: 5:33 PM
 */
public class GcmSender {
    private static final play.api.Logger logger = play.api.Logger.apply(GcmSender.class);

    private static final String AUTHORIZATION_KEY = "AIzaSyD0WLyJEeiZpAof0WwxbLaQJB0Dzp7ToHg"; // 

    private static final int TTL = 30;
    private static final int RETRIES = 2;

    public static void sendNotification(Long userId, String msg){
        Gcm gcm = Gcm.findByUserId(userId);
        if (gcm != null) {
            sendToGcm(userId, gcm.getReg_id() ,msg);
        } else {
            logger.underlyingLogger().info("[u="+userId+"] User does not have Gcm reg");
        }
    }

    private static boolean sendToGcm(Long userId, String regId, String msg) {
        try {
            Sender sender = new Sender(AUTHORIZATION_KEY);
            Message message = new Message.Builder().timeToLive(TTL).collapseKey("message")
                    .delayWhileIdle(true)
                    .addData("message", msg).build();

            Result result = sender.send(message, regId, RETRIES);
            logger.underlyingLogger().info("[u="+userId+"] Gcm send result("+regId+"): "+result);
            return true;
        }
        catch (Exception e) {
            logger.underlyingLogger().error("[u="+userId+"] Error in Gcm send", e);
            System.out.println("exception in gcm");
            return false;
        }
    }
}
