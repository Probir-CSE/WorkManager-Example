package com.probir.workmanagerexample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Probir Bhowmik on 04-Feb-20. Soft BD Ltd. Email:probirbhowmikcse@gmail.com
 */
public class MyWorker extends Worker {
    private NotificationManager notificationManager;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {
        setForegroundAsync(createForegroundInfo());
        rpt();
        return Result.success();
    }


    //-----
    private ForegroundInfo createForegroundInfo() {

        // Build a notification using bytesRead and contentLength

        Context context = getApplicationContext();
        String id = context.getString(R.string.notification_channel_id);
        String title = context.getString(R.string.notification_title);
        String cancel = context.getString(R.string.cancel);
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        Notification notification = new NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                .addAction(android.R.drawable.ic_delete, cancel, intent)
                .build();

        return new ForegroundInfo(1, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        // Create a Notification channel

        String channelId = "task_channel";
        String channelName = "task_name";

        NotificationChannel channel = new
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }
    //-----

    public void getAndSaveDateAndTime() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "/DateTime/");
            if (!file.exists()) {
                file.mkdir();
                Toast.makeText(getApplicationContext(), "mkdir", Toast.LENGTH_SHORT).show();
            }

            //read file----
            File fileEvents = new File(Environment.getExternalStorageDirectory(), "DateTime/sample.txt");
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileEvents));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
            }
            String result = text.toString();
            ////------end of read file

            File myFileName = new File(file, "sample.txt");
            FileWriter fileWriter = new FileWriter(myFileName);
            Date currentTime = Calendar.getInstance().getTime();
            fileWriter.write(result + "\n" + currentTime.toString() +" From Work");
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception e) {
        }
    }

    public void rpt() {

        final Handler h;

        h = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                getAndSaveDateAndTime();
                            }
                        });
                        TimeUnit.SECONDS.sleep(5);
                    } catch (Exception ex) {
                    }
                }
            }
        }).start();
    }
    //---


}
