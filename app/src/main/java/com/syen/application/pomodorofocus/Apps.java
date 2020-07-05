package com.syen.application.pomodorofocus;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;

// A helper class that stores information about apps
public class Apps {
    private String pkgName;
    private String appName;
    public static PackageManager pm;

    public Apps(String pkgName) throws PackageManager.NameNotFoundException {
        this.pkgName = pkgName;
        ApplicationInfo applicationInfo = pm.getApplicationInfo(pkgName, 0);
        this.appName = (String) pm.getApplicationLabel(applicationInfo);
    }

    // Setting package manager so that it can be used later, must be initialised
    public static void setPm(Context context){
        pm = context.getPackageManager();
    }

    public String getName(){
        return appName;
    }

    // A function that gives icon of a selected app on demand
    public Bitmap getIcon(Context context){
        Drawable img;
        try {
            img = pm.getApplicationIcon(pkgName);
        } catch(PackageManager.NameNotFoundException e){
            Log.e("CHECK336", "Image not found for "+ pkgName);
            img = context.getResources().getDrawable(R.drawable.ic_adb_black_48dp);
        }
        return getBitmapFromDrawable(img);
    }

    public String getPkgName(){
        return pkgName;
    }

    // A helper function that converts icon drawable to bitmap, obtained from stackoverflow
    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        // Basically it draws the icon over an empty canvas
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }
}
