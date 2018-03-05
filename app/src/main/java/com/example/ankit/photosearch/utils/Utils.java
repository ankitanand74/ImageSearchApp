package com.example.ankit.photosearch.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class Utils {
    private static Context context;
    private static final String TAG = Utils.class.getName();
    private static final String BASEURL = "https://api.imgur.com/3/gallery/search/";

    public static void setContext(Context cnxt){
        context = cnxt;
    }

    //make URL from base url and userText
    public static Uri.Builder getUri(String query) {
        Uri baseuri = Uri.parse(BASEURL+1);
        Uri.Builder uriBuilder = baseuri.buildUpon();
        uriBuilder.appendQueryParameter("q", query);
        Log.d(TAG,"context is " +context);
        if(!ConnectivityUtils.isConnectedWifi(context) || !ConnectivityUtils.isConnectedFast(context)){
            uriBuilder.appendQueryParameter("q_size_px", "small");
        }
        return uriBuilder;
    }

    public static Uri.Builder getUri(String query, int offset) {
        Uri baseuri = Uri.parse(BASEURL+offset);
        Uri.Builder uriBuilder = baseuri.buildUpon();
        uriBuilder.appendQueryParameter("q", query);
        Log.d(TAG,"context is " +context);
        if(!ConnectivityUtils.isConnectedWifi(context) || !ConnectivityUtils.isConnectedFast(context)){
            uriBuilder.appendQueryParameter("q_size_px", "small");
        }
        Log.d(TAG, "get Uri offset called " + uriBuilder);
        return uriBuilder;
    }

    // Extract json from string response

    public static List<String> extractImages(String sampleJsonResponse) {

        List<String> imageUrlList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(sampleJsonResponse);

            JSONArray itemsArray = baseJsonResponse.getJSONArray("data");
            if (itemsArray != null) {

                Log.d(TAG, "images is " + itemsArray.length());
                for (int i = 0; i < itemsArray.length(); i++) {

                    JSONObject itemsArrayJSONObject = itemsArray.getJSONObject(i);
                    JSONArray imagesJSONArray = null;
                    if (itemsArrayJSONObject.has("images"))
                        imagesJSONArray = itemsArrayJSONObject.getJSONArray("images");

                    JSONObject imagesJSONObject = null;
                    if (imagesJSONArray != null) {
                        Log.d(TAG, "images is " + imagesJSONArray.length());
                        imagesJSONObject = imagesJSONArray.getJSONObject(0);
                    }

                    if (imagesJSONObject != null && imagesJSONObject.has("link")){
                        String currLink = imagesJSONObject.getString("link");
                        if(currLink.contains("jpg") || currLink.contains("png"))
                            imageUrlList.add(currLink);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("Utils", "Problem parsing", e);
        }

        // return list of image urls after parsing
        Log.d(TAG, " " + imageUrlList);
        return imageUrlList;
    }

    // get resource from imageview
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static boolean checkImageResource(Context ctx, ImageView imageView, int imageResource) {
        boolean result = false;

        if (ctx != null && imageView != null && imageView.getDrawable() != null) {
            Drawable.ConstantState constantState;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                constantState = ctx.getResources()
                        .getDrawable(imageResource, ctx.getTheme())
                        .getConstantState();
            } else {
                constantState = ctx.getResources().getDrawable(imageResource)
                        .getConstantState();
            }

            if (imageView.getDrawable().getConstantState() == constantState) {
                result = true;
            }
        }
        return result;
    }
}