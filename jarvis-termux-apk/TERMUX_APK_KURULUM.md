# Termux'ta APK Oluştur + Gri Daire Dalga Jarvis

## 1. Termux'ta APK (2 yol)

### YOL A: En kolay - AndroidIDE (ÖNERİLEN)
Termux'ta SDK ile uğraşma, direkt Android için yapılmış IDE'yi kullan:

1. Play Store'dan `AndroidIDE` indir (by itsaky, ücretsiz)
2. Bu `jarvis-termux-apk` klasörünü indir:
   Termux'ta:
   ```
   cd ~
   wget https://8000-ifqrketw1hgu5po9jun7z.e2b.app/jarvis-termux-apk.zip
   unzip -o jarvis-termux-apk.zip
   ```
   (Link çalışmazsa: `cp -r /sdcard/Download/jarvis-termux-apk ~/` )

3. AndroidIDE'yi aç > Open Project > `jarvis-termux-apk` seç
4. Sağ üst `▶ Run` veya `Build > Assemble Debug` de
5. APK şurada: `app/build/outputs/apk/debug/app-debug.apk`
6. Kur, Shizuku izni ver

**Avantaj:** SDK, NDK, her şey içinde, Termux'taki gibi x86/aarch64 sorunu yok, 2 dkda build.

### YOL B: Termux'ta direkt Gradle (ileri seviye)
```bash
pkg update -y
pkg install -y openjdk-17 git wget unzip
# Android SDK Termux için (aarch64 uyumlu community build)
pkg install -y sdkmanager  # varsa
# yoksa manuel:
mkdir -p ~/android-sdk/cmdline-tools
cd ~/android-sdk/cmdline-tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip -q commandlinetools-linux-*.zip
mkdir -p latest && mv cmdline-tools/* latest/ 2>/dev/null; true
export ANDROID_HOME=$HOME/android-sdk
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

cd ~/jarvis-termux-apk
chmod +x gradlew
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## 2. Yeni Özellikler
- **Gri Daire Dalga:** `CircularWaveView.kt` - 3 katmanlı gri halka, pulse animasyon
  - Sen konuşurken: DURUR (soluk)
  - Jarvis konuşurken: DALGALANIR (gri tonlar, volume ile titrer)
- **Sesli Konuşma:** STT (SpeechRecognizer) + TTS (TextToSpeech) tam entegre
  - Basılı tut konuş, bırak dinlesin
  - Yazarak da konuşabilirsin
  - TR dil desteği

## 3. Shizuku ile sınırsız
Aynı kod Termux'taki gibi `rish -c "am start ..."` ile her şeyi yapar.
Şimdilik demo cevap veriyor, Gemini key eklersen tam Jarvis olur:
`app/src/main/java/com/jarvis/MainActivity.kt` içinde `onUser()` kısmına Gemini çağrısı ekle.

