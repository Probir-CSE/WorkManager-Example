package com.probir.workmanagerexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ContentUriTriggers;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SyncRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//---------------------------------Service-------------------------------
        startService(new Intent(this, MyService.class));

//---------------------------------JWorkManager--------------------------
//        This is the subclass of our WorkRequest
//        final OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class).build();
//        WorkManager.getInstance().enqueue(workRequest);

//        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
//        workManager.enqueue(new OneTimeWorkRequest.Builder(MyWorker.class).build());

//        PeriodicWorkRequest periodicWorkRequest=new PeriodicWorkRequest.Builder(MyWorker.class,)

//---------------------------------JobScheduler--------------------------
        ComponentName componentName = new ComponentName(this, MyJobScheduler.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(2000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);


    }


}

