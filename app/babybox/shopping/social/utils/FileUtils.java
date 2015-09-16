package babybox.shopping.social.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import play.Play;

public class FileUtils {

    private static final String STORAGE_IMAGES_VALID_EXT = 
            Play.application().configuration().getString("storage.images.valid-extensions");
    
    public static Boolean isImage(String filename) {
        if(filename != null) {
            String ext = FilenameUtils.getExtension(filename);
            String[] valids = StringUtils.split(STORAGE_IMAGES_VALID_EXT, ",");
            for (String valid : valids) {
                if(valid.equalsIgnoreCase(ext)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean isExternal(String filename) {
        return filename.startsWith("http");
    }
}
