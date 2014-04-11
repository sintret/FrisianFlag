package com.frisianflag.app;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Andifitria on 11/04/2014.
 */
public class Util {

    static String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    final static String TARGET_BASE_PATH = extStorageDirectory + "/frisianFlag/";

    public boolean checkFile(String fullname) {
        File file = new File(fullname);
        if (file.exists())
            return true;
        else return false;
    }
    /*
    Create directory in sd card
     */

    public void  createDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                Log.e("tag", "create directory");
            } else {
                Log.e("tag", "Directory filed to created");
            }
        } else Log.e("tag", "file exist");
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public void copyFileOrDir(Context myContext, String path) {
        AssetManager assetManager = myContext.getAssets();
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() " + path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFileAsset(myContext, path);
            } else {
                String fullPath = TARGET_BASE_PATH + path;
                Log.i("tag", "path=" + fullPath);
                File dir = new File(fullPath);
                if (!dir.exists())
                    if (!dir.mkdirs()) ;
                Log.i("tag", "could not create dir " + fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";

                    copyFileOrDir(myContext, p + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    public void copyFileAsset(Context myContext, String filename) {
        AssetManager assetManager = myContext.getAssets();

        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() " + filename);
            in = assetManager.open(filename);
            if (filename.endsWith(".jpg")) // extension was added to avoid compression on APK file
                newFileName = TARGET_BASE_PATH + filename.substring(0, filename.length() - 4);
            else
                newFileName = TARGET_BASE_PATH + filename;
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
            Log.e("tag", "Exception in copyFile() of " + newFileName);
            Log.e("tag", "Exception in copyFile() " + e.toString());
        }

    }
}
