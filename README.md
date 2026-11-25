# JARVIS APP - Kendi Kendine Öğrenen Android Asistan

Bu senin istediğin şey: **App olarak Jarvis. Kendi kendine alışa alışa öğrenen.**

Termux + Tasker dandikliğinden kurtulduk, direkt APK yapıyoruz.

## Mimarisi (Nasıl Öğreniyor?)

```
[ Kulak ] -> Brain (Gemini Cloud / Gemma Local Hibrit)
              |
              v
    Skill Kütüphanesi var mı? --EVET--> REFLEX (0.8sn) direkt çalıştır
              | HAYIR
              v
    Vision ile KEŞFET (ekran fotosu + UI ağacı gör, dokun/yaz ile dene)
              |
              v
    Başardı mı? --EVET--> Trace'i al -> LLM ile genelleştir -> SKILL olarak kaydet
              |
              v
    Bir daha aynı iş gelirse artık düşünmez, refleks olur.
```

Yani Voyager/Minecraft botu gibi. Ne kadar kullanırsan o kadar ustalaşıyor.

## İçindekiler
- `JarvisAccessibilityService.kt` -> Telefonun gözü ve elleri. Ekranı okur, dokunur, yazar. Shizuku varsa sistem seviyesinde.
- `JarvisBrain.kt` -> Hibrit beyin. İnternet varsa Gemini 2.5 Pro, yoksa telefondaki Gemma 3 4B.
- `SkillStore.kt` -> Öğrendiklerini Room DB + Vector'de tutar. `skill_library.json` değil, gerçek DB.
- `LearningEngine.kt` -> Yaptığı adımları izler, "bunu nasıl genelleştiririm" diye LLM'e sorar ve kaydeder.
- `CallHandler.kt` -> Shizuku ile arama aç/kapat + sesi hatta enjekte et.

## Kurulum (5 dk)

1. Android Studio'yu indir (PC lazım)
2. Bu `jarvis-app` klasörünü aç
3. `local.properties` ekle: `sdk.dir=/path/to/Android/Sdk`
4. Build > Make Project > Run (telefonunu USB ile bağla, USB Hata Ayıklama AÇIK)

İlk açılışta isteyecekleri:
- Erişilebilirlik izni -> VER
- Bildirim erişimi -> VER
- Shizuku izni -> Shizuku app > Authorized apps > Jarvis -> İzin ver
- Mikrofon -> VER

Sonra ana ekranda tek buton: **"Jarvis'i Uyandır"**

## Kullanım
De ki: "Jarvis whatsapp'tan Ayşe'ye naber yaz"

1. seferde 8-10 sn düşünür, ekranı göre göre yapar.
Ekranda küçük toast: `Öğrenildi: whatsapp_mesaj_gonder v1 kaydedildi!` yazar.

2. seferde "Mehmet'e yaz" dediğinde 1 sn'de yapar. Çünkü artık biliyor.

Aynısı arama için de geçerli. Biri aradığında ilk seferde öğrenir, sonra otomatik.

## Shizuku ile Sınırsız Mod
`ShizukuManager.kt` içinde `rish` komutları hazır. Root yok ama neredeyse root kadar yetki.

## Sonraki Adım
APK'yı build edince bana log at, birlikte ilk skill'i öğretelim. İlk öğretme en kritiği.

