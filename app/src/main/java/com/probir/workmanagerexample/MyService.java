package com.probir.workmanagerexample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Probir Bhowmik on 04-Feb-20. Soft BD Ltd. Email:probirbhowmikcse@gmail.com
 */
public class MyService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("text")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(0, notification);
        rpt();
        //do heavy work on a background thread
        //stopSelf();
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }


    }

    private void getAndSaveDateAndTime() {
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
            fileWriter.write(result + "\n" + currentTime.toString() + " From Service");
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception e) {
        }
    }

    private void rpt() {

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
                        TimeUnit.MINUTES.sleep(5);
                    } catch (Exception ex) {
                    }
                }
            }
        }).start();
    }


}
