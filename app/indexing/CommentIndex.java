package indexing;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.github.cleverage.elasticsearch.Index;
import com.github.cleverage.elasticsearch.IndexUtils;
import com.github.cleverage.elasticsearch.Indexable;
import com.github.cleverage.elasticsearch.annotations.IndexType;

@IndexType(name = "comments")
public class CommentIndex extends Index{
	
	public static Finder<CommentIndex> find = new Finder<CommentIndex>(CommentIndex.class);
	
	@JsonProperty("post_id") public Long post_id;
	@JsonProperty("comment_id")	public Long comment_id;
	@JsonProperty("oid") public Long owner_id;
	@JsonProperty("on") public String name;
	@JsonProperty("cd") public Long creationDate;
	@JsonProperty("commentText") public String commentText;
	
	@Override
	public Indexable fromIndex(Map map) {
		if (map == null) {
            return this;
        }
		this.post_id = (Long) IndexUtils.convertValue(map.get("post_id"), Long.class);
		this.comment_id = (Long) IndexUtils.convertValue(map.get("comment_id"), Long.class);
		this.owner_id = (Long) IndexUtils.convertValue(map.get("oid"), Long.class);
		this.name = (String) map.get("on");
		this.creationDate = (Long) IndexUtils.convertValue(map.get("cd"),Long.class);
		this.commentText = (String) map.get("commentText");
		return this;
	}

	@Override
	public Map toIndex() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("post_id", post_id);
		map.put("comment_id", comment_id);
		map.put("commentText", commentText);
		map.put("oid", owner_id);
		map.put("on", name);
		map.put("cd", creationDate);
		return map;
	}
}
