package hs.project.medicine.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import androidx.core.content.ContextCompat;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.activitys.SplashActivity;

public class NetworkUtil {

    public static boolean checkConnectedNetwork(Context context) {

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
        LogUtil.d("hasNetwork/" + hasNetwork);
        return hasNetwork;
    }

    /**
     * @param activity
     * @param isFinishActivity  Activity 종료 여부
     */
    public static void networkErrorDialogShow(Activity activity, boolean isFinishActivity) {

        if (!activity.isFinishing()) {
            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle("네트워크 경고")
                    .setMessage("네트워크에 연결되지 않았습니다")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            if (isFinishActivity) {
                                activity.finish();
                            }
                        }
                    }).create();

            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
        }
    }

}
