package customdata.file;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 22/2/15
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReviewFileReader {
    private static final play.api.Logger logger = play.api.Logger.apply(ReviewFileReader.class);

    public static final String PN_ID_KEY = "PN Id";
    public static final String DISTRICTID_KEY = "DistrictId";
    public static final String PN_KEY = "PN Name";
    public static final String TITLE_KEY = "Review Title";
    public static final String BODY_KEY = "Review Body";
    public static final String DATETIME_KEY = "DateTime";
    public static final String USERID_KEY = "UserId";

    private static final String DELIM = "\t";
    private static final DateTimeFormatter dtFmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");

    private List<ReviewEntry> reviews = new ArrayList<>();

    /**
     * @return
     */
    public List<ReviewEntry> getReviews() {
        return reviews;
    }

    /**
     * @param filePath
     */
    public void read(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        Map<Integer, String> headerMap = parseHeaderLine(br.readLine());

        String line;
        ReviewEntry lastReview = null;

        while ((line = br.readLine()) != null) {
            String[] row = line.split(DELIM);

            Long pnId = null, districtId = null, userId = null;
            String pnName = null, title = null, body = null;
            DateTime dateTime = null;

            for (int i = 0; i < row.length; i++) {
                String header = headerMap.get(i);
                if (header == null) {
                    logger.underlyingLogger().error("Error in review entry, can't find header");
                }
                else {
                    String value = row[i];
                    if (value != null && !"".equals(value)) {
                        value = value.trim();

                        if (header.equals(PN_ID_KEY)) {
                            pnId = Long.parseLong(value);
                        } else if (header.equals(DISTRICTID_KEY)) {
                            districtId = Long.parseLong(value);
                        } else if (header.equals(PN_KEY)) {
                            pnName = value;
                        } else if (header.equals(TITLE_KEY)) {
                            title = value;
                        } else if (header.equals(BODY_KEY)) {
                            body = value;
                        } else if (header.equals(DATETIME_KEY)) {
                            try {
                                dateTime = dtFmt.parseDateTime(value);
                            } catch (Exception e) {
                                logger.underlyingLogger().error("ParseDateException: "+value);
                            }
                        } else if (header.equals(USERID_KEY)) {
                            try {
                                userId = Long.parseLong(value);
                            } catch (NumberFormatException e) {
                                logger.underlyingLogger().error("NumberFormatException: "+value);
                            }
                        }
                    }
                }
            }

            // comment
            if (title == null && body != null) {
                ReviewComment comment = new ReviewComment();
                comment.body = body;
                comment.userId = userId;
                comment.dateTime = dateTime;
                lastReview.comments.add(comment);
            }
            else if (body == null && userId == null) {
                if (lastReview != null) {
                    reviews.add(lastReview);
                }
                lastReview = null;
                continue;   // skip
            }
            // new review
            else {
                lastReview = new ReviewEntry();
                lastReview.pnId = pnId;
                lastReview.districtId = districtId;
                lastReview.pnName = pnName;
                lastReview.title = title;
                lastReview.body = body;
                lastReview.userId = userId;
                lastReview.dateTime = dateTime;
            }
        }
        br.close();

        if (lastReview != null) {
            reviews.add(lastReview);
        }
    }

    private static Map<Integer, String> parseHeaderLine(String headerLine) {
        if (headerLine == null) {
            throw new IllegalStateException("Missing header");
        }

        Map<Integer, String> headerMap = new HashMap<>();
        String[] headers = headerLine.split(DELIM);
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(i, headers[i]);
        }
        return headerMap;
    }

    public static class ReviewEntry {
        public Long pnId;
        public Long districtId;
        public String pnName;
        public String title;
        public String body;
        public DateTime dateTime;
        public Long userId;
        public List<ReviewComment> comments = new ArrayList<>();

        public boolean isCompleted() {
            return pnId != null && districtId != null && pnName != null && title != null &&
                    body != null && dateTime != null && userId != null;
        }

        @Override
        public String toString() {
            return "ReviewEntry{" +
                    "pnId=" + pnId +
                    ", districtId=" + districtId +
                    ", pnName='" + pnName + '\'' +
                    ", title='" + title + '\'' +
                    ", body='" + body + '\'' +
                    ", dateTime=" + dateTime +
                    ", userId=" + userId +
                    ", comments=" + comments +
                    '}';
        }
    }

    public static class ReviewComment {
        public String body;
        public DateTime dateTime;
        public Long userId;

        public boolean isCompleted() {
            return body != null && dateTime != null && userId != null;
        }

        @Override
        public String toString() {
            return "ReviewComment{" +
                    ", body='" + body + '\'' +
                    ", dateTime=" + dateTime +
                    ", userId=" + userId +
                    '}';
        }
    }

    // Testing
    public static void main(String[] args) throws Exception {
        ReviewFileReader reader = new ReviewFileReader();
        reader.read("/Users/vichoty/Downloads/PNReviews-reviews.tsv");

        for (ReviewEntry review : reader.getReviews()) {
            System.out.println(review);
        }
    }
}
