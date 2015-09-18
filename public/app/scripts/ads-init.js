if (!window.af) {
    var af = (function() {
        return {
            getcookie:function(n){
                var m=n+'=';
                var ca=document.cookie.split(';');
                for(var i=0;i < ca.length;i++) {
                    var c=ca[i];
                    while (c.charAt(0)==' ')
                        c = c.substring(1,c.length);
                    if (c.indexOf(m) == 0)
                        return c.substring(m.length,c.length);
                }
                return null;
            },
            existcookie:function(n){
                var m=af.getcookie(n);
                if (m!=null&&m!=''){
                    return true;
                }else{
                    return false;
                }
            }
        };
    })();
}

window.defaultRegisteredAd;
window.registeredAds = {};
window.registeredAds[300250] = []; // Here you need to define AdsFactor size
window.registeredAds[72890] = [];
window.registeredAds[11] = [];
document.write = function(node) {
    //console.log('AD RESPONSE: '+node);
    if(typeof node !== 'undefined') {
        var elem;
        if (is300x250(node)) {
            elem = registeredAds[300250][0]         // .pop();
            //console.log('AD: poped 300x250 - '+elem);
        } else if (is728x90(node)) {
            elem = registeredAds[72890][0]          // .pop();
            //console.log('AD: poped 728x90 - '+elem);
        } else if (is1x1(node)) {
            elem = registeredAds[11][0]             // .pop();
            //console.log('AD: poped 1x1 - '+elem);
        }
        
        if(typeof elem === 'undefined') {
            elem = getDefaultRegisteredAd();
            //console.log('AD: poped default - '+elem);
        }
        
        if(typeof elem !== 'undefined') {
            $(elem).after(node);
            //console.log('AD: added after '+elem);
            
            // as long as not defer async request, set latest elem as default
            //if (!isDefer(node)) {
            //    defaultRegisteredAd = elem;
            //    console.log('AD: set default');
            //}
        }
    }
}

/*
document.write = function(node) {
    if(typeof node !== 'undefined') {
        var n = $(node)[2].innerHTML.lastIndexOf("swfobject.embedSWF");
        if(n == -1){
            var d = $(node)[2].innerHTML.slice(n,100000000);
            var data = d.split("/");
            var chk = data[5].split("_");
            if(typeof chk[1] !== 'undefined') {
                var elem = registeredAds[chk[1]].pop();
                if(typeof elem !== 'undefined') {
                    $(elem).after(node);
                }
            }
        }
    }
}
*/

var getDefaultRegisteredAd = function() {
    var elem;
    if (typeof defaultRegisteredAd !== 'undefined') {
        elem = defaultRegisteredAd;
        //console.error('AD: set default already - '+elem);
    } else if (registeredAds[300250].length > 0) {
        var lastIndex = registeredAds[300250].length-1;
        elem = registeredAds[300250][lastIndex];
        //console.error('AD: set default 300x250 - '+elem);
    } else if (registeredAds[11].length > 0) {
        var lastIndex = registeredAds[11].length-1;
        elem = registeredAds[11][lastIndex];
        //console.error('AD: set default 1x1 - '+elem);
    } else if (registeredAds[72890].length > 0) {
        var lastIndex = registeredAds[72890].length-1;
        elem = registeredAds[72890][lastIndex];
        //console.error('AD: set default 728x90 - '+elem);
    }
    return elem;
}

var isDefer = function(node) {
    return node.lastIndexOf("defer") != -1;
}

var is1x1 = function(node) {
    if (node.lastIndexOf("width='1'") != -1 && node.lastIndexOf("height='1'") != -1) {
        return true;
    }
    if (node.lastIndexOf("width=\"1\"") != -1 && node.lastIndexOf("height=\"1\"") != -1) {
        return true;
    }
    if (node.lastIndexOf("width=1") != -1 && node.lastIndexOf("height=1") != -1) {
        return true;
    }
    if (node.lastIndexOf("1x1") != -1) {
        return true;
    }
    return false;
}

var is300x250 = function(node) {
    if (node.lastIndexOf("300px") != -1 && node.lastIndexOf("250px") != -1) {
        return true;
    }
    if (node.lastIndexOf("'300'") != -1 && node.lastIndexOf("'250'") != -1) {
        return true;
    }
    if (node.lastIndexOf("\"300\"") != -1 && node.lastIndexOf("\"250\"") != -1) {
        return true;
    }
    if (node.lastIndexOf("width=300") != -1 && node.lastIndexOf("height=250") != -1) {
        return true;
    }
    if (node.lastIndexOf("300x250") != -1) {
        return true;
    }
    return false;
}

var is728x90 = function(node) {
    if (node.lastIndexOf("728px") != -1 && node.lastIndexOf("90px") != -1) {
        return true;
    }
    if (node.lastIndexOf("'728'") != -1 && node.lastIndexOf("'90'") != -1) {
        return true;
    }
    if (node.lastIndexOf("\"728\"") != -1 && node.lastIndexOf("\"90\"") != -1) {
        return true;
    }
    if (node.lastIndexOf("width=728") != -1 && node.lastIndexOf("height=90") != -1) {
        return true;
    }
    if (node.lastIndexOf("728x90") != -1) {
        return true;
    }
    return false;
}

var BB_MAG_1x1                  = 337876666690;
var BB_MAG_300x250              = 336814706671;
var BB_MAG_728x90               = 643242756821;
var BB_HOME_1x1                 = 510902096674;
var BB_HOME_300x250             = 550424006675;
var BB_HOME_728x90              = 852850346820;
var BB_ARTICLE_1x1              = 985037346676;
var BB_ARTICLE_300x250          = 244781106677;
var BB_ARTICLE_728x90           = 942495036804;
var BB_ARTICLE_LANDING_1x1      = 691373536678;
var BB_ARTICLE_LANDING_300x250  = 247130236679;
var BB_ARTICLE_LANDING_728x90   = 630501056805;
var BB_COMM_1x1                 = 512344316680;
var BB_COMM_300x250             = 586656756681;
var BB_COMM_728x90              = 688071966808;
var BB_COMM_LANDING_1x1         = 516615626682;
var BB_COMM_LANDING_300x250     = 982948656683;
var BB_COMM_LANDING_728x90      = 576698686809;
var BB_BCOMM_1x1                = 886585016684;
var BB_BCOMM_300x250            = 652876066685;
var BB_BCOMM_728x90             = 565242836806;
var BB_BCOMM_LANDING_1x1        = 975430386686;
var BB_BCOMM_LANDING_300x250    = 382773116687;
var BB_BCOMM_LANDING_728x90     = 619687626807;
var BB_OTHER_1x1                = 887185786688;     // profile
var BB_OTHER_300x250            = 263403256689;     // profile
var BB_OTHER_728x90             = 477069296823;     // message, mobile

var BB_COMM_P1_1x1              = 940313186737;
var BB_COMM_P1_300x250          = 995251896691;     
var BB_COMM_P1_728x90           = 395216266815;     // mobile article page
var BB_COMM_LANDING_P1_1x1      = 469337956699;     // comm discovery
var BB_COMM_LANDING_P1_300x250  = 772388026700;     // comm discovery
var BB_COMM_LANDING_P1_728x90   = 415742666810;
var BB_COMM_P2_1x1              = 376893136692;
var BB_COMM_P2_300x250          = 894219356670;
var BB_COMM_P2_728x90           = 136559036816;     
var BB_COMM_LANDING_P2_1x1      = 633668986701;     // fp
var BB_COMM_LANDING_P2_300x250  = 602161336702;     // fp
var BB_COMM_LANDING_P2_728x90   = 435015746811;     // fp
var BB_COMM_P3_1x1              = 689189426693;     // game
var BB_COMM_P3_300x250          = 418858546694;     // game
var BB_COMM_P3_728x90           = 203743246817;     // game
var BB_COMM_LANDING_P3_1x1      = 339054986703;     // campaign
var BB_COMM_LANDING_P3_300x250  = 487874436704;     // campaign
var BB_COMM_LANDING_P3_728x90   = 984442326812;     // campaign

// OBSOLETE!!
var BB_MY_MAG_1x1               = 714880026672;
var BB_MY_MAG_300x250           = 785814486673;
var BB_MY_MAG_728x90            = 717503756822;
var BB_COMM_P4_1x1              = 722211466695;
var BB_COMM_P4_300x250          = 925733136696;
var BB_COMM_P4_728x90           = 502038016818;
var BB_COMM_LANDING_P4_1x1      = 589191186705;
var BB_COMM_LANDING_P4_300x250  = 150519856706;
var BB_COMM_LANDING_P4_728x90   = 230571536813;
var BB_COMM_P5_1x1              = 247393906697;     
var BB_COMM_P5_300x250          = 842233366698;     
var BB_COMM_P5_728x90           = 684802916819;     
var BB_COMM_LANDING_P5_1x1      = 216750496707;
var BB_COMM_LANDING_P5_300x250  = 252220566708;
var BB_COMM_LANDING_P5_728x90   = 742473156814;
