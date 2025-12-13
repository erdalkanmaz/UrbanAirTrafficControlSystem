# Urban Air Traffic Control System - Çalıştırma Notları

**Proje:** UrbanAirTrafficControlSystem  
**Versiyon:** 1.0-SNAPSHOT

---

## 🚀 Uygulamayı Çalıştırma

### Yöntem 1: Maven JavaFX Plugin (Önerilen)

Terminal'de veya IntelliJ IDEA'nın Maven tool window'undan:

```bash
mvn javafx:run
```

Bu komut JavaFX modüllerini otomatik olarak yapılandırır ve uygulamayı çalıştırır.

---

### Yöntem 2: IntelliJ IDEA Run Configuration

1. **Run Configuration Oluştur:**
   - `AirTrafficMainWindow.java` dosyasında `main` metoduna sağ tıklayın
   - "Modify Run Configuration..." seçeneğini tıklayın

2. **VM Options Ekle:**
   - "VM options" alanına şunu ekleyin:
   ```
   --module-path "${PATH_TO_JAVAFX}/lib" --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics
   ```
   
   **Not:** `${PATH_TO_JAVAFX}` yerine JavaFX SDK'nın yolunu yazın (ör: `C:\javafx-sdk-17.0.10`)

3. **Alternatif (Daha Kolay):**
   - IntelliJ IDEA'nın Maven tool window'unu açın
   - `javafx:run` goal'ını çalıştırın

---

### Yöntem 3: JavaFX SDK İndirme (Gerekirse)

Eğer JavaFX SDK yüklü değilse:

1. [OpenJFX Downloads](https://openjfx.io/) sayfasından JavaFX 17.0.10 SDK'yı indirin
2. Bir klasöre çıkarın (ör: `C:\javafx-sdk-17.0.10`)
3. Yöntem 2'deki VM options'da bu yolu kullanın

---

## 🧪 Testleri Çalıştırma

### Yöntem 1: Maven (Backend Testleri)
```bash
# Tüm backend testleri (UI testleri hariç)
mvn test

# Belirli test sınıfı
mvn test -Dtest=PositionTest
```

**Not:** 
- Maven ile çalıştırıldığında UI testleri (`**/ui/**/*Test.java`) otomatik olarak exclude edilir
- UI testleri için IntelliJ IDEA kullanılmalı (Yöntem 2)

---

### Yöntem 2: IntelliJ IDEA (UI Testleri için)

#### UI Testleri (AirTrafficMainWindowTest):
1. Test sınıfına sağ tıklayın (`AirTrafficMainWindowTest.java`)
2. "Run 'AirTrafficMainWindowTest'" seçin
3. İlk çalıştırmada hata alırsanız:
   - "Run" → "Edit Configurations..."
   - Test yapılandırmasını bulun
   - "VM options" alanına şunu ekleyin:
   ```
   --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics
   --add-opens javafx.base/javafx.util=ALL-UNNAMED
   --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED
   --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
   ```

#### Backend Testleri:
- IntelliJ IDEA'dan direkt çalıştırılabilir (JavaFX modül yapılandırması gerekmez)
- Veya Maven tool window'dan `test` goal'ını çalıştırın

---

## ⚠️ Yaygın Hatalar

### Hata: "JavaFX-Runtime-Komponenten fehlen"
**Çözüm:** Maven JavaFX plugin kullanın: `mvn javafx:run`

### Hata: "Module not found"
**Çözüm:** VM options'a modül path ekleyin (Yöntem 2)

---

**Not:** En kolay yöntem Maven JavaFX plugin kullanmaktır (`mvn javafx:run`).

