package common.image;

/**
 * Created by IntelliJ IDEA.
 * Date: 2/8/14
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ImageDimensions {

    public static final int PROFILE_FULL = 150 * 2;
    public static final int PROFILE_THUMBNAIL = 85 * 2;
    public static final int PROFILE_MINI = 40 * 2;

    public static final int COVERPHOTO_FULL_WIDTH = 580;
    public static final int COVERPHOTO_THUMBNAIL_WIDTH = 320;
    public static final int COVERPHOTO_MINI_WIDTH = 120;

    public static final int LIGHTBOX_WIDTH_PX = (int) (880 * 1.3d);
    public static final int LIGHTBOX_HEIGHT_PX = (int) (620 * 1.3d);

    public static final int POST_IMAGE_PREVIEW_WIDTH_PX = 350;
    public static final int POST_IMAGE_PREVIEW_HEIGHT_PX = 350;

    public static final int COMMENT_IMAGE_PREVIEW_HEIGHT_PX = (int) (150 * 1.1d);

    public static final int PM_IMAGE_PREVIEW_WIDTH_PX = (int) (150);
    public static final int PM_IMAGE_PREVIEW_HEIGHT_PX = (int) (150);

}
