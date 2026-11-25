package com.jarvis

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40,80,40,40) }
        
        val title = TextView(this).apply { text = "JARVIS v3 - Öğrenen"; textSize = 22f }
        val status = TextView(this).apply { text = "Durum: Hazır. Shizuku: kontrol ediliyor..."; textSize = 14f }
        val input = EditText(this).apply { hint = "Jarvis'e söyle: örn. Ayşe'ye wp'den naber yaz" }
        val btn = Button(this).apply { text = "GÖNDER / ÖĞRET" }
        val log = TextView(this).apply { text = "Log:\n"; textSize = 12f }

        btn.setOnClickListener {
            val istek = input.text.toString()
            if(istek.isBlank()) return@setOnClickListener
            log.text = log.text.toString() + "\n> $istek\n[REFLEX kontrol ediliyor...]"
            // Burada JarvisBrain.istekAl(istek) çağrılacak
            // İlk seferde keşif, sonra skill kaydı
            Toast.makeText(this, "Jarvis düşünüyor: $istek", Toast.LENGTH_SHORT).show()
        }

        layout.addView(title); layout.addView(status); layout.addView(input); layout.addView(btn); layout.addView(log)
        setContentView(layout)
    }
}
