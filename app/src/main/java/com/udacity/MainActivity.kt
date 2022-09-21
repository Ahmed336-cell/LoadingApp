package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 0

    private var glideDownloadId = 0L
    private var repositoryDownloadId = 0L
    private var retrofitDownloadId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel(
            getString(R.string.notification_downloaded_channel_id),
            getString(R.string.notification_title)
        )

        custom_button.setOnClickListener {
            if (download_file_group.checkedRadioButtonId != -1) custom_button.setLoadingState(
                ButtonState.Clicked
            )
            when (download_file_group.checkedRadioButtonId) {
                R.id.download_file_1 -> download( getString(R.string.repo_glide))
                R.id.download_file_2 -> download( getString(R.string.repo_app))
                R.id.download_file_3 -> download( getString(R.string.repo_ret))
                else -> Toast.makeText(
                    this,
                    getString(R.string.no_option_selected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id!!))
            if (cursor.moveToNext()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val notificationManager =
                    ContextCompat.getSystemService(context!!, NotificationManager::class.java)
                cursor.close()
                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        notificationManager?.sendNotification(
                            context.getString(R.string.notification_description),
                            context,
                            context.getString(
                                when (id) {
                                    glideDownloadId -> R.string.glide_image_loading_library
                                    repositoryDownloadId -> R.string.loadapp_current_repository
                                    else -> R.string.retrofit_type_face_http_client
                                }
                            ),
                            getString(R.string.Failed)
                        )
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        notificationManager?.sendNotification(
                            context.getString(R.string.notification_description),
                            context,
                            context.getString(
                                when (id) {
                                    glideDownloadId -> R.string.glide_image_loading_library
                                    repositoryDownloadId -> R.string.loadapp_current_repository
                                    else -> R.string.retrofit_type_face_http_client
                                }
                            ),
                            getString(R.string.success)
                        )
                    }
                }
            }
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        when (url) {
            // enqueue puts the download request in the queue.
            getString(R.string.repo_glide) -> glideDownloadId = downloadManager.enqueue(request)
            getString(R.string.repo_app) -> repositoryDownloadId = downloadManager.enqueue(request)
            getString(R.string.repo_ret) -> retrofitDownloadId = downloadManager.enqueue(request)
        }
    }



    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager =
                ContextCompat.getSystemService(this, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

}
