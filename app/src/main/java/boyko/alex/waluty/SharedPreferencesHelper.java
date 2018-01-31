package boyko.alex.waluty;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sasha on 31.01.2018.
 *
 * Save buy from/to course
 */

class SharedPreferencesHelper {
    private SharedPreferencesHelper(){}

    static void setBuyFrom(double value) {
        SharedPreferences sharedPref = ApplicationController.getInstance().getSharedPreferences("currencies1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("from", String.valueOf(value));
        editor.apply();
    }

    static void setBuyTo(double value) {
        SharedPreferences sharedPref = ApplicationController.getInstance().getSharedPreferences("currencies1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("to", String.valueOf(value));
        editor.apply();
    }

    static double getBuyFrom(){
        return Double.valueOf(ApplicationController.getInstance().getSharedPreferences("currencies1", Context.MODE_PRIVATE).getString("from", "0"));
    }

    static double getBuyTo(){
        return Double.valueOf(ApplicationController.getInstance().getSharedPreferences("currencies1", Context.MODE_PRIVATE).getString("to", "0"));
    }
}
