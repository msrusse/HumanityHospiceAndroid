package com.masonsrussell.humanityhospice_android;

public class PostInformation {
    static public String postID, PostImageURL, PosterUID, PosterName, Post, Timestamp;

    public static void setPostInfo(String id, String imageURL, String posterUid, String posterName, String post, String timestamp) {
        postID = id;
        PostImageURL = imageURL;
        PosterUID = posterUid;
        PosterName = posterName;
        Post = post;
        Timestamp = timestamp;
    }

}
