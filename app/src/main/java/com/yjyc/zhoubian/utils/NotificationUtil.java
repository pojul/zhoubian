package com.yjyc.zhoubian.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.yjyc.zhoubian.R;

public class NotificationUtil {

    private static RemoteViews rv;

    public static void notifyDownLoad(Context context, int progress) {
        if(rv == null){
            rv = new RemoteViews(context.getPackageName(), R.layout.notify_download_apk);
            rv.setTextViewText(R.id.progress_tv, progress + "%");
            rv.setProgressBar(R.id.progress, 100, progress, false);
            rv.setImageViewResource(R.id.icon, R.mipmap.icon_round);
       /* Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);*/
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    /*.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)//设置铃声及震动效果等
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setPriority(Notification.PRIORITY_DEFAULT)  //通知的优先级
                    .setCategory(Notification.CATEGORY_MESSAGE)  //通知的类型
                    .setContentIntent(pi)*/
                    .setCustomContentView(rv);
            //.setCustomBigContentView(rv);
            //.setFullScreenIntent(pi, true);
            Notification notification = mBuilder.build();
            notificationManager.notify(1012, notification);
        }else{
            rv.setTextViewText(R.id.progress_tv, progress + "%");
            rv.setProgressBar(R.id.progress, 100, progress, false);
        }
    }

    public static void notifyCancelDownLoad(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1012);
        rv = null;
    }

}
