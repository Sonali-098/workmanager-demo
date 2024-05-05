package com.spdfs.workmanagerdemo

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.spdfs.workmanagerdemo.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: AirplaneModeChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        //create an object of the broadcast receiver
        receiver = AirplaneModeChangeReceiver()
        setContentView(binding.root)

        IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED).also { intentFilter ->
            // registering the receiver
            // intentFilter: parameter which is passed in  registerReceiver() function is the intent filter that we have just created
            registerReceiver(receiver, intentFilter)
        }
        workerButtonClickListener()
    }

    //unregister broadcast receiver
    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    private fun workerButtonClickListener() {
        binding.oneTimeWorkerButton.setOnClickListener {
            oneTimeWork()
        }

        binding.periodicWorkerButton.setOnClickListener {
            periodicTimeWork()
        }
    }

    private fun oneTimeWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(true)
            .build()

        val notificationOneTimeWorkRequest: WorkRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setConstraints(constraints = constraints)
            .build()

        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(notificationOneTimeWorkRequest)
        observeWorkRequest(workManager = workManager, workRequest = notificationOneTimeWorkRequest)
    }

    private fun observeWorkRequest(workManager: WorkManager, workRequest: WorkRequest) {
        //observer work manager response
        workManager.getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->
            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                Log.i("Observe", "Work manager output data is ${workInfo.outputData}")
            } else {
                Log.i("Observe", "Work manager output state is ${workInfo.state}")
            }
        }
    }

    private fun periodicTimeWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val notificationPeriodicWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(NotificationWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(constraints = constraints)
            .addTag("id")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "id", ExistingPeriodicWorkPolicy.KEEP, notificationPeriodicWorkRequest
        )
    }
}