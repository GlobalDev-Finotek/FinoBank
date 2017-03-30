package finotek.global.dev.talkbank_ca.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Locale;

public class TBFonts {
    public static Typeface normalTypeFace = null;
    public static Typeface mediumTypeFace = null;
    public static Typeface lightTypeFace = null;

    private TBFonts(){

    }

    public static Typeface normalTypeFace(Context context){
        if(normalTypeFace == null) {
            AssetManager am = context.getApplicationContext().getAssets();
            normalTypeFace = Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "NotoSansCJKkr-Regular.otf"));
            return normalTypeFace;
        } else{
            return normalTypeFace;
        }
    }

    public static Typeface mediumTypeFace(Context context){
        if(mediumTypeFace == null) {
            AssetManager am = context.getApplicationContext().getAssets();
            mediumTypeFace = Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "NotoSansCJKkr-Medium.otf"));
            return mediumTypeFace;
        } else{
            return mediumTypeFace;
        }
    }

    public static Typeface lightTypeFace(Context context){
        if(lightTypeFace == null) {
            AssetManager am = context.getApplicationContext().getAssets();
            lightTypeFace = Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "NotoSansCJKkr-Light.otf"));
            return lightTypeFace;
        } else{
            return lightTypeFace;
        }
    }
}
