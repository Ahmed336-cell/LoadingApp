package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val intentExtras = intent.extras!!
        val downloadFileName = intentExtras.getString("DOWNLOAD_FILE")
        val downloadFileStatus = intentExtras.getString("STATUS")

        file_name.text = downloadFileName
        if (downloadFileStatus == "Success") {
            status.setTextColor(getColor(R.color.colorPrimaryDark))
        } else {
            status.setTextColor(getColor(R.color.red))
        }
        status.text = downloadFileStatus

        ok_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val notificationManager =
            ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
        notificationManager?.cancelAllNotifications()
    }

}