package common.cache;

import models.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class CategoryCache {
    // Permanent cache loaded up on system startup.

    private static List<Category> categories;
    private static final Map<Long, Category> categoriesMap = new HashMap<>();

    static {
        categories = Category.loadCategories();
        for (Category category : categories) {
        	categoriesMap.put(category.id, category);
        }
    }

    public static List<Category> getAllCategories() {
        return categories;
    }
    
    public static Category getCategory(Long id) {
    	return categoriesMap.get(id);
    }
}
