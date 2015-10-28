'use strict';

var babybox = angular.module('babybox');

babybox.directive('adsFactor', function($window, $compile) {
    return {
        restrict: 'A',
        template: '',
        link: 
            function postLink(scope, element, iAttrs) {
                var afid;
                if(af.existcookie('__AF')){
                    afid = af.getcookie('__AF');
                } else {
                    afid = 0;
                }
                resetAds(iAttrs.adsize);     // first clear ads and reset
                registeredAds[iAttrs.adsize].push(element);
                $(createAdsFactorScript(afid,iAttrs.adsid,iAttrs.adsize)).insertAfter($(element));
            }
    };
    function resetAds(adsize) {
        $("div[id^=af_]").remove();         // remove all floating ads
        registeredAds[adsize] = [];  // LIMITIONS: only 1 ad per ad size per page
        if (typeof window.defaultRegisteredAd !== 'undefined') {
            window.defaultRegisteredAd = undefined;
        }
    }
    function createAdsFactorScript(afid,sid,adsize) {
        var randomstr = new String (Math.random());
        randomstr = randomstr.substring(2,8);
        var script = ("<" + "script language='JavaScript' type='text/javascript' src='");
        script = script + ("http://servedby.adsfactor.net/adj.php?ts=" + randomstr + "&amp;sid="+sid+"&amp;afid=" + afid);
        if(document.af_used) {
            script = script + ("&amp;ex=" + document.af_used);
        }
        if(window.location.href) {
            script = script + ("&amp;location=" + encodeURIComponent(escape(window.location.href)));
        }
        if(document.referrer) {
            script = script + ("&amp;referer=" + encodeURIComponent(escape(document.referrer)));
        }
        script = script + ("'><" + "/script>");
        // tricky... google ads returned and we need to write out swfobject.js if so...
        if(adsize == 11) {
            script = script + ("<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/swfobject/2.2/swfobject.js'></script>");
        }
        script = script + ("<noscript><a href='http://servedby.adsfactor.net/adc.php?sid="+sid+"' ><img src='http://servedby.adsfactor.net/adv.php?sid="+sid+"' border='0'></a></noscript>");
        return script;
    }
});

/**
 * e.g.
 * <input ng:model="filter.firstName"/>
 * <input ng:model="filter.lastName"/>
 * <input ng:model="filter.email"/>
 * 
 * <ul ng:repeat="cust in customers | objFilter: filter">
 *     <li>
 *         <a href="mailto:{{cust.email}}">{{cust.firstName}} {{cust.lastName}}</a>
 *     </li>
 * </ul>
 */
babybox.filter('objFilter', function() {
    return function(items, filter, exact) {
        if (!filter){
            return items;
        }
        var result = [];
        angular.forEach(filter, function(filterVal, filterKey) {
            angular.forEach(items, function(item, key) {
                var fieldVal = item[filterKey] + "";    // convert boolean to string in case
                //console.log("item value:"+fieldVal.toLowerCase()+"   filter value:"+filterVal.toLowerCase());
                if (exact && fieldVal && fieldVal.toLowerCase() == filterVal.toLowerCase()) {
                    result.push(item);                
                } else if (!exact && fieldVal && fieldVal.toLowerCase().indexOf(filterVal.toLowerCase()) > -1){
                    result.push(item);
                }
            });
        });
        return result;
    };
});

/**
 * trustAsHtml
 */
babybox.filter('to_trusted', ['$sce', function($sce){
    return function(text) {
        return $sce.trustAsHtml(text);
    };
}]);

/**
 * A generic confirmation for risky actions.
 * Usage: Add attributes: ng-really-message="Are you sure"? ng-really-click="takeAction()" function
 */
babybox.directive('ngConfirmClick', [function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.bind('click', function() {
                var className = attrs.ngConfirmClassName;
                if (!className) {
                    className = "post-bootbox-modal";
                }
                var message = attrs.ngConfirmMessage;
                if (message) {
                    /*
                    bootbox.confirm(message, function(result) {
                        if (result) {
                            scope.$apply(attrs.ngConfirmClick);
                        }
                    });
                    */
                    
                    bootbox.dialog({
                      message: message,
                      title: "",
                      className: className,
                      buttons: {
                        cancel: {
                          label: "取消",
                          className: "btn-default",
                          callback: function() {
                            
                          }
                        },
                        success: {
                          label: "確認",
                          className: "btn-primary",
                          callback: function() {
                            scope.$apply(attrs.ngConfirmClick);
                          }
                        }
                      }
                    });
                    
                } else {
                    scope.$apply(attrs.ngConfirmClick);
                }
            });
        }
    }
}]);

/**
 * Use this directive 'valid-file' together with 'required' for input type='file'. 
 */
babybox.directive('validFile',function(){
    return {
        require:'ngModel',
        link:function(scope,el,attrs,ngModel){
            //change event is fired when file is selected
            el.bind('change',function(){
                scope.$apply(function(){
                    ngModel.$setViewValue(el.val());
                    ngModel.$render();
                });
            });
        }
    }
});