package hs.project.medicine;

import android.app.Application;
import android.content.Context;

public class MediApplication extends Application {
    private static Context context;

    public void onCreate(){
        super.onCreate();
        MediApplication.context = getApplicationContext();

        // firebaseCrashlytics 허용
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    }

    public static Context ApplicationContext(){
        return MediApplication.context;
    }
}
