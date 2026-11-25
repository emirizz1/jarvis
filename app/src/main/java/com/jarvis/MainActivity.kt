package com.jarvis.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

/**
 * JARVIS SES DALGASI
 * - Sen konuşurken: DURUR (donuk çizgi)
 * - Jarvis konuşurken: DALGALANIR (sin wave animasyon)
 */
class VoiceWaveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#00E5FF") // Jarvis mavisi
        strokeWidth = 6f
        style = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0A0E14"))
            setPadding(40, 80, 40, 40)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val title = TextView(this).apply {
            text = "JARVIS"
            textSize = 28f
            setTextColor(Color.parseColor("#00E5FF"))
            gravity = Gravity.CENTER
            letterSpacing = 0.15f
        }
        val subtitle = TextView(this).apply {
            text = "Kendi kendine öğrenen"
            textSize = 12f
            setTextColor(Color.parseColor("#8892A6"))
            gravity = Gravity.CENTER
        }

        // === SES DALGASI VIEW ===
        waveView = VoiceWaveView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200).apply {
                topMargin = 60
                bottomMargin = 40
            }
        }

        statusText = TextView(this).apply {
            text = "Dinlemek için mikrofona bas"
            textSize = 13f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        input = EditText(this).apply {
            hint = "veya buraya yaz..."
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#1A2332"))
            background = getDrawable(android.R.drawable.edit_text)
        }

        val micBtn = Button(this).apply {
            text = "🎤 BASILI TUT KONUŞ"
            setBackgroundColor(Color.parseColor("#00E5FF"))
            setTextColor(Color.BLACK)
        }

        val sendBtn = Button(this).apply {
            text = "GÖNDER"
            setBackgroundColor(Color.parseColor("#1A2332"))
            setTextColor(Color.WHITE)
        }

        // Mikrofon: basılı tut konuş
        micBtn.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> startListening()
                android.view.MotionEvent.ACTION_UP -> stopListening()
            }
            true
        }

        sendBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isNotBlank()) onUserMessage(text)
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(waveView)
        root.addView(statusText)
        root.addView(micBtn)
        root.addView(input)
        root.addView(sendBtn)

        setContentView(root)
    }

    private fun startListening() {
        isListening = true
        waveView.setSpeaking(false) // sen konuşurken dalga DURUR
        statusText.text = "Dinliyorum..."
        statusText.setTextColor(Color.parseColor("#00E5FF"))
        // SpeechRecognizer başlat
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
        }
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { onUserMessage(it) }
            }
            override fun onReadyForSpeech(p: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {} // burada wave'i volume ile oynatabilirsin ama sen konuşurken duracak
            override fun onBufferReceived(b: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(e: Int) { statusText.text = "Anlayamadım, tekrar dene" }
            override fun onEvent(t: Int, p: Bundle?) {}
        })
        speechRecognizer?.startListening(intent)
    }

    private fun stopListening() {
        isListening = false
        speechRecognizer?.stopListening()
        statusText.text = "İşleniyor..."
    }

    private fun onUserMessage(text: String) {
        statusText.text = "Sen: $text"
        input.setText("")
        // Burada JarvisBrain devreye girecek
        // Simülasyon: Jarvis cevap veriyor
        val cevap = "Anladım: $text. Bunu öğrendim, bir dahakine daha hızlı yapacağım."
        jarvisSpeak(cevap)
    }

    private fun jarvisSpeak(text: String) {
        statusText.text = "Jarvis: $text"
        waveView.setSpeaking(true) // Jarvis konuşurken DALGALANIR
        // TTS ile konuş + wave'i ses seviyesine göre titreştir
        tts?.speak(text, TextToSVeech.QUEUE_FLUSH, null, "jarvis_utterance")

        // Sahte volume animasyonu (gerçek TTS volume için UtteranceProgressListener + Visualizer gerekir)
        val fakeVolume = android.os.Handler(mainLooper)
        var tick = 0
        val runnable = object : Runnable {
            override fun run() {
                if (waveView.isAttachedToWindow && tick < 40) {
                    val vol = (30 + Math.random()*70).toInt()
                    waveView.setVolume(vol)
                    tick++
                    fakeVolume.postDelayed(this, 80)
                } else {
                    waveView.setSpeaking(false)
                }
            }
        }
        fakeVolume.post(runnable)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("tr", "TR")
            tts?.setSpeechRate(1.0f)
        }
    }

    override fun onDestroy() {
        tts?.stop(); tts?.shutdown()
        speechRecognizer?.destroy()
        super.onDestroy()
    }
}
