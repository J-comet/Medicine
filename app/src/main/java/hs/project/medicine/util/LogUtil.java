package hs.project.medicine.util;

import android.util.Log;

public class LogUtil {
    private static String lineOut() {
        int level = 4;
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        return (" at " + traces[level] + " ");
    }

    public static String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]");
        sb.append(message);
        return sb.toString();
    }

    // log debug
    public static void d(String message) {
        if (message.length() > 3000) {
            Log.d("MSG:::By-LogUtil:::", message.substring(0, 3000));
            d(message.substring(3000));
        } else {
            Log.d("MSG:::By-LogUtil:::", buildLogMsg(message + "::" + lineOut()));
        }
    }

    public static void e(String message) {
        if (message.length() > 3000) {
            Log.e("MSG:::By-LogUtil:::", message.substring(0, 3000));
            e(message.substring(3000));
        } else {
            Log.e("MSG:::By-LogUtil:::", buildLogMsg(message + "::" + lineOut()));
        }
    }
}
