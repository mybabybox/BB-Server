package mobile;

import play.Play;
import models.GcmToken;

import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;

public class GcmSender {
    private static final play.api.Logger logger = play.api.Logger.apply(GcmSender.class);

    public static final String MESSAGE_KEY = "message";
    
    public static final String API_SERVER_KEY = Play.application().configuration().getString("gcm.api.server.key");
    
    private static final int TTL = 30;
    private static final int RETRIES = 2;
    
    public static enum NotificationType {
        CONVERSATION,
        COMMENT
    }

    public static void sendNotification(Long userId, String msg){
        GcmToken gcmToken = GcmToken.findByUserId(userId);
        if (gcmToken != null) {
            sendToGcm(userId, gcmToken.getRegId(), msg);
        } else {
            logger.underlyingLogger().info("[u="+userId+"] User does not have Gcm reg");
        }
    }

    private static boolean sendToGcm(Long userId, String regId, String msg) {
        try {
            Sender sender = new Sender(API_SERVER_KEY);
            Message message = new Message.Builder().timeToLive(TTL)
                    .collapseKey(MESSAGE_KEY)
                    .delayWhileIdle(true)
                    .addData(MESSAGE_KEY, msg).build();

            Result result = sender.send(message, regId, RETRIES);
            logger.underlyingLogger().info("[u="+userId+"][regId="+regId+"][msg="+msg+"] Gcm send result: "+result);
            return true;
        } catch (Exception e) {
            logger.underlyingLogger().error("[u="+userId+"] Error in Gcm send", e);
            return false;
        }
    }
}
