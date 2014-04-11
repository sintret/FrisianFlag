package com.frisianflag.app;


import android.content.Context;
import android.webkit.WebView;

/**
 * Created by Andy fitria on 11/04/2014.
 */
public class GIFView extends WebView {

    public GIFView(Context context, String path) {
        super(context);
        loadUrl(path);
    }
}
