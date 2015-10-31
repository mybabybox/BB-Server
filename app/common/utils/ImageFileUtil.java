package common.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;
import play.Play;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Date: 28/6/14
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageFileUtil {
	private static final play.api.Logger logger = play.api.Logger.apply(ImageFileUtil.class);
	
    private static String IMAGE_TEMP_PATH = Play.application().configuration().getString("image.temp");

    static {
        if (!IMAGE_TEMP_PATH.endsWith("/")) {
            IMAGE_TEMP_PATH += "/";
        }
    }

    public static File copyImageFileToTemp(File file, String fileName) throws IOException {
        final File fileTo = new File(IMAGE_TEMP_PATH+fileName);
        crop(file).toFile(fileTo);
        //FileUtils.copyFile(file, fileTo);
        return fileTo;
    }

    public static void writeFileWithImage(File file, BufferedImage image) throws IOException {
        String ext = "jpg";
        if (file.getName().toLowerCase().endsWith("png")) {
            ext = "png";
        } else if (file.getName().toLowerCase().endsWith("gif")) {
            ext = "gif";
        }
        ImageIO.write(image, ext, file);
    }

    public static BufferedImage readImageFile(File file) throws IOException {
        // !!! Don't use ImageIO to read image file as it ignores EXIF information which may needs rotation
        //return ImageIO.read(file);        
        return Thumbnails.of(file).scale(1).asBufferedImage();
    }
    
    public static BufferedImage cropImageFile(File file) throws IOException {
        return crop(file).asBufferedImage();
    }
    
    private static Builder<File> crop(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        int width = img.getWidth();
        int height = img.getHeight();
        int dimension = (width < height)? width : height;
        
        return Thumbnails.of(file)
                .size(dimension, dimension)
                .crop(Positions.CENTER);
    }
}