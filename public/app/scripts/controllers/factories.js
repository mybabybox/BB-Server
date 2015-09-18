'use strict';

var babybox = angular.module('babybox');

babybox.factory('postFactory',function(postManagementService, likeFrameworkService, bookmarkService, usSpinnerService) {

    // some private functions if needed...
    //var myFunction = function() { 
    //    return ""; 
    //};

    var factory = {}; 

    //
    // post
    //
    
    factory.showMore = function(id, posts) {
        postManagementService.postBody.get({id:id},function(data){
            angular.forEach(posts, function(post, key){
                if(post.id == id) {
                    post.pt = data.body;
                    post.showM = false;
                }
            })
        })
    }
    
    factory.getAllComments = function(id, posts) {
        $("#comment-spinner_"+id).show(10);
        angular.forEach(posts, function(post, key){
            if (post.id == id) {
                postManagementService.allComments.get({id:id}, function(data) {
                    post.cs = data;
                    $("#comment-spinner_"+id).hide(10);                    
                });
                post.ep = true;
            }
        });
    }
    
    factory.deletePost = function(postId, posts) {
        postManagementService.deletePost.get({"postId":postId}, function(data) {
            angular.forEach(posts, function(post, key){
                if(post.id == postId) {
                    posts.splice(posts.indexOf(post),1);
                }
            })
        });
    }
    
    factory.deleteComment = function(commentId, post) {
        postManagementService.deleteComment.get({"commentId":commentId}, function(data) {
            var comments = post.cs;
            angular.forEach(comments, function(comment, key){
                if(comment.id == commentId) {
                    comments.splice(comments.indexOf(comment),1);
                }
            })
            post.n_c--;
        });
    }
    
    factory.selectEmoticon = function(code) {
        if($("#content-upload-input").val()){
            $("#content-upload-input").val($("#content-upload-input").val() + " " + code + " ");
        }else{
            $("#content-upload-input").val(code + " ");
        }
        $("#content-upload-input").focus();
        $("#content-upload-input").trigger('input');    // need this to populate jquery val update to ng-model
    }
    
    factory.selectCommentEmoticon = function(code, index) {
        if($("#userCommentfield_"+index).val()){
            $("#userCommentfield_"+index).val($("#userCommentfield_"+index).val() + " " + code + " ");
        }else{
            $("#userCommentfield_"+index).val(code + " ");
        }
        $("#userCommentfield_"+index).focus();
        $("#userCommentfield_"+index).trigger('input');    // need this to populate jquery val update to ng-model
    }
    
    //
    // social
    //
    
    factory.want_answer = function(post_id, posts) {
        likeFrameworkService.hitWantAnswerOnQnA.get({"post_id":post_id}, function(data) {
            angular.forEach(posts, function(post, key){
                if(post.id == post_id) {
                    post.isWtAns=true;
                    post.nowa++;
                }
            })
        });
    }
    
    factory.unwant_answer = function(post_id, posts) {
        likeFrameworkService.hitUnwantAnswerOnQnA.get({"post_id":post_id}, function(data) {
            angular.forEach(posts, function(post, key){
                if(post.id == post_id) {
                    post.isWtAns=false;
                    post.nowa--;
                }
            })
        });
    }
    
    factory.like_post = function(post_id, posts) {
        likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
            angular.forEach(posts, function(post, key){
                if(post.id == post_id) {
                    post.isLike=true;
                    post.nol++;
                }
            })
        });
    }
    
    factory.unlike_post = function(post_id, posts) {
        likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
            angular.forEach(posts, function(post, key){
                if(post.id == post_id) {
                    post.isLike=false;
                    post.nol--;
                }
            })
        });
    }

    factory.like_comment = function(post_id, comment_id, posts) {
        likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
            angular.forEach(posts, function(post, key){
                if(post.id == post_id) {
                    angular.forEach(post.cs, function(comment, key){
                        if(comment.id == comment_id) {
                            comment.nol++;
                            comment.isLike=true;
                        }
                    })
                }
            })
        });
    }
    
    factory.unlike_comment = function(post_id, comment_id, posts) {
        likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
            angular.forEach(posts, function(post, key){
                if(post.id == post_id) {
                    angular.forEach(post.cs, function(comment, key){
                        if(comment.id == comment_id) {
                            comment.nol--;
                            comment.isLike=false;
                        }
                    })
                }
            })
        });
    }
    
    factory.bookmarkPost = function(post_id, posts) {
        bookmarkService.bookmarkPost.get({"post_id":post_id}, function(data) {
            angular.forEach(posts, function(post, key){
                if(post.id == post_id) {
                    post.isBookmarked = true;
                }
            })
        });
    }
    
    factory.unBookmarkPost = function(post_id, posts) {
        bookmarkService.unbookmarkPost.get({"post_id":post_id}, function(data) {
            angular.forEach(posts, function(post, key){
                if(post.id == post_id) {
                    post.isBookmarked = false;
                }
            })
        });
    }
    
    //
    // image
    //
    
    /*
    factory.onFileSelect = function($files, $timeout, selectedFiles, tempSelectedFiles, dataUrls) {
        if(selectedFiles.length == 0) {
            tempSelectedFiles = [];
        }
        
        selectedFiles.push($files);
        tempSelectedFiles.push($files);
        for (var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            dataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }
    */
    
    return factory;
});
