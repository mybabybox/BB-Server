package customdata.file;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/3/15
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostsFileReader {
    private static final play.api.Logger logger = play.api.Logger.apply(PostsFileReader.class);

    public static final String COMM_KEY = "Community Name";
    public static final String TITLE_KEY = "Post Title";
    public static final String BODY_KEY = "Post Body";
    public static final String DATETIME_KEY = "DateTime";
    public static final String USERID_KEY = "UserId";

    private static final String DELIM = "\t";
    private static final DateTimeFormatter dtFmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");

    private List<PostEntry> posts = new ArrayList<>();

    /**
     * @return
     */
    public List<PostEntry> getPosts() {
        return posts;
    }

    /**
     * @param filePath
     */
    public void read(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        Map<Integer, String> headerMap = parseHeaderLine(br.readLine());

        String line;
        PostEntry lastPost = null;

        while ((line = br.readLine()) != null) {
            String[] row = line.split(DELIM);

            Long userId = null;
            String commName = null, title = null, body = null;
            DateTime dateTime = null;

            for (int i = 0; i < row.length; i++) {
                String header = headerMap.get(i);
                if (header == null) {
                    logger.underlyingLogger().error("Error in PostEntry, can't find header");
                }
                else {
                    String value = row[i];
                    if (value != null && !"".equals(value)) {
                        value = value.trim();

                        if (header.equals(COMM_KEY)) {
                            commName = value;
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
                Comment comment = new Comment();
                comment.body = body;
                comment.userId = userId;
                comment.dateTime = dateTime;
                lastPost.comments.add(comment);
            }
            else if (body == null && userId == null) {
                if (lastPost != null) {
                    posts.add(lastPost);
                }
                lastPost = null;
                continue;   // skip
            }
            // new post
            else {
                lastPost = new PostEntry();
                lastPost.commName = commName;
                lastPost.title = title;
                lastPost.body = body;
                lastPost.userId = userId;
                lastPost.dateTime = dateTime;
            }
        }
        br.close();

        if (lastPost != null) {
            posts.add(lastPost);
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


    public static class PostEntry {
        public String commName;
        public String title;
        public String body;
        public DateTime dateTime;
        public Long userId;
        public List<Comment> comments = new ArrayList<>();

        public boolean isCompleted() {
            return commName != null && title != null &&
                    body != null && dateTime != null && userId != null;
        }

        @Override
        public String toString() {
            return "PostEntry{" +
                    "commName='" + commName + '\'' +
                    ", title='" + title + '\'' +
                    ", body='" + body + '\'' +
                    ", dateTime=" + dateTime +
                    ", userId=" + userId +
                    ", comments=" + comments +
                    '}';
        }
    }

    public static class Comment {
        public String body;
        public DateTime dateTime;
        public Long userId;

        public boolean isCompleted() {
            return body != null && dateTime != null && userId != null;
        }

        @Override
        public String toString() {
            return "Comment{" +
                    ", body='" + body + '\'' +
                    ", dateTime=" + dateTime +
                    ", userId=" + userId +
                    '}';
        }
    }
}
