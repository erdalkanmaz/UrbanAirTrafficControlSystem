# IntelliJ IDEA - UI Test Yapılandırması

**Proje:** UrbanAirTrafficControlSystem  
**Versiyon:** 1.0-SNAPSHOT

---

## 🧪 UI Testleri Çalıştırma (AirTrafficMainWindowTest)

### Sorun
UI testleri çalıştırılırken `Module javafx.controls not found` hatası alınıyor.

Bu hata, JavaFX modüllerinin modül path'te olmamasından kaynaklanır. JavaFX, Java 9+ modül sistemi gerektirir ve modüllerin modül path'te olması gerekir.

### Çözüm: Modül Path Yapılandırması (Zorunlu)

JavaFX modüllerini modül path'e eklemek için aşağıdaki adımları izleyin:

#### Adım 1: JavaFX SDK İndirme

**ÖNEMLİ:** Java 17 kullanıyorsanız, JavaFX 17.x SDK kullanmalısınız. JavaFX 25.x SDK, Java 21+ gerektirir!

1. [OpenJFX Downloads](https://openjfx.io/) sayfasına gidin
2. **JavaFX 17.0.17 SDK**'yı indirin (Windows için, Java 17 ile uyumlu)
3. İndirilen ZIP dosyasını bir klasöre çıkarın (ör: `C:\javafx-sdk-17.0.17`)
4. `lib` klasörünün varlığını kontrol edin (ör: `C:\javafx-sdk-17.0.17\lib`)

**Not:** Eğer JavaFX 17.0.17 bulamazsanız, 17.x serisinden herhangi bir sürüm kullanabilirsiniz (17.0.17, 17.0.18, vb.)

#### Adım 2: Test Yapılandırması Oluştur

1. `AirTrafficMainWindowTest.java` dosyasına sağ tıklayın
2. "Run 'AirTrafficMainWindowTest'" seçin
3. İlk çalıştırmada hata alırsanız devam edin (bu normal)

#### Adım 3: VM Options ve Module Path Ekle

1. "Run" menüsünden "Edit Configurations..." seçin
2. Sol panelde "AirTrafficMainWindowTest" yapılandırmasını bulun
3. **"Use module path" seçeneğinin işaretli olduğundan emin olun** (genellikle "Use classpath of module" altında)
4. "VM options" alanına şunu ekleyin:

```
--module-path "C:\javafx-sdk-17.0.17\lib" --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics --add-opens javafx.base/javafx.util=ALL-UNNAMED --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
```

**ÖNEMLİ:** 
- `C:\javafx-sdk-17.0.17\lib` yerine kendi JavaFX SDK yolunuzu yazın!
- **Yazım hatası:** `--modul-path` değil, `--module-path` (iki "e" harfi) olmalı!
- **Tırnak işareti:** Modül path'i tırnak içine alın: `"C:\javafx-sdk-17.0.17\lib"`
- **Versiyon uyumu:** Java 17 kullanıyorsanız, JavaFX 17.x SDK kullanmalısınız. JavaFX 25.x SDK, Java 21+ gerektirir ve "Unsupported major.minor version" hatası verir!

5. "Apply" ve "OK" butonlarına tıklayın

#### Adım 4: Testi Çalıştır

- "Run" butonuna tıklayın veya `Shift+F10` tuşuna basın
- Tüm 10 test geçmeli

#### Adım 3: Testi Çalıştır
- "Run" butonuna tıklayın veya `Shift+F10` tuşuna basın

---

## ✅ Beklenen Sonuç

Testler başarıyla çalışmalı:
- 10 test metodu
- Tüm testler geçmeli

---

## ✅ VM Options Kontrol Listesi

VM options ekledikten sonra hala hata alıyorsanız, şunları kontrol edin:

1. **VM Options Doğru Yerde mi?**
   - "Run" → "Edit Configurations..."
   - Sol panelde **"AirTrafficMainWindowTest"** yapılandırmasını seçin
   - **"VM options"** alanına (üst kısımda, "Working directory" altında) ekleyin
   - **"Environment variables"** değil, **"VM options"** olmalı

2. **VM Options Tam Metni:**
   ```
   --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics
   --add-opens javafx.base/javafx.util=ALL-UNNAMED
   --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED
   --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
   ```

3. **Yapılandırmayı Kaydet:**
   - "Apply" butonuna tıklayın
   - "OK" butonuna tıklayın

4. **Testi Yeniden Çalıştır:**
   - Test sınıfına sağ tıklayın
   - "Run 'AirTrafficMainWindowTest'" seçin

---

## 🔄 Alternatif: Maven Bağımlılıklarını Modül Path'e Ekle

Eğer JavaFX SDK indirmek istemiyorsanız, Maven bağımlılıklarını modül path'e ekleyebilirsiniz:

1. Maven bağımlılıklarının tam yolunu bulun (genellikle `C:\Users\<USERNAME>\.m2\repository\org\openjfx\...`)
2. VM options'a şunu ekleyin (yolları kendi sisteminize göre güncelleyin):

```
--module-path "C:\Users\ErdalKanmaz\.m2\repository\org\openjfx\javafx-controls\17.0.10;C:\Users\ErdalKanmaz\.m2\repository\org\openjfx\javafx-fxml\17.0.10;C:\Users\ErdalKanmaz\.m2\repository\org\openjfx\javafx-swing\17.0.10;C:\Users\ErdalKanmaz\.m2\repository\org\openjfx\javafx-graphics\17.0.10;C:\Users\ErdalKanmaz\.m2\repository\org\openjfx\javafx-base\17.0.10" --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics --add-opens javafx.base/javafx.util=ALL-UNNAMED --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
```

**Not:** Bu yöntem daha karmaşıktır ve JavaFX SDK kullanmak daha kolaydır.

---

## 📝 Hata Mesajları ve Çözümleri

### Hata: "Unrecognized option: --modul-path"
**Çözüm:** Yazım hatası! `--modul-path` değil, `--module-path` (iki "e" harfi) olmalı.

### Hata: "Module javafx.controls not found"
**Çözüm:** VM options'a `--module-path` ve `--add-modules` parametrelerini ekleyin (yukarıdaki tam liste)

### Hata: "JavaFX toolkit not initialized"
**Çözüm:** VM options'a `--add-opens` parametrelerini ekleyin (yukarıdaki tam liste)

### Hata: "IllegalStateException: Toolkit not initialized"
**Çözüm:** VM options'ı kontrol edin ve JavaFX SDK versiyonunun projeyle uyumlu olduğundan emin olun (JavaFX 17.0.10 önerilir)

### Hata: "Could not create the Java Virtual Machine"
**Çözüm:** VM options'ta yazım hatası olabilir. `--module-path` (iki "e") olduğundan emin olun.

### Hata: "Unsupported major.minor version 67.0" veya "Error reading module"
**Çözüm:** JavaFX SDK versiyonu Java sürümünüzle uyumsuz! 
- Java 17 kullanıyorsanız → JavaFX 17.x SDK kullanın (17.0.17, 17.0.18, vb.)
- JavaFX 25.x SDK, Java 21+ gerektirir ve Java 17 ile çalışmaz!
- JavaFX SDK'yı [OpenJFX Downloads](https://openjfx.io/) sayfasından doğru versiyonu indirin.

---

**Not:** Bu yapılandırma sadece UI testleri için gereklidir. Backend testleri (284 test) Maven ile sorunsuz çalışır.

