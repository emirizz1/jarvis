package com.jarvis.brain

import com.jarvis.skills.SkillStore
import com.jarvis.service.JarvisAccessibilityService
import kotlinx.coroutines.*

/**
 * HIBRIT BEYIN
 * Internet varsa -> Gemini 2.5 Pro (zeki)
 * Yoksa / gizliyse -> Gemma 3 4B local (Ollama)
 * Once SKILL var mi bakar, varsa dusunmez.
 */
class JarvisBrain(
    private val skillStore: SkillStore,
    private val geminiKey: String
) {

    suspend fun istekAl(kullaniciIstegi: String) {
        // 1. HIZLI YOL: Skill var mi?
        val skill = skillStore.ara(kullaniciIstegi)
        if (skill != null) {
            println("[JARVIS REFLEX] ${skill.id} ile yapiyorum, dusunmuyorum")
            skillStore.calistir(skill, kullaniciIstegi)
            return
        }

        // 2. YAVAS YOL: Kesfet ve ogren
        println("[JARVIS KESIF] Bilmiyorum, ogreniyorum: $kullaniciIstegi")
        val trace = kesfet(kullaniciIstegi) // vision loop

        if (trace.basarili) {
            // 3. OGREN: Trace'i genellestirip skill'e cevir
            val yeniSkill = LearningEngine.traceToSkill(trace, kullaniciIstegi, geminiKey)
            skillStore.kaydet(yeniSkill)
            println("[JARVIS OGRENDI] ${yeniSkill.id} kaydedildi!")
        }
    }

    private suspend fun kesfet(istek: String): Trace {
        val service = JarvisAccessibilityService.get()!!
        val adimlar = mutableListOf<AkimAdimi>()

        repeat(12) { // max 12 adim
            val ekran = service.ekraniGor()
            // Gemini Vision'a ekran + ui gonder
            val karar = GeminiVision.sor(
                apiKey = geminiKey,
                istek = istek,
                ekranUi = ekran.ui,
                gecmisAdimlar = adimlar
            )

            when (karar.action) {
                "dokun" -> { service.dokun(karar.x, karar.y); adimlar.add(AkimAdimi("dokun", karar)) }
                "yaz" -> { service.yaz(karar.text); adimlar.add(AkimAdimi("yaz", karar)) }
                "uygulama_ac" -> { acUygulama(karar.app); adimlar.add(AkimAdimi("uygulama_ac", karar)) }
                "bitti" -> return Trace(adimlar, true)
            }
            delay(800)
        }
        return Trace(adimlar, false)
    }

    private fun acUygulama(packageName: String) {
        val intent = JarvisAccessibilityService.instance?.packageManager?.getLaunchIntentForPackage(packageName)
        JarvisAccessibilityService.instance?.startActivity(intent)
    }
}

data class Trace(val adimlar: List<AkimAdimi>, val basarili: Boolean)
data class AkimAdimi(val tip: String, val karar: GeminiKarar)

// TODO: Gemini Vision API wrapper buraya
object GeminiVision {
    suspend fun sor(apiKey: String, istek: String, ekranUi: List<Any>, gecmisAdimlar: List<AkimAdimi>): GeminiKarar {
        // Gercek implementasyonda: Google AI Studio Gemini 2.5 Pro Vision endpoint'e
        // screenshot base64 + ui json + prompt gonderilir
        return GeminiKarar("dokun", 500, 800, "", "")
    }
}
data class GeminiKarar(val action: String, val x: Int, val y: Int, val text: String, val app: String)

object LearningEngine {
    suspend fun traceToSkill(trace: Trace, istek: String, apiKey: String): com.jarvis.skills.Skill {
        // LLM'e: "su adimlari genellestir, parametreleri degisken yap" dedirt
        // Ornek: "Ayse" -> {kisi} yap
        return com.jarvis.skills.Skill(
            id = "skill_${System.currentTimeMillis()}",
            aciklama = istek,
            tetikler = listOf(istek.take(10)),
            adimlar = trace.adimlar.map { it.karar.toString() },
            kullanim = 0
        )
    }
}
