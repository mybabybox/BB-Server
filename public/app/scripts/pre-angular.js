// facebook append #_=_ to redirect uri if not set 
// http://stackoverflow.com/questions/7131909/facebook-callback-appends-to-return-url
if (window.location.hash == '#_=_') {
    window.location.hash = ''; // for older browsers, leaves a # behind
    history.pushState('', document.title, window.location.pathname); // nice and clean
    //event.preventDefault(); // no page reload
}

// backward compatible with angular hash mode
if (location.hash.match(/^#[^!]/)) { 
    location.hash = location.hash.replace(/^#/, "#!");
}
