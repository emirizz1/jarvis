package com.jarvis

import android.app.Activity
import android.os.Bundle
import android.speech.*
import android.speech.tts.TextToSpeech
import android.widget.*
import android.view.Gravity
import android.graphics.Color
import android.content.Intent
import android.view.MotionEvent
import com.jarvis.ui.CircularWaveView
import java.util.*

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var wave: CircularWaveView
    private lateinit var status: TextView
    private lateinit var log: TextView
    private var tts: TextToSpeech? = null
    private var sr: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0F1216"))
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(32,60,32,32)
        }

        root.addView(TextView(this).apply {
            text = "JARVIS"
            textSize = 26f
            setTextColor(Color.parseColor("#9AA0A8"))
            gravity = Gravity.CENTER
            letterSpacing = 0.2f
        })
        root.addView(TextView(this).apply {
            text = "gri daire • termux • shizuku"
            textSize = 11f
            setTextColor(Color.parseColor("#6B7280"))
            gravity = Gravity.CENTER
        })

        wave = CircularWaveView(this).apply {
            layoutParams = LinearLayout.LayoutParams(520,520).apply { topMargin=36; bottomMargin=24; gravity=Gravity.CENTER }
        }
        root.addView(wave)

        status = TextView(this).apply {
            text = "Basılı tut ve konuş"
            textSize = 13f
            setTextColor(Color.parseColor("#9AA0A8"))
            gravity = Gravity.CENTER
        }
        root.addView(status)

        log = TextView(this).apply {
            text = ""
            textSize = 13f
            setTextColor(Color.parseColor("#D1D5DB"))
            setPadding(16,16,16,16)
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#1A1F29")); cornerRadius=16f
            }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f).apply { topMargin=12; bottomMargin=12 }
        }
        root.addView(log)

        val row = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER }
        val mic = Button(this).apply {
            text = "🎤 BASILI TUT KONUŞ"
            setBackgroundColor(Color.parseColor("#9AA0A8"))
            setTextColor(Color.WHITE)
        }
        val send = Button(this).apply {
            text = "GÖNDER"
            setBackgroundColor(Color.parseColor("#252A33"))
            setTextColor(Color.WHITE)
        }
        val inp = EditText(this).apply {
            hint="veya yaz..."
            setTextColor(Color.WHITE); setHintTextColor(Color.parseColor("#6B7280"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        row.addView(inp); row.addView(send)
        root.addView(row)

        // Basili tut konus
        mic.setOnTouchListener { _, e ->
            when(e.action){
                MotionEvent.ACTION_DOWN -> startListen()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> stopListen()
            }; true
        }
        root.addView(mic)

        send.setOnClickListener { if(inp.text.isNotBlank()){ onUser(inp.text.toString()); inp.text.clear() } }
        inp.setOnEditorActionListener { _,_,_ -> if(inp.text.isNotBlank()){ onUser(inp.text.toString()); inp.text.clear() }; true }

        setContentView(root)
    }

    private fun startListen(){
        wave.setSpeaking(false)
        status.text="Dinliyorum..."
        sr = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        sr?.setRecognitionListener(object: RecognitionListener{
            override fun onResults(r: Bundle?) {
                val t=r?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if(t!=null) onUser(t) else status.text="Anlayamadım"
            }
            override fun onError(e:Int){ status.text="Hata: $e"; wave.setSpeaking(false) }
            override fun onRmsChanged(v:Float){ /* sen konusurken dalga durur, o yuzden volume guncellemiyoruz */ }
            override fun onReadyForSpeech(p:Bundle?){}
            override fun onBeginningOfSpeech(){}
            override fun onBufferReceived(b:ByteArray?){}
            override fun onEndOfSpeech(){}
            override fun onEvent(a:Int,b:Bundle?){}
        })
        sr?.startListening(intent)
    }
    private fun stopListen(){ sr?.stopListening(); status.text="İşleniyor..." }

    private fun onUser(text:String){
        log.append("\nSen: $text\n")
        // Burada normalde Gemini/Local'e gidecek, simdi demo cevap
        val reply = "Anladım \"$text\" — bunu öğrendim, bir dahakine dalga daha hızlı olacak."
        jarvisSpeak(reply)
        // Shizuku örneği: whatsapp aç
        // rish -c "am start -n com.whatsapp"
    }

    private fun jarvisSpeak(text:String){
        log.append("Jarvis: $text\n")
        status.text = "Jarvis konuşuyor..."
        wave.setSpeaking(true)
        // TTS
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis")
        // Sahte volume animasyonu (gercek Visualizer ile de yapilabilir)
        val h=android.os.Handler(mainLooper)
        var c=0
        val r=object:Runnable{
            override fun run(){
                if(c<35){ wave.setVolume((20+Math.random()*80).toInt()); c++; h.postDelayed(this,90) }
                else { wave.setSpeaking(false); status.text="Basılı tut ve konuş" }
            }
        }; h.post(r)
    }

    override fun onInit(s:Int){
        if(s==TextToSpeech.SUCCESS){ tts?.language=Locale("tr","TR"); tts?.setSpeechRate(1.0f) }
    }
    override fun onDestroy(){ tts?.stop(); tts?.shutdown(); sr?.destroy(); super.onDestroy() }
}
