import SlideShow from "./slideshow.js";
window.onload = function(){
    console.log(lazyLoadSubnav)
    localStorage.removeItem('username');
    SlideShow(0);
}