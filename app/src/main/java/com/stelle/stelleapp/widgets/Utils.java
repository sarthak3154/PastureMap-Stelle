package com.stelle.stelleapp.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Sarthak on 29-04-2017
 */

public class Utils {

    public static final int OPEN_SANS_REGULAR = 0;
    public static final int PT_SANS_WEB_REGULAR = 1;
    public static final int DOSIS_SEMI_BOLD = 2;

    public static Typeface getThisTypeFace(Context context, int type) {
        Typeface typeface;
        switch (type) {

            case OPEN_SANS_REGULAR:
                typeface = FontCache.getFont(context, "fonts/OpenSans-Regular.ttf");
                break;
            case PT_SANS_WEB_REGULAR:
                typeface = FontCache.getFont(context, "fonts/PT_Sans-Web-Regular.ttf");
                break;
            default:
                typeface = FontCache.getFont(context, "fonts/OpenSans-Regular.ttf");
                break;
        }
        return typeface;
    }

    public static void showToast(Context context, String msgString) {
        Toast.makeText(context, msgString, Toast.LENGTH_SHORT).show();
    }


    public static void showSnackBar(View viewById, String msgString) {
        Snackbar.make(viewById, msgString, Snackbar.LENGTH_SHORT).show();
    }

}
