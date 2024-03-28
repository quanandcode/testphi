export default function handleURL(url,params={}){
    url =  new URL(url? url:window.location.href)
    //set params
    params.index? url.searchParams.set('page_index', params.index) : null
    params.search? url.searchParams.set('search', params.search) : null
    //change window url
    window.history.replaceState({}, {}, url.toString());
    return url.toString()
}