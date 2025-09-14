package com.example.fogsonar

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.PI

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 101
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val scope = CoroutineScope(Dispatchers.Default)
    private var audioJob: Job? = null
    private var isFirstStart = true

    private lateinit var statusTextView: TextView
    private lateinit var startStopButton: Button
    private lateinit var detectedFrequencyTextView: TextView
    private lateinit var logTextView: TextView
    private lateinit var logTitleTextView: TextView
    private lateinit var waveformView: WaveformView
    private lateinit var tts: TextToSpeech

    // Audio configuration
    private val SAMPLE_RATE = 44100 // Hz
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val FFT_SIZE = 1024
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT) * 2

    // Target frequencies for triggering, with associated narration
    private val TARGET_FREQUENCIES = mapOf(
        261.63 to "লিগ্যাসি স্ক্রোল আপডেট হয়েছে—কয়েল অনুরণন লক্ষ্য স্বাক্ষরের সাথে মিলেছে।",
        392.00 to "৩৯২ হার্জে সোনার পিং লগ করা হয়েছে—ভূখণ্ডের ব্যতিক্রম সনাক্ত হয়েছে।",
        523.25 to "৫২৩ হার্জে ট্র্যাকার কোর সক্রিয় হয়েছে—প্রথম প্রতিধ্বনি ম্যাপ করা হয়েছে।"
    )
    private val FREQUENCY_TOLERANCE = 5.0 // +/- Hz for detection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTextView = findViewById(R.id.statusTextView)
        startStopButton = findViewById(R.id.startStopButton)
        detectedFrequencyTextView = findViewById(R.id.detectedFrequencyTextView)
        logTextView = findViewById(R.id.logTextView)
        logTitleTextView = findViewById(R.id.logTitleTextView)
        waveformView = findViewById(R.id.waveformView)

        tts = TextToSpeech(this, this)

        startStopButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                checkAndStartRecording()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("bn", "BD"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                Log.d("TTS", "TTS Initialized")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    private fun speak(text: String) {
        if (::tts.isInitialized) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, "")
        }
    }

    private fun checkAndStartRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
        } else {
            startRecording()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording()
            } else {
                statusTextView.text = "Microphone permission denied."
            }
        }
    }

    private fun startRecording() {
        if (audioRecord ==.class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 101
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val scope = CoroutineScope(Dispatchers.Default)
    private var audioJob: Job? = null
    private var isFirstStart = true

    private lateinit var statusTextView: TextView
    private lateinit var startStopButton: Button
    private lateinit var detectedFrequencyTextView: TextView
    private lateinit var logTextView: TextView
    private lateinit var logTitleTextView: TextView
    private lateinit var waveformView: WaveformView
    private lateinit var tts: TextToSpeech

    // Audio configuration
    private val SAMPLE_RATE = 44100 // Hz
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val FFT_SIZE = 1024
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT) * 2

    // Target frequencies for triggering, with associated narration
    private val TARGET_FREQUENCIES = mapOf(
        261.63 to "লিগ্যাসি স্ক্রোল আপডেট হয়েছে—কয়েল অনুরণন লক্ষ্য স্বাক্ষরের সাথে মিলেছে।",
        392.00 to "৩৯২ হার্জে সোনার পিং লগ করা হয়েছে—ভূখণ্ডের ব্যতিক্রম সনাক্ত হয়েছে।",
        523.25 to "৫২৩ হার্জে ট্র্যাকার কোর সক্রিয় হয়েছে—প্রথম প্রতিধ্বনি ম্যাপ করা হয়েছে।"
    )
    private val FREQUENCY_TOLERANCE = 5.0 // +/- Hz for detection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTextView = findViewById(R.id.statusTextView)
        startStopButton = findViewById(R.id.startStopButton)
        detectedFrequencyTextView = findViewById(R.id.detectedFrequencyTextView)
        logTextView = findViewById(R.id.logTextView)
        logTitleTextView = findViewById(R.id.logTitleTextView)
        waveformView = findViewById(R.id.waveformView)

        tts = TextToSpeech(this, this)

        startStopButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                checkAndStartRecording()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("bn", "BD"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                Log.d("TTS", "TTS Initialized")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    private fun speak(text: String) {
        if (::tts.isInitialized) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, "")
        }
    }

    private fun checkAndStartRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
        } else {
            startRecording()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording()
            } else {
                statusTextView.text = "Microphone permission denied."
            }
        }
    }

    private fun startRecording() {
        if (audioRecord == null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                BUFFER_SIZE
            )
        }

        if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
            if (isFirstStart) {
                val milestone = "Tracker Core Online — Realme 14 Pro activated, legacy narration embedded."
                val milestoneBengali = "ট্র্যাকার কোর অনলাইন—Realme 14 Pro সক্রিয়, লিগ্যাসি বর্ণনা এমবেড করা হয়েছে।"
                appendLog(milestone)
                speak(milestoneBengali)
                isFirstStart = false
            }

            audioRecord?.startRecording()
            isRecording = true
            statusTextView.text = "Recording audio..."
            startStopButton.text = "Stop Sonar"
            appendLog("Sonar started.")

            audioJob = scope.launch {
                val audioBuffer = ShortArray(FFT_SIZE)
                while (isRecording) {
                    val read = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0
                    if (read == FFT_SIZE) {
                        val (detectedFreq, narration) = analyzeFrequency(audioBuffer, read)
                        withContext(Dispatchers.Main) {
                            detectedFrequencyTextView.text = "Detected Freq: %.2f Hz".format(detectedFreq)
                            waveformView.updateWaveform(audioBuffer)
                            if (narration != null) {
                                appendLog(narration)
                                speak(narration)
                            }
                        }
                    } else if (read > 0) {
                        Log.w("FogSonar", "Read less than FFT_SIZE samples: $read")
                    }
                }
            }
            Log.d("FogSonar", "Recording started.")
        } else {
            statusTextView.text = "Failed to initialize AudioRecord."
            Log.e("FogSonar", "AudioRecord not initialized.")
        }
    }

    private fun stopRecording() {
        isRecording = false
        audioJob?.cancel()
        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null
        statusTextView.text = "Sonar stopped."
        startStopButton.text = "Start Sonar"
        detectedFrequencyTextView.text = "Detected Freq: -- Hz"
        appendLog("Sonar stopped.")
        Log.d("FogSonar", "Recording stopped.")
    }

    private fun analyzeFrequency(audioBuffer: ShortArray, readSize: Int): Pair<Double, String?> {
        if (readSize == 0 || readSize != FFT_SIZE) return Pair(0.0, null)

        val real = DoubleArray(FFT_SIZE)
        val imag = DoubleArray(FFT_SIZE)

        for (i in 0 until FFT_SIZE) {
            val window = 0.5 * (1.0 - cos(2 * PI * i / (FFT_SIZE - 1)))
            real[i] = audioBuffer[i] * window / Short.MAX_VALUE.toDouble()
            imag[i] = 0.0
        }

        fft(real, imag, false)

        var maxMagnitude = 0.0
        var maxIndex = 0

        for (i in 0 until FFT_SIZE / 2) {
            val magnitude = sqrt(real[i] * real[i] + imag[i] * imag[i])
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude
                maxIndex = i
            }
        }

        val detectedFreq = maxIndex * (SAMPLE_RATE.toDouble() / FFT_SIZE)

        var narration: String? = null
        for ((targetFreq, message) in TARGET_FREQUENCIES) {
            if (detectedFreq >= targetFreq - FREQUENCY_TOLERANCE &&
                detectedFreq <= targetFreq + FREQUENCY_TOLERANCE) {
                narration = message
                break
            }
        }

        return Pair(detectedFreq, narration)
    }

    private fun fft(real: DoubleArray, imag: DoubleArray, inverse: Boolean) {
        val n = real.size
        if (n <= 1) return

        val evenReal = DoubleArray(n / 2)
        val evenImag = DoubleArray(n / 2)
        val oddReal = DoubleArray(n /2)
        val oddImag = DoubleArray(n / 2)

        for (k in 0 until n / 2) {
            evenReal[k] = real[2 * k]
            evenImag[k] = imag[2 * k]
            oddReal[k] = real[2 * k + 1]
            oddImag[k] = imag[2 * k + 1]
        }

        fft(evenReal, evenImag, inverse)
        fft(oddReal, oddImag, inverse)

        for (k in 0 until n / 2) {
            val angle = 2 * PI * k / n * (if (inverse) -1 else 1)
            val wkReal = cos(angle)
            val wkImag = sin(angle)

            val oddTermReal = oddReal[k] * wkReal - oddImag[k] * wkImag
            val oddTermImag = oddReal[k] * wkImag + oddImag[k] * wkReal

            real[k] = evenReal[k] + oddTermReal
            imag[k] = evenImag[k] + oddTermImag

            real[k + n / 2] = evenReal[k] - oddTermReal
            imag[k + n / 2] = evenImag[k] - oddTermImag
        }

        if (inverse) {
            for (k in 0 until n) {
                real[k] /= n
                imag[k] /= n
            }
        }
    }

    private fun appendLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val currentLog = logTextView.text.toString()
        val newLog = "$timestamp - $message\n$currentLog"
        logTextView.text = newLog.take(2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        scope.cancel()
    }
}
 null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                BUFFER_SIZE
            )
        }

        if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
            if (isFirstStart) {
                val milestone = "Tracker Core Online — Realme 14 Pro activated, legacy narration embedded."
                val milestoneBengali = "ট্র্যাকার কোর অনলাইন—Realme 14 Pro সক্রিয়, লিগ্যাসি বর্ণনা এমবেড করা হয়েছে।"
                appendLog(milestone)
                speak(milestoneBengali)
                isFirstStart = false
            }

            audioRecord?.startRecording()
            isRecording = true
            statusTextView.text = "Recording audio..."
            startStopButton.text = "Stop Sonar"
            appendLog("Sonar started.")

            audioJob = scope.launch {
                val audioBuffer = ShortArray(FFT_SIZE)
                while (isRecording) {
                    val read = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0
                    if (read == FFT_SIZE) {
                        val (detectedFreq, narration) = analyzeFrequency(audioBuffer, read)
                        withContext(Dispatchers.Main) {
                            detectedFrequencyTextView.text = "Detected Freq: %.2f Hz".format(detectedFreq)
                            waveformView.updateWaveform(audioBuffer)
                            if (narration != null) {
                                appendLog(narration)
                                speak(narration)
                            }
                        }
                    } else if (read > 0) {
                        Log.w("FogSonar", "Read less than FFT_SIZE samples: $read")
                    }
                }
            }
            Log.d("FogSonar", "Recording started.")
        } else {
            statusTextView.text = "Failed to initialize AudioRecord."
            Log.e("FogSonar", "AudioRecord not initialized.")
        }
    }

    private fun stopRecording() {
        isRecording = false
        audioJob?.cancel()
        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null
        statusTextView.text = "Sonar stopped."
        startStopButton.text = "Start Sonar"
        detectedFrequencyTextView.text = "Detected Freq: -- Hz"
        appendLog("Sonar stopped.")
        Log.d("FogSonar", "Recording stopped.")
    }

    private fun analyzeFrequency(audioBuffer: ShortArray, readSize: Int): Pair<Double, String?> {
        if (readSize == 0 || readSize != FFT_SIZE) return Pair(0.0, null)

        val real = DoubleArray(FFT_SIZE)
        val imag = DoubleArray(FFT_SIZE)

        for (i in 0 until FFT_SIZE) {
            val window = 0.5 * (1.0 - cos(2 * PI * i / (FFT_SIZE - 1)))
            real[i] = audioBuffer[i] * window / Short.MAX_VALUE.toDouble()
            imag[i] = 0.0
        }

        fft(real, imag, false)

        var maxMagnitude = 0.0
        var maxIndex = 0

        for (i in 0 until FFT_SIZE / 2) {
            val magnitude = sqrt(real[i] * real[i] + imag[i] * imag[i])
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude
                maxIndex = i
            }
        }

        val detectedFreq = maxIndex * (SAMPLE_RATE.toDouble() / FFT_SIZE)

        var narration: String? = null
        for ((targetFreq, message) in TARGET_FREQUENCIES) {
            if (detectedFreq >= targetFreq - FREQUENCY_TOLERANCE &&
                detectedFreq <= targetFreq + FREQUENCY_TOLERANCE) {
                narration = message
                break
            }
        }

        return Pair(detectedFreq, narration)
    }

    private fun fft(real: DoubleArray, imag: DoubleArray, inverse: Boolean) {
        val n = real.size
        if (n <= 1) return

        val evenReal = DoubleArray(n / 2)
        val evenImag = DoubleArray(n / 2)
        val oddReal = DoubleArray(n /2)
        val oddImag = DoubleArray(n / 2)

        for (k in 0 until n / 2) {
            evenReal[k] = real[2 * k]
            evenImag[k] = imag[2 * k]
            oddReal[k] = real[2 * k + 1]
            oddImag[k] = imag[2 * k + 1]
        }

        fft(evenReal, evenImag, inverse)
        fft(oddReal, oddImag, inverse)

        for (k in 0 until n / 2) {
            val angle = 2 * PI * k / n * (if (inverse) -1 else 1)
            val wkReal = cos(angle)
            val wkImag = sin(angle)

            val oddTermReal = oddReal[k] * wkReal - oddImag[k] * wkImag
            val oddTermImag = oddReal[k] * wkImag + oddImag[k] * wkReal

            real[k] = evenReal[k] + oddTermReal
            imag[k] = evenImag[k] + oddTermImag

            real[k + n / 2] = evenReal[k] - oddTermReal
            imag[k + n / 2] = evenImag[k] - oddTermImag
        }

        if (inverse) {
            for (k in 0 until n) {
                real[k] /= n
                imag[k] /= n
            }
        }
    }

    private fun appendLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val currentLog = logTextView.text.toString()
        val newLog = "$timestamp - $message\n$currentLog"
        logTextView.text = newLog.take(2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        scope.cancel()
    }
}
