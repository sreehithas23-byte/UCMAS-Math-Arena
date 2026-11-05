package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundHapticManager(private val context: Context) {
    private var toneGen: ToneGenerator? = null
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var isSoundEnabled: Boolean = true
    var isHapticEnabled: Boolean = true

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (_: Exception) {
            toneGen = null
        }
    }

    fun playBeadClick() {
        if (isSoundEnabled) {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
            } catch (_: Exception) {}
        }
        performHaptic(20)
    }

    fun playCorrectChime() {
        if (isSoundEnabled) {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
            } catch (_: Exception) {}
        }
        performHaptic(60)
    }

    fun playVictoryFanfare() {
        if (isSoundEnabled) {
            try {
                toneGen?.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_L, 300)
            } catch (_: Exception) {}
        }
        performSuccessPattern()
    }

    fun playErrorBuzz() {
        if (isSoundEnabled) {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
            } catch (_: Exception) {}
        }
        performHaptic(120)
    }

    fun playFlashTick() {
        if (isSoundEnabled) {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 50)
            } catch (_: Exception) {}
        }
        performHaptic(15)
    }

    fun playCoinSound() {
        if (isSoundEnabled) {
            try {
                toneGen?.startTone(ToneGenerator.TONE_CDMA_PIP, 80)
            } catch (_: Exception) {}
        }
        performHaptic(30)
    }

    private fun performHaptic(durationMs: Long) {
        if (!isHapticEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun performSuccessPattern() {
        if (!isHapticEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 40, 60, 80, 60, 100)
                val amplitudes = intArrayOf(0, 150, 0, 200, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(200)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        toneGen?.release()
        toneGen = null
    }
}
