package hs.project.medicine.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean checkConnectedNetwork(Context context){

        boolean hasNetwork = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {

                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    hasNetwork = true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    hasNetwork = true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    hasNetwork = true;
                }
            }

        } else {

            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

            if (activeNetworkInfo != null) {
                switch (activeNetworkInfo.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                    case ConnectivityManager.TYPE_MOBILE:
                    case ConnectivityManager.TYPE_WIMAX:
                    case ConnectivityManager.TYPE_ETHERNET:
                        hasNetwork = true;
                        break;

                    default:
                        hasNetwork = false;
                        break;
                }
            }
        }
        LogUtil.d("hasNetwork/"+hasNetwork);
        return hasNetwork;
    }

}
