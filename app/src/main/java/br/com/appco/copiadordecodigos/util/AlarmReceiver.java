package br.com.appco.copiadordecodigos.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import br.com.appco.copiadordecodigos.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Construa a notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.minhascontassemfundo)
                .setContentTitle("Conta próxima do vencimento")
                .setContentText("Sua conta vencerá em 2 dias")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Nome do Canal", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification.createNotificationChannel(channel);
        }

    }
}
