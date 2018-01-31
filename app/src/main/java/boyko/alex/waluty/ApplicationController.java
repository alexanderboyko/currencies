package boyko.alex.waluty;

import android.app.Application;

/**
 * Created by Sasha on 31.01.2018.
 */

public class ApplicationController extends Application {
    private static ApplicationController appInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
    }

    public static synchronized ApplicationController getInstance() {
        return appInstance;
    }
}
