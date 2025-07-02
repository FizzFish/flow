package cn.sast.common

import java.util.Timer
import java.util.TimerTask

/**
 * Simple wrapper around [Timer] that executes a repeating action.
 */
open class CustomRepeatingTimer(
    private val interval: Long,
    private val action: () -> Unit
) {
    private var timer: Timer? = null

    var isRepeats: Boolean = true
        internal set

    fun start() {
        val t = Timer()
        val task = object : TimerTask() {
            override fun run() {
                action()
                if (!isRepeats) {
                    cancel()
                }
            }
        }
        t.scheduleAtFixedRate(task, interval, interval)
        timer = t
    }

    open fun stop() {
        timer?.let { runningTimer ->
            runningTimer.cancel()
            runningTimer.purge()
            timer = null
        }
    }
}
