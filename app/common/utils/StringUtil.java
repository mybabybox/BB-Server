package common.utils;

import domain.DefaultValues;

import java.io.*;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringUtil {

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String shortMessage(String str) {
		int count = DefaultValues.SHORT_MESSAGE_COUNT;
		if (str.length() <= count) {
			return str;
		}
		return str.substring(0, count + 1) + " ...";
	}
	
    /**
     * @param collection
     * @param separator
     * @return
     */
    public static String collectionToString(Collection collection, String separator) {
        if (collection == null || collection.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (Object obj : collection) {
            sb.append(delim).append(obj);
            delim = separator;
        }
        return sb.toString();
    }

    /**
     * @param text
     * @return
     */
    public static String removeNonDigits(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("[^0-9]", "");
    }

    /**
     * @param text
     * @param numChars
     * @return
     */
    public static String truncateWithDots(String text, int numChars) {
        if (text == null) {
            return null;
        }
        if (text.length() <= numChars) {
            return text;
        }
        return text.substring(0, numChars)+"...";
    }


    /**
     * @param text
     * @return
     */
    public static int computePostShortBodyCount(String text) {
        int shortBodyCount;
        if (text.length() >= DefaultValues.POST_PREVIEW_CHARS){
	        String shortDesc = text.substring(DefaultValues.POST_PREVIEW_CHARS);

	        if (shortDesc.lastIndexOf("<img") == -1){
	        	shortBodyCount = DefaultValues.POST_PREVIEW_CHARS;
	        } else {
	        	if(shortDesc.lastIndexOf("<img") < shortDesc.lastIndexOf("/>")){
	        		shortBodyCount = DefaultValues.POST_PREVIEW_CHARS;
		        } else {
		        	shortDesc.substring(shortDesc.lastIndexOf("<img")-1);
		        	shortBodyCount = shortDesc.length();     // dont include emoticon if chopped off
		        }
	        }
        } else {
        	shortBodyCount = 0;
        }
        return shortBodyCount;
    }

    public static String compress(String in) {
        if (in == null || "".equals(in)) {
            return in;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(in.getBytes("ISO-8859-1"));
            gzip.flush();
            gzip.finish();
            gzip.close();
            return baos.toString("ISO-8859-1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decompress(String in) {
        if (in == null || "".equals(in)) {
            return in;
        }
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(in.getBytes("ISO-8859-1")));
            InputStreamReader ir = new InputStreamReader(gzip, "ISO-8859-1");
            StringWriter sw = new StringWriter();
            char[] buffer = new char[10240];
            for (int len; (len = ir.read(buffer)) > 0; ) {
                sw.write(buffer, 0, len);
            }
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
