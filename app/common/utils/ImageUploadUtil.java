package common.utils;

import org.joda.time.DateTime;

import play.Play;

public class ImageUploadUtil {
    private static final String STORAGE_PATH = Play.application().configuration().getString("storage.path"); 

    private static final String IMAGE_URL_PREFIX =
            Play.application().configuration().getString("image.url.prefix", "/image");

    //private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HHmmss");

    private String uploadType;
    
    public ImageUploadUtil(String uploadType) {
        this.uploadType = uploadType;
    }
    
    public String getImagePath(Long year, Long month, Long date, String name) {
        return STORAGE_PATH + "/" + uploadType + "/" + year + "/" + month + "/" + date + "/" + name;
    }

    public String getImagePath(DateTime dateTime, String rawFileName) {
        //String name = timeFormatter.print(dateTime)+"_"+rawFileName;
        return getImagePath(Long.valueOf(dateTime.getYear()), Long.valueOf(dateTime.getMonthOfYear()), Long.valueOf(dateTime.getDayOfMonth()), rawFileName);
    }

    public String getImageUrl(DateTime dateTime, String rawFileName) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthOfYear();
        int date = dateTime.getDayOfMonth();
        //String name = timeFormatter.print(dateTime)+"_"+rawFileName;
        return IMAGE_URL_PREFIX + "/" + uploadType + "/" + year + "/" + month + "/" + date + "/" + rawFileName;
    }
}