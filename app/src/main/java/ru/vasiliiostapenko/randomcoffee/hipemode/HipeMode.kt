package ru.vasiliiostapenko.randomcoffee.hipemode

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class HipeMode {
    fun setBrightness(layout: WindowManager.LayoutParams, window: Window) {
        layout.screenBrightness = 1f
        window.setAttributes(layout)
    }

    fun vibrator(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(context, VibratorManager::class.java) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            getSystemService(context, Vibrator::class.java) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            val pattern = longArrayOf(
                0,
                200,
                100,
                30000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, 12)
                vibrator.vibrate(effect)
            } else {
                vibrator.vibrate(pattern, 12)
            }
        }
    }

    fun setAudio(context: Context) {
        val player = ExoPlayer.Builder(context).build()
        val videoUri = Uri.parse(AUDIO_URL)
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        player.volume = 1f
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0
        )
        player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        player.prepare()
        player.play()


    }

    companion object {
        const val AUDIO_URL = "https://gostudents.ru/krutite-baraban"
    }
}