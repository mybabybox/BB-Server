package viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import models.Category;


public class CategoryVM {
	@JsonProperty("id") public Long id;
	@JsonProperty("icon") public String icon;
	@JsonProperty("name") public String name;
	@JsonProperty("description") public String description;
	@JsonProperty("categoryType") public String categoryType;
	@JsonProperty("seq") public int seq;

    public CategoryVM(Category category) {
    	this.id = category.id;
    	this.icon = category.icon;
    	this.name = category.name;
    	this.description = category.description;
    	this.categoryType = category.categoryType.name();
    	this.seq = category.seq;
    }
}
