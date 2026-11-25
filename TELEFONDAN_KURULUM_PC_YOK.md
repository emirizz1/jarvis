# PC YOKSA JARVIS NASIL KURULUR? (Telefondan)

Sıkıntı yok kanka, PC'siz de APK çıkarıyoruz. 2 yol var:

## YOL 1: EN KOLAY - GitHub ile Bulutta Build (Önerilen)
Hiçbir şey kurmuyorsun, GitHub senin yerine APK'yı yapıyor.

1. Telefondan github.com'a gir, hesap aç (2 dk)
2. Yeni repo oluştur: `jarvis-app` adında
3. Bu klasördeki tüm dosyaları yükle (Upload files)
   - `app/` klasörü
   - `.github/workflows/build.yml` zaten hazır
4. Yükleyince otomatik build başlar: Repo > Actions > `Jarvis APK Build` > yeşil tik bekle (5 dk)
5. Actions > Artifacts > `jarvis-debug-apk` indir -> APK hazır!
6. İndir, kur, Shizuku izni ver. Bitti.

Bu en temizi, telefonu yormuyor.

## YOL 2: TELEFONDA DIREKT BUILD (Termux + Gradle)
PC yok ama telefonu PC gibi kullanıyoruz. Biraz ağır, 3GB yer yer.

Termux'ta:
```bash
pkg update -y
pkg install openjdk-17 gradle git wget unzip -y
termux-setup-storage

# Android SDK'yı telefona kur (komut satırı tools)
mkdir -p ~/android-sdk && cd ~/android-sdk
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-*.zip
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null; true

export ANDROID_HOME=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Gerekli paketleri indir
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Projeyi al
cd ~
git clone https://github.com/SENIN_KULLANICIN/jarvis-app.git
cd jarvis-app
chmod +x gradlew
./gradlew assembleDebug
```
APK şurada oluşur: `app/build/outputs/apk/debug/app-debug.apk`
Dosya yöneticisinden kur.

## YOL 3: APK'SIZ ANINDA BASLA (Hemen kullanmak istiyorsan)
APK build ile uğraşmak istemiyorsan, aynı öğrenen mantığı Termux'ta çalıştır:

Bizim `agent_v3_ogrenen.py` zaten PC'siz çalışıyor ve Shizuku'n var.
```bash
cd ~/jarvis-android
python agent_v3_ogrenen.py
```
Bu da aynı şekilde öğreniyor, sadece arayüzü app değil terminal. Skill'leri `skill_library.json`'a kaydediyor.
APK hazır olana kadar bununla idare edebilirsin, mantık birebir aynı.

---

Hangi yolu seçiyorsun? 
- GitHub yolunu seçersen sana adım adım ekran görüntülü anlatayım, repo linkini at yeter build'i kontrol edeyim.
- Termux yolunu seçersen `pkg install` kısmında takılırsan söyle.

PC yok diye Jarvis'ten vazgeçmek yok.
