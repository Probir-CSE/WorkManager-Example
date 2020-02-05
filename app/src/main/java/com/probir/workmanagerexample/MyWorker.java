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


    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        rpt();
        return null;
    }

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
            fileWriter.write(result + "\n" + currentTime.toString());
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

}
