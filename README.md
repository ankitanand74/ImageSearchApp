<div align="center">

# Image Search

A simple android app to search for images over the internet using the Imgur search API.
Following are some of the trending features employed in this app:-

Image search using Imgur search API with trending features like Infinite scrolling with pre-caching and asynchronous loading of images, Zoom and pan in full-screen mode on individual images, History of the previous searches in a persistent layer and real-time search with efficient network usage using Volley and Glide library.

<img src="https://user-images.githubusercontent.com/22789194/29027804-bb5b8178-7b71-11e7-9672-a5c624521ee8.jpeg">

</div>

### API Used

[Imgur custom search api](https://apidocs.imgur.com/) - To receive images from all possible sources upon request. Queried using GET Gallery Search.

### Libraries Used
  - [Volley](https://github.com/google/volley) - For network call, image loading and caching.
  - [Glide](https://github.com/bumptech/glide) - For specifically handling image requests and caching them.

### Installing

To make the application work, add your own key following the [api-guide](https://apidocs.imgur.com/#4cc03220-6ad9-bdd2-f652-4efcced22697) and insert them into [AppConstants file](https://github.com/ankitanand74/ImageSearchApp/blob/master/app/src/main/java/com/example/ankit/photosearch/utils/AppConstants.java) in the following field mentioned below:-

'APP_CLIENT_ID'
  
### License

MIT
