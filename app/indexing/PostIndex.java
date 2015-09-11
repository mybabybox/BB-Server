package indexing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.github.cleverage.elasticsearch.Index;
import com.github.cleverage.elasticsearch.IndexUtils;
import com.github.cleverage.elasticsearch.Indexable;
import com.github.cleverage.elasticsearch.annotations.IndexType;

@IndexType(name = "posts")
public class PostIndex extends Index{

	public static Finder<PostIndex> find = new Finder<PostIndex>(PostIndex.class);
	
	@JsonProperty("community_id") public Long community_id;
	@JsonProperty("cn") public String communityName;
	@JsonProperty("post_id") public Long post_id;
	@JsonProperty("oid") public Long owner_id;
	@JsonProperty("description") public String description;
	@JsonProperty("comments") public List<CommentIndex> comments = new ArrayList<CommentIndex>();
	
	@JsonProperty("p") public String postedBy;
	@JsonProperty("t") public Date postedOn;
	@JsonProperty("n_c") public int noOfComments;
	
	@JsonProperty("hasImg") public boolean hasImages = false;
	@JsonProperty("post_img_folder") public Long folder_id;
			
	@Override
	public Indexable fromIndex(Map map) {
		if (map == null) {
            return this;
        }
		this.community_id = (Long) IndexUtils.convertValue(map.get("community_id"), Long.class);
		this.communityName = (String) map.get("cn"); 
		this.post_id = (Long) IndexUtils.convertValue(map.get("post_id"), Long.class);
		this.owner_id = (Long) IndexUtils.convertValue(map.get("oid"), Long.class);
		this.description = (String) map.get("description");
		this.comments = IndexUtils.getIndexables(map, "comments", CommentIndex.class);
		this.postedBy = (String) map.get("p");
		this.postedOn = (Date) IndexUtils.convertValue(map.get("t"), Date.class);
		this.noOfComments = (int) map.get("n_c");
		this.hasImages = (boolean) map.get("hasImg");
		this.folder_id = (Long) IndexUtils.convertValue(map.get("post_img_folder"), Long.class);
		return this;
	}

	@Override
	public Map toIndex() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("community_id", community_id);
		map.put("cn", communityName);
		map.put("post_id", post_id);
		map.put("oid", owner_id);
		map.put("description", description);
		map.put("comments", IndexUtils.toIndex(comments));
		map.put("p", postedBy);
		map.put("t", postedOn);
		map.put("n_c", noOfComments);
		map.put("post_img_folder", folder_id);
		map.put("hasImg", hasImages);
		return map;
	}

}
