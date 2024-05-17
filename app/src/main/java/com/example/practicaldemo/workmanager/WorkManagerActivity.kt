package com.example.practicaldemo.workmanager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.practicaldemo.R
import com.example.practicaldemo.databinding.ActivityWorkManagerBinding
import java.util.concurrent.TimeUnit

class WorkManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkManagerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkManagerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            oneTimeBtn.setOnClickListener {
                oneTimeWork()
            }

            periodicTimeBtn.setOnClickListener {
                periodicWork()
            }
        }

    }

    fun oneTimeWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(true)
            .build()

        val myWorkRequest: WorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)
    }

    fun periodicWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val myWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("my_tag")
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "my_tag", ExistingPeriodicWorkPolicy.KEEP, myWorkRequest
        )

    }
}