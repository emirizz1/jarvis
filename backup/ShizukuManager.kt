package com.jarvis.shizuku

import rikka.shizuku.Shizuku
import java.io.BufferedReader

/**
 * SHIZUKU - Sinirsiz yetki
 * Normal app'in yapamadigi her seyi yaptirir
 */
object ShizukuManager {

    fun isReady(): Boolean = try { Shizuku.pingBinder() } catch(e: Exception) { false }

    fun runCommand(cmd: String, onResult: (String)->Unit) {
        if (!isReady()) { onResult("Shizuku not running"); return }
        try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", cmd), null, null)
            process.waitFor()
            val out = process.inputStream.bufferedReader().readText()
            onResult(out)
        } catch(e: Exception) { onResult("err: ${e.message}") }
    }

    // Telefon ac
    fun answerCall() = runCommand("service call telecom 11") {}
    // Telefon kapat
    fun endCall() = runCommand("service call telecom 6") {}
    // Sesi hatta enjekte et (Android 13+ direct, alti fallback hoparlor)
    fun injectAudio(text: String) {
        runCommand("cmd media_session dispatch") {}
        // Termux TTS fallback'i de buradan tetiklenebilir
    }
    // Izinleri toptan ver
    fun grantAll(packageName: String) {
        val perms = listOf(
            "android.permission.ANSWER_PHONE_CALLS",
            "android.permission.READ_PHONE_STATE",
            "android.permission.CALL_PHONE",
            "android.permission.WRITE_SECURE_SETTINGS"
        )
        perms.forEach { runCommand("pm grant $packageName $it") {} }
    }
}
