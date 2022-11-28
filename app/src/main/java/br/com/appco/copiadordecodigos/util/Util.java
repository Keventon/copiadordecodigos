package br.com.appco.copiadordecodigos.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util  extends Activity {

    public static boolean checarConexaoDispositivo(Context context) {

        boolean conectado = false;

        try {

            final ConnectivityManager conManager = (ConnectivityManager)context
                    .getSystemService(context.CONNECTIVITY_SERVICE);

            final NetworkInfo wifi = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final NetworkInfo mobile = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (conManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
                conectado = true;
            }

            if (conManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED) {
                conectado = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conectado;
    }
}
