package util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import com.yukon.notes.R;

public class Utils {

    public static void setThemeColor(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String color = sharedPreferences.getString("color_list","Red");
        switch (color){
            case "Red":
                context.setTheme(R.style.Theme_Notes_Red);
                break;
            case "Blue":
                context.setTheme(R.style.Theme_Notes_Blue);
                break;
        }
    }

    public static void setDarkTheme(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = sharedPreferences.getString("theme_list","System Default");
        switch (theme) {
            case "Light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "Dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "System Default":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

    }

}
