// Force fullscreen when launched from home screen
if (window.navigator.standalone || window.matchMedia('(display-mode: standalone)').matches) {
    // Hide address bar on iOS
    window.addEventListener('load', function() {
        setTimeout(function() {
            window.scrollTo(0, 1);
        }, 100);
    });
    
    // Prevent zoom
    document.addEventListener('touchmove', function(e) {
        if(e.scale !== 1) e.preventDefault();
    }, { passive: false });
}

// Listen for app launch
window.addEventListener('DOMContentLoaded', function() {
    if (!window.navigator.standalone) {
        // Show "Add to Home Screen" prompt
        if (window.matchMedia('(display-mode: browser)').matches) {
            alert("For best experience, add this app to your home screen!");
        }
    }
});