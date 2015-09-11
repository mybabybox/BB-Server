package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

public class BookmarkSummaryVM {
    @JsonProperty("b") public boolean hasBookmarks;
	@JsonProperty("qc") public long qnaBookmarkCount;
	@JsonProperty("pc") public long postBookmarkCount;
	@JsonProperty("ac") public long articleBookmarkCount;
	@JsonProperty("pkc") public long pkViewBookmarkCount;
	
	public BookmarkSummaryVM(long qnaBookmarkCount, long postBookmarkCount, long articleBookmarkCount, long pkViewBookmarkCount) {
	    this.hasBookmarks = (qnaBookmarkCount + postBookmarkCount + articleBookmarkCount + pkViewBookmarkCount) > 0;
	    this.qnaBookmarkCount = qnaBookmarkCount;
	    this.postBookmarkCount = postBookmarkCount;
	    this.articleBookmarkCount = articleBookmarkCount;
	    this.pkViewBookmarkCount = pkViewBookmarkCount;
	}
}
