package com.hisu.english4kids

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hisu.english4kids.databinding.ActivityMainBinding
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.utils.receiver.NotificationReceiver
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!
    private var backPressTimeMilliSeconds = 0L
    private val pressIntervalSeconds = 2 * 1000
    private lateinit var mExitToast: Toast
    private lateinit var mMediaPlayer:MediaPlayer
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val localDataManager = LocalDataManager()
        localDataManager.init(this)

        setUpMediaPlayer()
        initTextToSpeech()

//        createNotyChannel()
//        scheduleNotification()
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            if(it == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.UK
            }
        })
    }

     fun speakText(text:String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    private fun setUpMediaPlayer() {
        mMediaPlayer = MediaPlayer()

        mMediaPlayer.setOnPreparedListener {
            it?.start()
        }

        mMediaPlayer.setOnCompletionListener {
            it?.stop()
            it?.reset()
        }
    }

    private fun createNotyChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notyChannel = NotificationChannel(
                getString(R.string.notification_learning_channel_id),
                getString(R.string.notification_learning_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationMgr =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationMgr.createNotificationChannel(notyChannel)
        }
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 1,
            intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

//test send right away
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            System.currentTimeMillis(),
//            pendingIntent
//        )

//Setup send notification at certain time of day
        val firingCal= Calendar.getInstance()
        val currentCal = Calendar.getInstance()

        firingCal.set(Calendar.HOUR, 10)
        firingCal.set(Calendar.MINUTE, 33)
        firingCal.set(Calendar.SECOND, 0)

        var intendedTime = firingCal.timeInMillis
        val currentTime = currentCal.timeInMillis

        if(intendedTime >= currentTime){
            // set from today
            alarmManager.setRepeating(AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent)
        } else{
            // set from next day
            firingCal.add(Calendar.DAY_OF_MONTH, 1)
            intendedTime = firingCal.timeInMillis

            alarmManager.setRepeating(AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent)
        }
    }

    override fun onNavigateUp(): Boolean {
        return super.onNavigateUp()
    }

    override fun onBackPressed() {
        /*
          Press back button twice within 2s to exit program
         */
        if (backPressTimeMilliSeconds + pressIntervalSeconds > System.currentTimeMillis()) {
            mExitToast.cancel()
            moveTaskToBack(true) //Only move to back not close the whole app
            return
        } else {
           runOnUiThread {
               mExitToast = Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT)
               mExitToast.show()
           }
        }

        backPressTimeMilliSeconds = System.currentTimeMillis()
    }

    fun playAudio(uri: String) {
        try {
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            mMediaPlayer.setDataSource(this, Uri.parse(uri))
            mMediaPlayer.prepareAsync()
        } catch(e: Exception) {
            Log.e(MainActivity::class.java.name, e.message ?: "Error while trying to play audio")
        }
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
    }
}