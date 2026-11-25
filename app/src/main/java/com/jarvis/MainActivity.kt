package com.jarvis
import android.app.Activity
import android.os.Bundle
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40,80,40,40) }
        val title = TextView(this).apply { text = "JARVIS Minimal - Calisiyor!"; textSize = 22f }
        val status = TextView(this).apply { text = "Shizuku: hazir\nOgrenme: aktif\nSonraki adim: Whatsapp ogret"; textSize = 14f }
        val input = EditText(this).apply { hint = "Jarvis'e soyle..." }
        val btn = Button(this).apply { text = "GONDER" }
        btn.setOnClickListener { Toast.makeText(this, "Jarvis dusunuyor: ${input.text}", Toast.LENGTH_SHORT).show() }
        layout.addView(title); layout.addView(status); layout.addView(input); layout.addView(btn)
        setContentView(layout)
    }
}
