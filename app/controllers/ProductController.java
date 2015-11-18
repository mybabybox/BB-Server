package controllers;

import static play.data.Form.form;
import handler.FeedHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Category;
import models.Collection;
import models.Comment;
import models.Conversation;
import models.Post;
import models.Post.ConditionType;
import models.Resource;
import models.User;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import service.SocialRelationHandler;
import viewmodel.CommentVM;
import viewmodel.ConversationVM;
import viewmodel.PostVM;
import viewmodel.PostVMLite;
import viewmodel.ResponseStatusVM;
import viewmodel.UserVM;
import babybox.shopping.social.exception.SocialObjectNotCommentableException;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import common.cache.CalcServer;
import common.cache.JedisCache;
import common.model.FeedFilter.FeedType;
import common.utils.HtmlUtil;
import common.utils.ImageFileUtil;
import common.utils.NanoSecondStopWatch;

import controllers.Application.DeviceType;
import domain.DefaultValues;
import domain.SocialObjectType;

public class ProductController extends Controller{
	
	@Inject
	JedisCache jedisCache;
	
	@Inject
	FeedHandler feedHandler;
	
	private static play.api.Logger logger = play.api.Logger.apply(ProductController.class);
	
	@Transactional
	public static Result createProductWeb() {
		DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
		List<FilePart> images = request().body().asMultipartFormData().getFiles();
		String catId = dynamicForm.get("catId");
	    String title = dynamicForm.get("title");
	    String body = dynamicForm.get("body");
	    String price = dynamicForm.get("price");
	    String conditionType = dynamicForm.get("conditionType");
	    String deviceType = dynamicForm.get("deviceType");
		return createProduct(title, body, Long.parseLong(catId), Double.parseDouble(price), Post.parseConditionType(conditionType), images, Application.parseDeviceType(deviceType));
	}
	
	@Transactional
	public static Result createProduct() {
		Http.MultipartFormData multipartFormData = request().body().asMultipartFormData();
		String catIdStr = multipartFormData.asFormUrlEncoded().get("catId")[0];
	    String title = multipartFormData.asFormUrlEncoded().get("title")[0];
	    String body = multipartFormData.asFormUrlEncoded().get("body")[0];
	    String priceStr = multipartFormData.asFormUrlEncoded().get("price")[0];
	    String conditionType = multipartFormData.asFormUrlEncoded().get("conditionType")[0];
	    String deviceType = multipartFormData.asFormUrlEncoded().get("deviceType")[0];
	    List<FilePart> images = Application.parseAttachments("image", DefaultValues.MAX_POST_IMAGES);
	    
	    Long catId = -1L;
	    try {
	    	catId = Long.parseLong(catIdStr);
	    } catch (NumberFormatException e) {
	    }
	    
	    Double price = -1D;
	    try {
	    	price = Double.parseDouble(priceStr);
	    } catch (NumberFormatException e) {
	    }
		return createProduct(title, body, catId, price, Post.parseConditionType(conditionType), images, Application.parseDeviceType(deviceType));
	}

	private static Result createProduct(String title, String body, Long catId, Double price, ConditionType conditionType, List<FilePart> images, DeviceType deviceType) {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
	    
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		
		Category category = Category.findById(catId);
        if (category == null) {
            return notFound();
        }
        
		try {
			Post newPost = localUser.createProduct(title, body, category, price, conditionType, deviceType);
			if (newPost == null) {
				return badRequest("Failed to create product. Invalid parameters.");
			}
			
			for (FilePart image : images) {
				String fileName = image.getFilename();
				File file = image.getFile();
				File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
				newPost.addPostPhoto(fileTo);
			}
			
			SocialRelationHandler.recordNewPost(newPost, localUser);
			ResponseStatusVM response = new ResponseStatusVM(SocialObjectType.POST, newPost.id, localUser.id, true);
			
			sw.stop();
	        if (logger.underlyingLogger().isDebugEnabled()) {
	            logger.underlyingLogger().debug("[u="+localUser.getId()+"][p="+newPost.id+"] createProduct(). Took "+sw.getElapsedMS()+"ms");
	        }
	        
			return ok(Json.toJson(response));
		} catch (IOException e) {
			logger.underlyingLogger().error("Error in createProduct", e);
		}
		
		return badRequest();
	}
	
	@Transactional
    public static Result editProductWeb() {
        DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
        String postId = dynamicForm.get("postId");
        String catId = dynamicForm.get("catId");
        String title = dynamicForm.get("title");
        String body = dynamicForm.get("body");
        String price = dynamicForm.get("price");
        String conditionType = dynamicForm.get("conditionType");
        return editProduct(Long.parseLong(postId), title, body, Long.parseLong(catId), 
                Double.parseDouble(price), Post.parseConditionType(conditionType));
    }
    
    @Transactional
    public static Result editProduct() {
        Http.MultipartFormData multipartFormData = request().body().asMultipartFormData();
        String postIdStr = multipartFormData.asFormUrlEncoded().get("postId")[0];
        String catIdStr = multipartFormData.asFormUrlEncoded().get("catId")[0];
        String title = multipartFormData.asFormUrlEncoded().get("title")[0];
        String body = multipartFormData.asFormUrlEncoded().get("body")[0];
        String priceStr = multipartFormData.asFormUrlEncoded().get("price")[0];
        String conditionType = multipartFormData.asFormUrlEncoded().get("conditionType")[0];
        
        Long postId = -1L;
        try {
            postId = Long.parseLong(postIdStr);
        } catch (NumberFormatException e) {
        }
        
        Long catId = -1L;
        try {
            catId = Long.parseLong(catIdStr);
        } catch (NumberFormatException e) {
        }
        
        Double price = -1D;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
        }
        
        return editProduct(postId, title, body, catId, price, Post.parseConditionType(conditionType));
    }

    private static Result editProduct(Long postId, String title, String body, Long catId, 
            Double price, Post.ConditionType conditionType) {
        
        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return notFound();
        }
        
        Post post = Post.findById(postId);
        if (post == null) {
            return notFound();
        }
        
        Category oldCategory = post.category;
        Category category = Category.findById(catId);
        if (category == null) {
            return notFound();
        }
        
        Post editPost = localUser.editProduct(post, title, body, category, price, conditionType);
        if (editPost == null) {
            return badRequest("Failed to edit product. Invalid parameters.");
        }
        
        // category changed, handle event
        if (catId != oldCategory.id) {
            SocialRelationHandler.recordEditPost(editPost, oldCategory);
        }
        ResponseStatusVM response = new ResponseStatusVM(SocialObjectType.POST, editPost.id, localUser.id, true);
        
        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.getId()+"][p="+postId+"] editProduct(). Took "+sw.getElapsedMS()+"ms");
        }
        
        return ok(Json.toJson(response));
    }

	@Transactional
	public static Result createCollection() {
		final User localUser = Application.getLocalUser(session());
		DynamicForm form = DynamicForm.form().bindFromRequest();	
		Collection newCollection = localUser.createCollection(form.get("name"), form.get("description"));
		if (newCollection == null) {
			return badRequest("Failed to create Collection. Invalid parameters.");
		}
		return ok(Json.toJson(newCollection.id));
	}
	
	@Transactional
	public static Result addToCollection() {
		final User localUser = Application.getLocalUser(session());
		DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
		Long productId = Long.parseLong(dynamicForm.get("product_id"));
		Long collectionId = Long.parseLong(dynamicForm.get("collectionId"));
		Collection collection = null;
		if(collectionId == 0){
			String collectionName = dynamicForm.get("collectionName");
			collection = localUser.createCollection(collectionName);
		} else {
			collection = Collection.findById(collectionId);
		}
		collection.products.add(Post.findById(productId));
		return ok();
	}

	@Transactional
	public Result getAllFeedProducts() {
		return ok(Json.toJson(getPostVMsFromPosts(Post.getEligiblePostsForFeeds())));
	}

	private static List<PostVMLite> getPostVMsFromPosts(List<Post> posts) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return null;
		}
		
		List<PostVMLite> vms = new ArrayList<>();
		for (Post product : posts) {
			PostVMLite vm = new PostVMLite(product, localUser);
			vms.add(vm);
		}
		return vms;
	}
	
	@Transactional
	public static Result getAllSimilarProducts() {
		return ok();
	}

	@Transactional
	public static Result getProductImageById(Long id) {
		response().setHeader("Cache-Control", "max-age=604800");
		Resource resource = Resource.findById(id);
		if (resource == null || resource.getThumbnailFile() == null) {
			return ok();
		}
		return ok(resource.getThumbnailFile());
	}

	@Transactional
	public static Result getOriginalProductImageById(Long id) {
		response().setHeader("Cache-Control", "max-age=604800");
		Resource resource = Resource.findById(id);
		if (resource == null || resource.getRealFile() == null) {
			return ok();
		}
		return ok(resource.getRealFile());
	}
	
	@Transactional
	public static Result getMiniProductImageById(Long id) {
		response().setHeader("Cache-Control", "max-age=604800");
		Resource resource = Resource.findById(id);
		if (resource == null || resource.getMini() == null) {
			return ok();
		}
		return ok(new File(resource.getMini()));
	}

	@Transactional
	public Result product(Long id) {
		final User localUser = Application.getLocalUser(session());
		return ok(views.html.babybox.web.product.render(Json.stringify(Json.toJson(getProductInfoVM(id))), Json.stringify(Json.toJson(new UserVM(localUser)))));
	}
	
	@Transactional
	public Result getProductInfo(Long id) {
		PostVM post = getProductInfoVM(id);
		if (post == null) {
			return notFound();
		}
		return ok(Json.toJson(post));
	}
	
	public PostVM getProductInfoVM(Long id) {
		User localUser = Application.getLocalUser(session());
		Post post = Post.findById(id);
		if (post == null) {
			return null;
		}
		onView(id);
		PostVM vm = new PostVM(post, localUser);
		return vm;
	}

	@Transactional
	public static Result likePost(Long id) {
		User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return notFound();
        }
		
		Post post = Post.findById(id);
		SocialRelationHandler.recordLikePost(post, localUser);
		return ok();
	}

	@Transactional
	public static Result unlikePost(Long id) {
		User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return notFound();
        }
		
		Post post = Post.findById(id); 
		SocialRelationHandler.recordUnLikePost(post, localUser);
		return ok();
	}

	@Transactional
	public static Result soldPost(Long id) {
		User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return notFound();
        }
		
		Post post = Post.findById(id);
		if (post == null) {
		    return notFound();
		}
		
		if (post.owner.id == localUser.id || localUser.isSuperAdmin()) {
			SocialRelationHandler.recordSoldPost(post, localUser);
		}
		return ok();
	}

	@Transactional
	public static Result newComment() {
		final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return notFound();
        }
        
		DynamicForm form = form().bindFromRequest();
		Long postId = Long.parseLong(form.get("postId"));
		String body = HtmlUtil.convertTextToHtml(form.get("body"));
		
		Post post = Post.findById(postId);
		try {
			Comment comment = (Comment) post.onComment(localUser, body);
			
			// set device
			DeviceType deviceType = Application.parseDeviceType(form.get("deviceType"));
			comment.deviceType = deviceType;
			
			SocialRelationHandler.recordNewComment(comment, post);
			ResponseStatusVM response = new ResponseStatusVM(SocialObjectType.COMMENT, comment.id, comment.owner.id, true);
			return ok(Json.toJson(response));
		} catch (SocialObjectNotCommentableException e) {
		}
		return badRequest();
	}
	
	@Transactional
    public static Result deletePost(Long id) {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return notFound();
        }
        
        Post post = Post.findById(id);

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug(String.format("[u=%d][c=%d][p=%d] deletePost", localUser.id, post.category.id, id));
        }

        if (localUser.equals(post.owner) || 
                localUser.isSuperAdmin()) {
        	localUser.deleteProduct(post);
        	SocialRelationHandler.recordDeletePost(post, localUser);
            return ok();
        }
        return badRequest("Failed to delete post. [u=" + localUser.id + "] not owner of post [id=" + id + "].");
    }
    
    @Transactional
    public static Result deleteComment(Long id) {
        final User localUser = Application.getLocalUser(session());
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug(String.format("[u=%d][cmt=%d] deleteComment", localUser.id, id));
        }

        try {
	        Comment comment = Comment.findById(id);
	        if (localUser.equals(comment.owner) ||
	                localUser.isSuperAdmin()) {
	        	Post post = Post.findById(comment.socialObject);
	            post.onDeleteComment(localUser, comment);
	            SocialRelationHandler.recordDeleteComment(comment, post);
	            return ok();
	        }
        } catch (SocialObjectNotCommentableException e) {
		}
        return badRequest("Failed to delete comment. [u="+localUser.id+"] not owner of comment [id=" + id + "].");
    }

	@Transactional
	public static Result onView(Long id) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		
		Post post = Post.findById(id);
		SocialRelationHandler.recordViewPost(post, localUser);
		return ok();
	}
	
	@Transactional 
	public Result getCategoryPopularFeed(Long id, String postType, Long offset){
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		
		List<PostVMLite> vms = feedHandler.getPostVM(id, offset, localUser, FeedType.CATEGORY_POPULAR);
		return ok(Json.toJson(vms));

	}

	@Transactional 
	public Result getCategoryNewestFeed(Long id, String postType, Long offset){
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		List<PostVMLite> vms = feedHandler.getPostVM(id, offset, localUser, FeedType.CATEGORY_NEWEST);
		return ok(Json.toJson(vms));
	}
	
	@Transactional()
	public Result getCategoryPriceLowHighFeed(Long id, String postType, Long offset){
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		List<PostVMLite> vms = feedHandler.getPostVM(id, offset, localUser, FeedType.CATEGORY_PRICE_LOW_HIGH);
		return ok(Json.toJson(vms));
	}
	
	@Transactional 
	public Result getCategoryPriceHighLowFeed(Long id, String postType, Long offset) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		List<PostVMLite> vms = feedHandler.getPostVM(id, offset, localUser, FeedType.CATEGORY_PRICE_HIGH_LOW);
		return ok(Json.toJson(vms));
	}
	
	@Transactional 
	public Result getSuggestedProducts(Long id) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		List<PostVMLite> vms = feedHandler.getPostVM(id, 0l, localUser, FeedType.PRODUCT_SUGGEST);
		return ok(Json.toJson(vms));
	}
	
	@Transactional
	public static Result getPostComments(Long id, Long offset) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		
		Post post = Post.findById(id);
		if (post == null) {
			return ok();
		}
		
		List<CommentVM> comments = new ArrayList<CommentVM>();
		for (Comment comment : post.getPostComments(offset)) {
			CommentVM commentVM = new CommentVM(comment, localUser);
			comments.add(commentVM);
		}
		return ok(Json.toJson(comments));
	}

	@Transactional
	public static Result getConversations(Long id) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		
		// Only owner can view all chats for a product
		Post post = Post.findById(id);
		if (post == null) {
			return notFound();
		}
		
		if (post.owner.id == localUser.id || localUser.isSuperAdmin()) {
			List<ConversationVM> vms = new ArrayList<>();
			List<Conversation> conversations = post.findConversations();
			if (conversations != null) {
				for (Conversation conversation : conversations) {
					// archived, dont show
					if (conversation.isArchivedBy(localUser)) {
						continue;
					}

					ConversationVM vm = new ConversationVM(conversation, localUser);
					vms.add(vm);
				}
			}
			return ok(Json.toJson(vms));
		}
		return badRequest();
	}
}