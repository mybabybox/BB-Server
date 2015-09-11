'use strict';

var minibean = angular.module('minibean');

minibean.factory('schoolsFactory',function(schoolsService, postManagementService, likeFrameworkService, bookmarkService, usSpinnerService) {

    var factory = {}; 

    factory.bookmarkPN = function(pn_id, pns, bookmarkedPNs) {
        bookmarkService.bookmarkPN.get({"pn_id":pn_id}, function(data) {
            angular.forEach(pns, function(pn, key){
                if(pn.id == pn_id) {
                    pn.isBookmarked = true;
                    
                    var bookmarked = false;
                    angular.forEach(bookmarkedPNs, function(bookmarkedPN, key){
                    	if (bookmarkedPN.id == pn_id) {
                    		bookmarked = true;
                    	}
                    })
                    if (!bookmarked) {
                    	bookmarkedPNs.push(pn);
                    }
                }
            })
        });
    }
    
    factory.unBookmarkPN = function(pn_id, pns, bookmarkedPNs) {
        bookmarkService.unbookmarkPN.get({"pn_id":pn_id}, function(data) {
            angular.forEach(pns, function(pn, key){
                if(pn.id == pn_id) {
                    pn.isBookmarked = false;
                    
                    angular.forEach(bookmarkedPNs, function(bookmarkedPN, key){
                    	if (bookmarkedPN.id == pn_id) {
                    		//alert(pn_id+":"+bookmarkedPNs.indexOf(bookmarkedPN));
                    		bookmarkedPNs.splice(bookmarkedPNs.indexOf(bookmarkedPN),1);
                    	}
                    })
                }
            })
        });
    }
    
    factory.bookmarkKG = function(kg_id, kgs, bookmarkedKGs) {
        bookmarkService.bookmarkKG.get({"kg_id":kg_id}, function(data) {
            angular.forEach(kgs, function(kg, key){
                if(kg.id == kg_id) {
                    kg.isBookmarked = true;
                    
                    var bookmarked = false;
                    angular.forEach(bookmarkedKGs, function(bookmarkedKG, key){
                    	if (bookmarkedKG.id == kg_id) {
                    		bookmarked = true;
                    	}
                    })
                    if (!bookmarked) {
                    	bookmarkedKGs.push(kg);
                    }
                }
            })
        });
    }
    
    factory.unBookmarkKG = function(kg_id, kgs, bookmarkedKGs) {
        bookmarkService.unbookmarkKG.get({"kg_id":kg_id}, function(data) {
            angular.forEach(kgs, function(kg, key){
                if(kg.id == kg_id) {
                	kg.isBookmarked = false;
                    
                    angular.forEach(bookmarkedKGs, function(bookmarkedKG, key){
                    	if (bookmarkedKG.id == kg_id) {
                    		//alert(kg_id+":"+bookmarkedKGs.indexOf(bookmarkedKG));
                    		bookmarkedKGs.splice(bookmarkedKGs.indexOf(bookmarkedKG),1);
                    	}
                    })
                }
            })
        });
    }
    
    return factory;
});
    
minibean.factory('pkViewFactory',function(pkViewService, postManagementService, likeFrameworkService, bookmarkService, usSpinnerService) {

    var factory = {}; 

    factory.deleteComment = function(commentId, attr, pkview) {
        postManagementService.deleteComment.get({"commentId":commentId}, function(data) {
            if (attr == 'YES') {
                angular.forEach(pkview.red_cs, function(comment, key){
                    if(comment.id == commentId) {
                        pkview.red_cs.splice(pkview.red_cs.indexOf(comment),1);
                    }
                })
                pkview.n_rc--;            
            } else if (attr == 'NO') {
                angular.forEach(pkview.blue_cs, function(comment, key){
                    if(comment.id == commentId) {
                        pkview.blue_cs.splice(pkview.blue_cs.indexOf(comment),1);
                    }
                })
                pkview.n_bc--;
            }
            pkview.n_c--;
        });
    }
    
    factory.selectCommentEmoticon = function(code, attr) {
        var elem;
        if (attr == 'YES') {
            elem = $("#redCommentfield");
        } else if (attr == 'NO') {
            elem = $("#blueCommentfield");
        }
        
        if(elem.val()){
            elem.val(elem.val() + " " + code + " ");
        }else{
            elem.val(code + " ");
        }
        elem.focus();
        elem.trigger('input');    // need this to populate jquery val update to ng-model
    }
    
    factory.like_pkview = function(pkview_id, pkview) {
        likeFrameworkService.hitLikeOnPKView.get({"pkview_id":pkview_id}, 
            function(data) {
                pkview.nol++;
                pkview.isLike=true;
            });
    }

    factory.unlike_pkview = function(pkview_id, pkview) {
        likeFrameworkService.hitUnlikeOnPKView.get({"pkview_id":pkview_id}, 
            function(data) {
                pkview.nol--;
                pkview.isLike=false;
            });
    }
    
    factory.bookmarkPKView = function(pkview_id, pkview) {
        bookmarkService.bookmarkPKView.get({"pkview_id":pkview_id}, function(data) {
            pkview.isBookmarked = true;
        });
    }
    
    factory.unBookmarkPKView = function(pkview_id, pkview) {
        bookmarkService.unbookmarkPKView.get({"pkview_id":pkview_id}, function(data) {
            pkview.isBookmarked = false;
        });
    }
    
    factory.like_comment = function(comment_id, attr, pkview) {
        likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
            if (attr == 'YES') {
                angular.forEach(pkview.red_cs, function(comment, key){
                    if(comment.id == comment_id) {
                        comment.nol++;
                        comment.isLike = true;
                    }
                });
            } else if (attr == 'NO') {
                angular.forEach(pkview.blue_cs, function(comment, key){
                    if(comment.id == comment_id) {
                        comment.nol++;
                        comment.isLike = true;
                    }
                });
            }
        });
    }
    
    factory.unlike_comment = function(comment_id, attr, pkview) {
        likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
            if (attr == 'YES') {
                angular.forEach(pkview.red_cs, function(comment, key){
                    if(comment.id == comment_id) {
                        comment.nol--;
                        comment.isLike = false;
                    }
                });
            } else if (attr == 'NO') {
                angular.forEach(pkview.blue_cs, function(comment, key){
                    if(comment.id == comment_id) {
                        comment.nol--;
                        comment.isLike = false;
                    }
                });
            }
        });
    }
    
    var alreadyVote = function(pkview) {
        if (pkview.isRed) {
            prompt("<div><b>你已支持紅豆豆</b></div>", "bootbox-default-prompt", 2500);
            return true;
        }
        if (pkview.isBlue) {
            prompt("<div><b>你已支持藍豆豆</b></div>", "bootbox-default-prompt", 2500);
            return true;
        }
        return false;
    }
    
    factory.redVote = function(pkview) {
        if (alreadyVote(pkview)) {
            return;
        }
        var pkviewToVote = pkview;
        pkViewService.yesVotePKView.get({id:pkview.id},
            function(data) {
                pkviewToVote.n_rv++;
                pkviewToVote.isRed = true;
            }
        );
    }
    
    factory.blueVote = function(pkview) {
        if (alreadyVote(pkview)) {
            return;
        }
        var pkviewToVote = pkview;
        pkViewService.noVotePKView.get({id:pkview.id},
            function(data) {
                pkviewToVote.n_bv++;
                pkviewToVote.isBlue = true;
            }
        );
    }
    
    return factory;
});

minibean.factory('articleFactory',function(likeFrameworkService, bookmarkService, usSpinnerService) {

    var factory = {}; 

    factory.like_article = function(article_id, article) {
        likeFrameworkService.hitLikeOnArticle.get({"article_id":article_id}, 
            function(data) {
                article.nol++;
                article.isLike=true;
            });
    }

    factory.unlike_article = function(article_id, article) {
        likeFrameworkService.hitUnlikeOnArticle.get({"article_id":article_id}, 
            function(data) {
                article.nol--;
                article.isLike=false;
            });
    }
    
    factory.bookmarkArticle = function(article_id, articles) {
        bookmarkService.bookmarkArticle.get({"article_id":article_id}, function(data) {
            angular.forEach(articles, function(article, key){
                if(article.id == article_id) {
                    article.isBookmarked = true;
                }
            })
        });
    }
    
    factory.unBookmarkArticle = function(article_id, articles) {
        bookmarkService.unbookmarkArticle.get({"article_id":article_id}, function(data) {
            angular.forEach(articles, function(article, key){
                if(article.id == article_id) {
                    article.isBookmarked = false;
                }
            })
        });
    }
    
    return factory;
});

minibean.factory('postFactory',function(postManagementService, likeFrameworkService, bookmarkService, usSpinnerService) {

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
