package com.example.week6handlerthreadappwb

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.week6handlerthreadappwb.databinding.FragmentButtonsBinding
import java.util.*

class ButtonsFragment : Fragment() {
    private var isThreadRunning = false
    private lateinit var binding: FragmentButtonsBinding
    private var pauseOffset: Long = 0
    private var chronometer20secDelimiter = 1
    private var currentChronometerTime: Long = 0L
    private val random = Random()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentButtonsBinding.inflate(inflater, container, false)

        binding.playButton.setOnClickListener { startThreadCalculation() }
        binding.pauseButton.setOnClickListener { pauseThreadCalculation() }
        binding.resetButton.setOnClickListener { resetThreadCalculation() }

        binding.threadChronometer.setOnChronometerTickListener {
            currentChronometerTime = SystemClock.elapsedRealtime() - binding.threadChronometer.base

            if (currentChronometerTime / chronometer20secDelimiter > 20000) {
                chronometer20secDelimiter++
                val randomColor: Int = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
                binding.layout.setBackgroundColor(randomColor)
            }
        }

        return binding.root
    }

    private fun startThreadCalculation() {
        isThreadRunning = true
        binding.playButton.isEnabled = false
        binding.resetButton.isEnabled = false

        if (isThreadRunning) {
            binding.threadChronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            binding.threadChronometer.start()
        }

        Thread {
            var counter = 4 //расчет начинается с 4 символов числа ПИ (3.14 стоит в TextView по умолчанию)
            while (isThreadRunning) {
                val pi = PiCalculation.piSpigot(counter).toString()
                val piChar = pi.substring(pi.length - 1, pi.length)
                counter += 1
                parentFragmentManager.setFragmentResult(
                    "requestPiCharKey", bundleOf("PI_CHAR_KEY" to piChar)
                )
                Thread.sleep(10)
            }
        }.start()
    }

    private fun pauseThreadCalculation() {
        isThreadRunning = false
        binding.playButton.isEnabled = true
        binding.resetButton.isEnabled = true

        if (!isThreadRunning) {
            binding.threadChronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - binding.threadChronometer.base
        }
    }

    private fun resetThreadCalculation() {
        isThreadRunning = false

        if (!isThreadRunning) {
            binding.threadChronometer.base = SystemClock.elapsedRealtime()
            pauseOffset = 0
            currentChronometerTime = 0L
            chronometer20secDelimiter = 1
            binding.threadChronometer.stop()
        }

        binding.playButton.isEnabled = true
        val isReset = true
        parentFragmentManager.setFragmentResult(
            "requestResultKey",
            bundleOf("RESET_KEY" to isReset)
        )
    }
}