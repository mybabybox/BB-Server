package common.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang.exception.ExceptionUtils;

public class SimpleSerializer {
    private static play.api.Logger logger = play.api.Logger.apply(SimpleSerializer.class);
    
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (oos != null)
                    oos.close();
                if (baos != null)
                    baos.close();
            } catch (IOException ioe) {
            }
        }
        return null;
    }
     
    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (bais != null)
                    bais.close();
            } catch (IOException ioe) {
            }
        }
        return null;
    }
}
