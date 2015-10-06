package controllers;

import static play.data.Form.form;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Category;
import models.Collection;
import models.Comment;
import models.Post;
import models.Resource;
import models.User;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import viewmodel.CommentVM;
import viewmodel.PostVM;
import viewmodel.PostVMLite;
import viewmodel.ResponseStatusVM;
import viewmodel.UserVM;
import common.cache.CalcServer;
import common.utils.HtmlUtil;
import common.utils.ImageFileUtil;
import controllers.Application.DeviceType;
import domain.SocialObjectType;

public class ProductController extends Controller{
	private static play.api.Logger logger = play.api.Logger.apply(ProductController.class);
	
	@Transactional
	public static Result createProductWeb() {
		DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
		List<FilePart> images = request().body().asMultipartFormData().getFiles();
		String catId = dynamicForm.get("catId");
	    String title = dynamicForm.get("title");
	    String body = dynamicForm.get("body");
	    String price = dynamicForm.get("price");
	    String deviceType = dynamicForm.get("deviceType");
		return createProduct(title, body, Long.parseLong(catId), Double.parseDouble(price), images, Application.parseDeviceType(deviceType));
	}
	
	@Transactional
	public static Result createProductMobile() {
		Http.MultipartFormData multipartFormData = request().body().asMultipartFormData();
		List<FilePart> files = new ArrayList<>();
		for(int i = 0; i<5; i++){
			if(multipartFormData.getFile("post-image"+i) != null){
				files.add(multipartFormData.getFile("post-image"+i));
			} else {
				break;
			}
		}
	    
		String catId = multipartFormData.asFormUrlEncoded().get("catId")[0];
	    String title = multipartFormData.asFormUrlEncoded().get("title")[0];
	    String body = multipartFormData.asFormUrlEncoded().get("body")[0];
	    String price = multipartFormData.asFormUrlEncoded().get("price")[0];
	    String deviceType = multipartFormData.asFormUrlEncoded().get("deviceType")[0];
	    request().body().asMultipartFormData().getFiles();
		return createProduct(title, body, Long.parseLong(catId), Double.parseDouble(price), files, Application.parseDeviceType(deviceType));
	}
	
	private static Result createProduct(String title, String body, Long catId, Double price, List<FilePart> images, DeviceType deviceType) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		
		try {
			Post newPost = localUser.createProduct(title, body, Category.findById(catId), price);
			if (newPost == null) {
				return badRequest("Failed to create product. Invalid parameters.");
			}
			
			for(FilePart image : images){
				String fileName = image.getFilename();
				File file = image.getFile();
				File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
				newPost.addPostPhoto(fileTo);
			}
			
			// set device
			newPost.deviceType = deviceType;
			
			CalcServer.addToQueues(newPost);
			ResponseStatusVM response = new ResponseStatusVM(SocialObjectType.POST, newPost.id, localUser.id, true);
			return ok(Json.toJson(response));
		} catch (IOException e) {
			logger.underlyingLogger().error("Error in createProduct", e);
		}
		
		return badRequest();
	}

	@Transactional
	public static Result createCollection() {
		final User localUser = Application.getLocalUser(session());
		DynamicForm form1 = DynamicForm.form().bindFromRequest();	
		Category category = Category.findById(Long.parseLong(form1.get("category")));
		Collection newCollection = localUser.createCollection(form1.get("name"), form1.get("description"), category);
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
	public static Result getAllFeedProducts() {
		return ok(Json.toJson(getPostVMsFromPosts(Post.getAllPosts())));
	}

	private static List<PostVMLite> getPostVMsFromPosts(List<Post> posts) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return null;
		}
		
		List<PostVMLite> vms = new ArrayList<>();
		for (Post product : posts) {
			PostVMLite vm = new PostVMLite(product);
			vm.isLiked = product.isLikedBy(localUser);
			vms.add(vm);
		}
		return vms;
	}
	
	@Transactional
	public static Result getAllSimilarProducts() {
		return ok(Json.toJson(getPostVMsFromPosts(Post.getAllPosts())));
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
	public static Result product(Long id) {
		final User localUser = Application.getLocalUser(session());
		return ok(views.html.babybox.web.product.render(Json.stringify(Json.toJson(getProductInfoVM(id))), Json.stringify(Json.toJson(new UserVM(localUser)))));
	}
	
	@Transactional
	public static Result getProductInfo(Long id) {
		PostVM post = getProductInfoVM(id);
		if (post == null) {
			return notFound();
		}
		return ok(Json.toJson(post));
	}
	
	public static PostVM getProductInfoVM(Long id) {
		User localUser = Application.getLocalUser(session());
		Post post = Post.findById(id);
		if (post == null) {
			return null;
		}
		PostVM vm = new PostVM(post, localUser);
		vm.isFollowingOwner = post.owner.isFollowedBy(localUser);
		return vm;
	}

	@Transactional
	public static Result likePost(Long id) {
		Post.findById(id).onLikedBy(Application.getLocalUser(session()));
		return ok();
	}

	@Transactional
	public static Result unlikePost(Long id) {
		Post.findById(id).onUnlikedBy(Application.getLocalUser(session()));
		return ok();
	}

	@Transactional
	public static Result newComment() {
		DynamicForm form = form().bindFromRequest();
		Long postId = Long.parseLong(form.get("postId"));
		String body = HtmlUtil.convertTextToHtml(form.get("body"));
		Comment comment = (Comment) Post.findById(postId).onComment(Application.getLocalUser(session()), body);

		// set device
		DeviceType deviceType = Application.parseDeviceType(form.get("deviceType"));
		comment.deviceType = deviceType;
		
		ResponseStatusVM response = new ResponseStatusVM(SocialObjectType.COMMENT, comment.id, comment.owner.id, true);
		return ok(Json.toJson(response));
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
            post.delete(localUser);
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

        Comment comment = Comment.findById(id);
        if (localUser.equals(comment.owner) ||
                localUser.isSuperAdmin()) {
            comment.delete(localUser);
            return ok();
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
		
		Post.findById(id).onView(localUser);
		return ok();
	}
	
	@Transactional 
	public static Result getCategoryPopularFeed(Long id, String postType, Long offset){
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		
		List<Long> postIds = CalcServer.getCategoryPopularFeed(id, offset.doubleValue());
		if(postIds.size() == 0){
			return ok(Json.toJson(postIds));
		}
		
		List<PostVMLite> vms = new ArrayList<>();
		List<Post> posts =  Post.getPosts(postIds);
		for(Post post : posts) {
			PostVMLite vm = new PostVMLite(post);
			vm.isLiked = post.isLikedBy(localUser);
			vm.offset = post.baseScore;
			vms.add(vm);
		}
		return ok(Json.toJson(vms));

	}
	
	@Transactional 
	public static Result getCategoryNewestFeed(Long id, String postType, Long offset){
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		
		List<Long> postIds = CalcServer.getCategoryNewestFeed(id, offset.doubleValue());	
		if(postIds.size() == 0){
			return ok(Json.toJson(postIds));
		}
		
		List<PostVMLite> vms = new ArrayList<>();
		List<Post> posts =  Post.getPosts(postIds);
		for(Post post : posts) {
			PostVMLite vm = new PostVMLite(post);
			vm.isLiked = post.isLikedBy(localUser);
			vm.offset = post.getCreatedDate().getTime();
			vms.add(vm);
		}
		return ok(Json.toJson(vms));
	}
	
	@Transactional 
	public static Result getCategoryPriceLowHighFeed(Long id, String postType, Long offset){
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		
		List<Long> postIds = CalcServer.getCategoryPriceLowHighFeed(id, offset.doubleValue());
        if(postIds.size() == 0){
			return ok(Json.toJson(postIds));
		}
        
        List<PostVMLite> vms = new ArrayList<>();
		for(Post product : Post.getPosts(postIds)) {
			PostVMLite vm = new PostVMLite(product);
			vm.isLiked = product.isLikedBy(localUser);
			vm.offset = product.price.longValue();
			vms.add(vm);
		}
		return ok(Json.toJson(vms));
	}
	
	@Transactional 
	public static Result getCategoryPriceHighLowFeed(Long id, String postType, Long offset) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
			logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
			return notFound();
		}
		
		List<Long> postIds = CalcServer.getCategoryPriceHighLowFeed(id, offset.doubleValue());
		if(postIds.size() == 0){
			return ok(Json.toJson(postIds));
		}
		
		List<PostVMLite> vms = new ArrayList<>();
		for(Post product : Post.getPosts(postIds)) {
			PostVMLite vm = new PostVMLite(product);
			vm.isLiked = product.isLikedBy(localUser);
			vm.offset = product.price.longValue();
			vms.add(vm);
		}
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
		
		List <CommentVM> comments = new ArrayList<CommentVM>();
		for(Comment comment : post.getComments()){
			CommentVM commentVM = new CommentVM(comment, localUser);
			comments.add(commentVM);
		}
		return ok(Json.toJson(comments));
	}

}