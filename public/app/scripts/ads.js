
var load_ads = function(page_code) {
    if (!window.af) 
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
    
    if(af.existcookie('__AF')){
        afid = af.getcookie('__AF'); 
    } else {
        afid = 0; 
    } 
    randomstr = new String (Math.random()); 
    randomstr = randomstr.substring(2,8); 
    document.write("<" + "script language='JavaScript' type='text/javascript' src='"); 
    document.write("http://servedby.adsfactor.net/adj.php?ts=" + randomstr + "&amp;sid=336814706671&amp;afid=" + afid);
    if(document.af_used){
        document.write("&amp;ex=" + document.af_used);
    } 
    if(window.location.href){
        document.write("&amp;location=" + encodeURIComponent(escape(window.location.href)));
    } 
    if(document.referrer){
        document.write("&amp;referer=" + encodeURIComponent(escape(document.referrer)));
    } 
    document.write("'><" + "/script>");
    document.write("<noscript><a href='http://servedby.adsfactor.net/adc.php?sid=336814706671' ><img src='http://servedby.adsfactor.net/adv.php?sid=336814706671' border='0'></a></noscript>");
}