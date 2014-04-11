package com.frisianflag.app;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends ActionBarActivity {

    public WebView mWebView;
    static String extStorageDirectory =  Environment.getExternalStorageDirectory().toString();
    final static String TARGET_BASE_PATH = extStorageDirectory+"/frisianFlag/";
    String fullname="";
     ViewFlipper mViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
  /*      getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(false);*/
        getActionBar().hide();

        // create dir
        File dir = new File(TARGET_BASE_PATH+"/doc");
        if(!dir.exists()){
            dir.mkdirs();
            Log.e("tag","create directory successfully");
        } else {
            Log.e("tag","directory exist");
        }


        //createDir("frisianFlag");
        //copyFileOrDir("doc");

        // set the flipper
        mViewFlipper = (ViewFlipper) findViewById(R.id.flipview);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        // Enable Javascript
        //mWebView.setWebChromeClient(new WebChromeClient());
        //mWebView.setWebViewClient(new MyWebViewClient());
        //mWebView.setWebViewClient(new WebChromeClient());

      //  mWebView.getSettings().setJavaScriptEnabled(true);
       // mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.loadUrl("file:///android_asset/index.html");

        mWebView.setWebViewClient(new MyWebViewClient());
       // mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);*/

    }

    public void home(){
        mWebView.loadUrl("file:///android_asset/index.html");
    }

    private void animate() {
        mViewFlipper.setInAnimation(getBaseContext(), R.anim.grow_from_middle);
        mViewFlipper.setOutAnimation(getBaseContext(), R.anim.shrink_to_middle);
        mViewFlipper.showNext();
      /*
        Animation anim = AnimationUtils.loadAnimation(getBaseContext(),
                android.R.anim.slide_in_left);
        view.startAnimation(anim);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                home();
                return true;
            case R.id.action_logout:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.endsWith("pdf")){

                 //start
                String[] part;
                String name;
                part = url.split("doc/");
                name=part[1];
                fullname = TARGET_BASE_PATH+"/doc/"+name;

                // PDF reader code //
                File file = new File(fullname);
                Toast.makeText(MainActivity.this, "open document..", Toast.LENGTH_LONG).show();
                if (!file.exists())
                {
                   copyFile(name);
                    //Toast.makeText(MainActivity.this, name + " Not Found.....", Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),"application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //end

                return false;
            } else {
                animate();
                view.loadUrl(url);
                return true;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    public void copyFile(String filename) {
        AssetManager assetManager = this.getAssets();

        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() "+filename);
            in = assetManager.open("doc/" +filename);
            if (filename.endsWith(".jpg")) // extension was added to avoid compression on APK file
                newFileName = TARGET_BASE_PATH + "doc/" + filename.substring(0, filename.length()-4);
            else
                newFileName = TARGET_BASE_PATH + "doc/" +filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }

    }

   /* public void copyFileChannel(String name) {
        AssetManager assetManager = this.getAssets();
        String destination = TARGET_BASE_PATH+"doc/"+name;
        String src = assetManager.toString()+"/doc/"+name;
        File source = new File(src);
        File dest = new File(destination);
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            //inputChannel = assetManager.open(s).getClass().get
            //inputChannel = assetManager.open(s);
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            inputChannel.close();
            outputChannel.close();
        }
        catch (IOException e) {
            Log.e("tag",e.getMessage());
            Log.e("tag",destination);
            Log.e("tag",src);
        }
    }*/

}
