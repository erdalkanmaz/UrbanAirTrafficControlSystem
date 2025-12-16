# Urban Air Traffic Control System - Sistem Kriterleri ve Çalışma Standartları

**Proje:** UrbanAirTrafficControlSystem  
**Versiyon:** 1.0-SNAPSHOT  
**Son Güncelleme:** 2025-12-16  
**Durum:** Geliştirme aşamasında (Sprint 4 Faz 2 Temel Yapı tamamlandı)

---

## 📋 Dokümantasyon Amacı

Bu dokümantasyon, Urban Air Traffic Control System'in tüm işlemler, fonksiyonlar ve sistem bileşenleri için çalışma kriterlerini tanımlar. Bu kriterler:

- **Havacılık kurumlarına izin başvurusu** için referans olarak kullanılacaktır
- **Kullanım kılavuzu** için temel oluşturacaktır
- **Sistem doğrulama ve test** için kriterler sağlayacaktır
- **Operatör eğitimi** için standartlar belirleyecektir

**Not:** Bu dokümantasyon, sistem geliştikçe güncellenecek ve genişletilecektir.

---

## 📊 İçindekiler

1. [Sistem Durumu Paneli Kriterleri](#sistem-durumu-paneli-kriterleri)
2. [Trafik Yönetimi Kriterleri](#trafik-yönetimi-kriterleri)
3. [Uçuş İzni Yönetimi Kriterleri](#uçuş-izni-yönetimi-kriterleri)
4. [Harita ve Güvenlik Kriterleri](#harita-ve-güvenlik-kriterleri)
5. [Trafik Kuralları Kriterleri](#trafik-kuralları-kriterleri)
6. [Performans Kriterleri](#performans-kriterleri)
7. [Havacılık Standartları Uyumluluğu](#havacılık-standartları-uyumluluğu)

---

## 🖥️ Sistem Durumu Paneli Kriterleri

### 1. Operational Status (Operasyonel Durum)

**Kriter:**
- Sistemin genel operasyonel durumunu gösterir
- "Yes": Sistem çalışıyor ve trafik yönetimi yapabilir
- "No": Sistem çalışmıyor veya kritik bir sorun var

**Kontrol Mekanizması:**
```java
isOperational = TrafficControlCenter.isOperational() && cityMap != null
```

**Renk Kodlaması:**
- 🟢 **Yeşil (Yes):** Sistem normal çalışıyor
- 🔴 **Kırmızı (No):** Sistem çalışmıyor, müdahale gerekli

**Ne Zaman "No" Olur?**
- CityMap yüklenmemişse
- Sistem manuel olarak kapatılmışsa (`isOperational = false`)
- Kritik bir hata oluşmuşsa

**Havacılık Standartları:**
- ICAO Annex 11: Sistem sürekli operasyonel olmalı (%99.9 uptime hedefi)
- EASA U-Space: Sistem durumu sürekli izlenmeli ve raporlanmalı

---

### 2. Active Vehicles (Aktif Araçlar)

**Kriter:**
- Şu anda trafikte aktif olan toplam araç sayısını gösterir
- Bu araçlar uçuş izni almış ve trafiğe kaydedilmiş araçlardır

**Hesaplama:**
```java
activeVehicles.size()
```

**Durum Kategorileri:**
- **In Flight:** `VehicleStatus.IN_FLIGHT` - Uçuşta olan araçlar
- **Landing:** `VehicleStatus.LANDING` - İniş yapan araçlar
- **Emergency:** `VehicleStatus.EMERGENCY` - Acil durumdaki araçlar
- **Taking Off:** `VehicleStatus.TAKING_OFF` - Kalkış yapan araçlar
- **Preparing:** `VehicleStatus.PREPARING` - Hazırlanan araçlar

**İdeal Değer:**
- Normal: 0 - 10,000 araç
- Yüksek: 10,000 - 100,000 araç
- Kritik: 100,000+ araç (performans sorunları olabilir)

**Havacılık Standartları:**
- ICAO Annex 2: Maksimum trafik kapasitesi tanımlanmalı
- EASA U-Space: Trafik yoğunluğu sürekli izlenmeli

---

### 3. Pending Authorizations (Bekleyen İzinler)

**Kriter:**
- Uçuş izni talep eden ancak henüz onaylanmamış/reddedilmemiş araç sayısı

**Hesaplama:**
```java
authorizations.values().stream()
    .filter(auth -> auth.getStatus() == AuthorizationStatus.PENDING)
    .count()
```

**Durum:**
- **PENDING:** İzin bekliyor, henüz değerlendirilmemiş

**Renk Kodlaması:**
- 🟠 **Turuncu:** Bekleyen izinler var, dikkat gerekli

**İşlem Süreci:**
1. Araç uçuş izni talep eder (`requestFlightAuthorization`)
2. Sistem güvenlik ve trafik yoğunluğu kontrolü yapar
3. İzin onaylanır veya reddedilir
4. Durum `PENDING` → `APPROVED` veya `REJECTED` olur

**Havacılık Standartları:**
- ICAO Annex 11: İzin talepleri < 5 saniye içinde değerlendirilmeli
- EASA U-Space: Otomatik izin onayı/reddi mekanizması olmalı

---

### 4. Approved (Not Registered) (Onaylanmış Ama Kayıtlı Olmayan)

**Kriter:**
- Uçuş izni onaylanmış ancak henüz trafiğe kaydedilmemiş araç sayısı

**Hesaplama:**
```java
authorizations.values().stream()
    .filter(auth -> auth.getStatus() == AuthorizationStatus.APPROVED && auth.isValid())
    .filter(auth -> !activeVehicles.containsKey(auth.getVehicleId()))
    .count()
```

**Durum:**
- **APPROVED:** İzin onaylandı, geçerli
- **Not Registered:** Araç henüz `registerVehicle()` ile trafiğe kaydedilmemiş

**Renk Kodlaması:**
- 🔵 **Mavi:** İzin onaylandı, trafiğe giriş bekleniyor

**İşlem Süreci:**
1. İzin onaylanır (`APPROVED`)
2. Araç hazırlanır (`PREPARING`)
3. Araç trafiğe kaydedilir (`registerVehicle`)
4. Araç durumu `TAKING_OFF` → `IN_FLIGHT` olur

**Havacılık Standartları:**
- ICAO Annex 2: Onaylanmış izinler belirli bir süre içinde kullanılmalı
- EASA U-Space: İzin geçerlilik süresi tanımlanmalı (varsayılan: 2 saat)

---

### 5. In Flight (Uçuşta)

**Kriter:**
- Şu anda uçuşta olan araç sayısı

**Hesaplama:**
```java
activeVehicles.values().stream()
    .filter(v -> v.getStatus() == VehicleStatus.IN_FLIGHT)
    .count()
```

**Durum:**
- **IN_FLIGHT:** Araç normal uçuşta, rota takibinde

**Renk Kodlaması:**
- 🟢 **Yeşil:** Normal uçuş durumu

**Havacılık Standartları:**
- ICAO Annex 2: Uçuşta araçlar sürekli izlenmeli
- EASA U-Space: Konum güncellemeleri < 1 saniye aralıklarla alınmalı

---

### 6. Landing (İniş)

**Kriter:**
- İniş yapan araç sayısı

**Hesaplama:**
```java
activeVehicles.values().stream()
    .filter(v -> v.getStatus() == VehicleStatus.LANDING)
    .count()
```

**Durum:**
- **LANDING:** Araç iniş yapıyor, iniş izni alınmış

**Renk Kodlaması:**
- 🟠 **Turuncu:** İniş süreci devam ediyor, dikkat gerekli

**İşlem Süreci:**
1. Araç hedefe yaklaşır
2. İniş izni talep edilir
3. Durum `IN_FLIGHT` → `LANDING` olur
4. İniş tamamlanır
5. Araç trafikten çıkarılır (`unregisterVehicle`)

**Havacılık Standartları:**
- ICAO Annex 2: İniş süreci güvenli şekilde yönetilmeli
- EASA U-Space: İniş izni otomatik veya manuel verilebilir

---

### 7. Emergency (Acil Durum)

**Kriter:**
- Acil durumdaki araç sayısı

**Hesaplama:**
```java
activeVehicles.values().stream()
    .filter(v -> v.getStatus() == VehicleStatus.EMERGENCY)
    .count()
```

**Durum:**
- **EMERGENCY:** Araç acil durumda, öncelikli müdahale gerekli

**Renk Kodlaması:**
- 🔴 **Kırmızı (Kalın):** Acil durum, acil müdahale gerekli

**İşlem Süreci:**
1. Acil durum tespit edilir (araç, sistem veya operatör tarafından)
2. Durum `EMERGENCY` olarak işaretlenir
3. Öncelikli trafik yönetimi devreye girer
4. Acil durum çözülene kadar sürekli izlenir

**Havacılık Standartları:**
- ICAO Annex 11: Acil durumlar < 100ms içinde tespit edilmeli
- FAA AC 90-48D: Acil durum öncelik sistemi olmalı
- EASA U-Space: Acil durum otomatik müdahale mekanizması olmalı

---

### 8. Base Stations (Baz İstasyonları)

**Kriter:**
- Sistemde kayıtlı baz istasyonu sayısı

**Hesaplama:**
```java
baseStations.size()
```

**Durum:**
- Baz istasyonları, araçlarla iletişim kurmak için kullanılır
- Kapsama alanını gösterir

**Ne Zaman Sorun Olur?**
- Base Stations: 0 → System Health "Warning" olur
- Araçlarla iletişim kurulamaz
- Konum güncellemeleri alınamaz

**İdeal Değer:**
- Minimum: 1 baz istasyonu (küçük alanlar için)
- Normal: 5-20 baz istasyonu (şehir için)
- Yüksek: 20+ baz istasyonu (metropol için)

**Havacılık Standartları:**
- ICAO Annex 11: Minimum 1 baz istasyonu gerekli (iletişim için)
- EASA U-Space: Kapsama alanı için yeterli sayıda baz istasyonu olmalı

---

### 9. Rule Engine (Kural Motoru)

**Kriter:**
- Trafik kuralı motorunun durumunu gösterir

**Kontrol:**
```java
ruleEngine != null
```

**Durumlar:**
- **Active:** Kural motoru çalışıyor, kurallar kontrol ediliyor
- **Inactive:** Kural motoru çalışmıyor, kurallar kontrol edilmiyor

**Renk Kodlaması:**
- 🟢 **Yeşil (Active):** Normal çalışıyor
- 🔴 **Kırmızı (Inactive):** Çalışmıyor, müdahale gerekli

**Ne Zaman "Inactive" Olur?**
- RuleEngine null ise
- Sistem hatası oluşmuşsa

**Havacılık Standartları:**
- ICAO Annex 2: Trafik kuralları sürekli kontrol edilmeli
- EASA U-Space: Kural motoru sürekli aktif olmalı

---

### 10. System Health (Sistem Sağlığı)

**Kriter:**
- Sistemin genel sağlık durumunu gösterir
- Tüm bileşenlerin durumuna göre hesaplanır

**Hesaplama:**
```java
if (!isOperational) return "Critical";
if (baseStationCount == 0) return "Warning";
return "Good";
```

**Durumlar:**

#### 🟢 Good (İyi)
**Koşullar:**
- Operational: Yes
- Base Stations: ≥ 1
- Rule Engine: Active

**Anlamı:**
- Sistem sağlıklı çalışıyor
- Tüm bileşenler normal
- Müdahale gerekmez

---

#### 🟠 Warning (Uyarı)
**Koşullar:**
- Operational: Yes
- Base Stations: 0
- Rule Engine: Active

**Anlamı:**
- Sistem çalışıyor ama bazı bileşenler eksik
- Baz istasyonu yok (araçlarla iletişim sorunu)
- Müdahale önerilir

**Ne Yapılmalı?**
- Baz istasyonu eklenmeli
- İletişim altyapısı kontrol edilmeli

---

#### 🔴 Critical (Kritik)
**Koşullar:**
- Operational: No
- VEYA Rule Engine: Inactive

**Anlamı:**
- Sistem kritik durumda
- Trafik yönetimi yapılamaz
- Acil müdahale gerekli

**Ne Yapılmalı?**
- Sistem durumu kontrol edilmeli
- Hata logları incelenmeli
- Gerekirse sistem yeniden başlatılmalı

**Havacılık Standartları:**
- ICAO Annex 11: Sistem sürekli "Good" durumunda olmalı
- EASA U-Space: "Warning" veya "Critical" durumlarında acil müdahale gerekir

---

## 🚁 Trafik Yönetimi Kriterleri

### 1. Araç Kayıt İşlemi

**Kriter:**
- Araç trafiğe kaydedilmeden önce geçerli uçuş izni olmalı
- İzin durumu `APPROVED` ve geçerli olmalı (`isValid() == true`)

**İşlem Akışı:**
```java
1. requestFlightAuthorization(vehicle, departure, destination)
2. Sistem güvenlik kontrolü yapar (canAuthorizeFlight)
3. İzin onaylanır (APPROVED) veya reddedilir (REJECTED)
4. registerVehicle(vehicle) - sadece APPROVED izinle
```

**Hata Durumları:**
- `IllegalStateException`: "Araç için geçerli uçuş izni bulunamadı"
- İzin `PENDING`, `REJECTED`, `EXPIRED` veya `CANCELLED` ise kayıt yapılamaz

**Havacılık Standartları:**
- ICAO Annex 11: Tüm araçlar geçerli izinle kayıt olmalı
- EASA U-Space: İzin kontrolü otomatik yapılmalı

---

### 2. Konum Güncelleme

**Kriter:**
- Araç konumu sürekli güncellenmeli
- Güncelleme sırasında trafik kuralları kontrol edilmeli

**İşlem Akışı:**
```java
1. updateVehiclePosition(vehicleId, newPosition)
2. Araç konumu güncellenir
3. Trafik kuralı kontrolü yapılır (ruleEngine.checkViolations)
4. İhlal varsa uyarı gönderilir (sendWarning)
5. Baz istasyonu bağlantıları güncellenir
```

**Performans Kriterleri:**
- Konum güncelleme süresi: < 5ms
- Kural kontrolü süresi: < 10ms
- Toplam işlem süresi: < 15ms

**Havacılık Standartları:**
- ICAO Annex 11: Konum güncellemeleri < 1 saniye aralıklarla alınmalı
- EASA U-Space: Gerçek zamanlı konum takibi zorunlu

---

### 3. Trafikten Çıkarma

**Kriter:**
- Araç trafikten çıkarıldığında tüm kayıtlar temizlenmeli

**İşlem Akışı:**
```java
1. unregisterVehicle(vehicleId)
2. Araç activeVehicles'dan çıkarılır
3. İzin kaydı authorizations'dan çıkarılır
4. Baz istasyonu bağlantıları güncellenir
```

**Havacılık Standartları:**
- ICAO Annex 11: Araç trafikten çıkarıldığında kayıtlar temizlenmeli
- EASA U-Space: Trafikten çıkış işlemi otomatik veya manuel yapılabilir

---

## ✈️ Uçuş İzni Yönetimi Kriterleri

### 1. İzin Talebi

**Kriter:**
- Tüm uçuşlar için izin talep edilmeli
- Kalkış ve varış noktaları belirtilmeli

**İşlem Akışı:**
```java
1. requestFlightAuthorization(vehicle, departure, destination)
2. İzin oluşturulur (PENDING durumunda)
3. Sistem güvenlik kontrolü yapar (canAuthorizeFlight)
4. İzin onaylanır (APPROVED) veya reddedilir (REJECTED)
```

**Güvenlik Kontrolleri:**
- Kalkış noktası güvenli mi? (`cityMap.isPositionSafe(departure)`)
- Varış noktası güvenli mi? (`cityMap.isPositionSafe(destination)`)
- Trafik yoğunluğu uygun mu? (`activeVehicles.size() < 100`)

**Havacılık Standartları:**
- ICAO Annex 11: İzin talepleri < 5 saniye içinde değerlendirilmeli
- EASA U-Space: Otomatik izin onayı/reddi mekanizması olmalı

---

### 2. İzin Durumları

**Durumlar:**
- **PENDING:** İzin bekliyor, henüz değerlendirilmemiş
- **APPROVED:** İzin onaylandı, geçerli
- **REJECTED:** İzin reddedildi (güvenlik veya trafik yoğunluğu nedeniyle)
- **EXPIRED:** İzin süresi doldu (varsayılan: 2 saat)
- **CANCELLED:** İzin iptal edildi

**Geçerlilik Kontrolü:**
```java
isValid() = (status == APPROVED) && (validUntil == null || now < validUntil)
```

**Havacılık Standartları:**
- ICAO Annex 2: İzin geçerlilik süresi tanımlanmalı
- EASA U-Space: İzin süresi dolmadan önce yenilenebilmeli

---

## 🗺️ Harita ve Güvenlik Kriterleri

### 1. Konum Güvenliği

**Kriter:**
- Tüm konumlar güvenlik kontrolünden geçmeli
- Yasak bölgeler ve engeller kontrol edilmeli

**Kontrol Mekanizması:**
```java
cityMap.isPositionSafe(position)
```

**Kontrol Kriterleri:**
- Konum şehir sınırları içinde mi?
- Konum yasak bölgede mi? (`restrictedZone.contains(position)`)
- Konum engel üzerinde mi? (`obstacle.contains(position)`)

**Havacılık Standartları:**
- ICAO Annex 2: Yasak bölgeler tanımlanmalı
- EASA U-Space: Geofencing zorunlu

---

### 2. Güvenli Geçiş Yüksekliği

**Kriter:**
- Belirli bir konumdan geçerken güvenli yükseklik hesaplanmalı

**Hesaplama:**
```java
cityMap.getSafePassageAltitude(position)
```

**Hesaplama Kriterleri:**
- Konumdaki maksimum engel yüksekliği bulunur
- Güvenlik payı eklenir (+10 metre)
- Minimum yükseklik: `maxObstacleHeight + 10.0`

**Havacılık Standartları:**
- ICAO Annex 2: Minimum güvenli yükseklik tanımlanmalı
- EASA U-Space: Engel yüksekliği + 10 metre güvenlik payı

---

## 🚦 Trafik Kuralları Kriterleri

### 1. Hız Limitleri

**Kriter:**
- Her bölge için maksimum hız limiti tanımlanmalı
- Hız limiti aşıldığında uyarı verilmeli

**Kontrol Mekanizması:**
```java
SpeedLimitRule.checkViolation(vehicle, position)
```

**Uyarı Eşikleri:**
- **Normal:** `velocity <= maxSpeed`
- **Uyarı:** `maxSpeed - tolerance < velocity <= maxSpeed`
- **İhlal:** `velocity > maxSpeed`

**Havacılık Standartları:**
- ICAO Annex 2: Hız limitleri bölge bazında tanımlanmalı
- EASA U-Space: Hız limiti ihlalleri otomatik tespit edilmeli

---

### 2. Giriş/Çıkış Kuralları

**Kriter:**
- Belirli bölgelere giriş/çıkış kuralları tanımlanmalı
- İzin olmadan giriş yapılamaz

**Kontrol Mekanizması:**
```java
EntryExitRule.checkViolation(vehicle, position)
```

**Kontrol Kriterleri:**
- Bölgeye giriş izni var mı?
- Bölgeden çıkış izni var mı?
- İzin olmadan giriş/çıkış yapılırsa ihlal tespit edilir

**Havacılık Standartları:**
- ICAO Annex 2: Yasak bölgelere giriş yasak
- EASA U-Space: Geofencing ile otomatik kontrol

---

## ⚡ Performans Kriterleri

### 1. Sistem Yanıt Süreleri

**Kriter:**
- Tüm işlemler belirli süreler içinde tamamlanmalı

**Hedef Süreler:**
- Araç kayıt süresi: < 10ms
- Konum güncelleme süresi: < 5ms
- Çarpışma tespiti: < 50ms (kritik), < 100ms (normal)
- Kural kontrolü: < 10ms
- UI güncelleme: 100ms (10 FPS) - Havacılık standartlarına uygun

**Havacılık Standartları:**
- ICAO Annex 11: Sistem yanıt süreleri tanımlanmalı, konum güncellemeleri < 1 saniye aralıklarla
- EASA U-Space: Gerçek zamanlı işleme zorunlu

---

### 2. Kapasite Kriterleri

**Kriter:**
- Sistem belirli sayıda aracı aynı anda yönetebilmeli

**Hedef Kapasiteler:**
- Normal: 0 - 10,000 araç
- Yüksek: 10,000 - 100,000 araç
- Kritik: 100,000+ araç (performans sorunları olabilir)

**Havacılık Standartları:**
- ICAO Annex 2: Maksimum trafik kapasitesi tanımlanmalı
- EASA U-Space: Sistem ölçeklenebilir olmalı

---

### 3. Spatial Indexing (Quadtree) Kriterleri

**Kriter:**
- Mekansal sorgular (bölge içindeki araçları bulma) için Quadtree kullanılmalı
- Performans: O(n) → O(log n) iyileştirmesi

**Hedef Performans:**
- 1000 araç için bölge sorgusu: < 200ms
- 10,000 araç için bölge sorgusu: < 500ms
- Insert/Update/Remove işlemleri: < 5ms

**Kullanım Senaryoları:**
- `getVehiclesInArea(center, radius)` - Bölge içindeki araçları bulma
- Çarpışma tespiti için yakın araçları bulma
- Trafik yoğunluğu analizi

**Havacılık Standartları:**
- ICAO Annex 11: Mekansal sorgular hızlı olmalı (< 1 saniye)
- EASA U-Space: Gerçek zamanlı trafik analizi için gerekli

---

### 4. Asenkron İşleme Kriterleri

**Kriter:**
- Araç konum güncellemeleri asenkron olarak işlenmeli
- Ana thread bloke edilmemeli

**Hedef Performans:**
- Asenkron güncelleme başlatma: < 1ms
- Thread pool yönetimi: Dinamik boyutlandırma
- Hata yönetimi: Exception handling ve logging

**Kullanım Senaryoları:**
- `updateVehiclePositionAsync(vehicleId, position)` - Asenkron konum güncelleme
- `processBatchUpdates(updates)` - Toplu güncelleme işleme

**Havacılık Standartları:**
- ICAO Annex 11: Sistem yanıt vermeye devam etmeli (non-blocking)
- EASA U-Space: Yüksek trafik durumlarında sistem stabil kalmalı

---

### 5. Batch Processing Kriterleri

**Kriter:**
- Çoklu araç güncellemeleri toplu olarak işlenmeli
- Paralel işleme kullanılmalı

**Hedef Performans:**
- 100 araç toplu güncelleme: < 100ms
- 1000 araç toplu güncelleme: < 500ms
- Paralel işleme: CPU core sayısına göre optimize

**Kullanım Senaryoları:**
- Toplu konum güncellemeleri
- Periyodik sistem senkronizasyonu
- Yedekleme ve geri yükleme işlemleri

**Havacılık Standartları:**
- ICAO Annex 11: Toplu işlemler verimli olmalı
- EASA U-Space: Sistem yüksek trafik durumlarında stabil kalmalı

---

### 6. Gerçek Zamanlı UI Güncelleme Kriterleri

**Kriter:**
- UI bileşenleri (Harita, Araç Listesi, Sistem Durumu) periyodik olarak güncellenmeli
- JavaFX Application Thread'de güvenli güncelleme yapılmalı

**Hedef Performans:**
- Güncelleme aralığı: 100ms (10 FPS) - Havacılık standartlarına uygun
- UI thread blocking: < 10ms
- Güncelleme servisi: Başlatma/durdurma kontrollü

**Güncellenen Bileşenler:**
- `MapVisualization` - Harita ve araç görselleştirmesi
- `VehicleListView` - Araç listesi tablosu
- `SystemStatusPanel` - Sistem durumu paneli

**Havacılık Standartları:**
- ICAO Annex 11: Konum güncellemeleri < 1 saniye aralıklarla görüntülenmeli
- EASA U-Space: Gerçek zamanlı görselleştirme zorunlu

---

### 7. Harita Üzerinde Araç Görselleştirme Kriterleri

**Kriter:**
- Aktif araçlar harita üzerinde görselleştirilmeli
- Araç tipine göre renk kodlaması yapılmalı
- Yön göstergesi (heading arrow) gösterilmeli

**Renk Kodlaması:**
- 🟢 **Yeşil:** Passenger (Yolcu) araçları
- 🔵 **Mavi:** Cargo (Kargo) araçları
- 🔴 **Kırmızı:** Emergency (Acil durum) araçları
- ⚪ **Gri:** Diğer araç tipleri

**Görselleştirme Özellikleri:**
- Araç konumu: Daire şeklinde gösterilir
- Acil durum araçları: Daha büyük yarıçap (7px vs 5px)
- Yön göstergesi: IN_FLIGHT durumundaki araçlar için ok işareti
- Görünürlük: Canvas dışındaki araçlar çizilmez (performans optimizasyonu)

**Havacılık Standartları:**
- ICAO Annex 11: Araç konumları görselleştirilmeli
- EASA U-Space: Gerçek zamanlı trafik görselleştirmesi zorunlu

---

### 8. Çarpışma Önleme Sistemi Kriterleri

**Kriter:**
- Sistem, tüm aktif araçlar için çarpışma riskini sürekli değerlendirmelidir
- Kritik çarpışma riski tespit edildiğinde < 50ms içinde müdahale edilmelidir
- Çarpışma önleme algoritması ICAO Annex 2'ye uygun olmalıdır

**Minimum Güvenli Mesafeler:**
- Yatay mesafe: 50 metre (ICAO Annex 2)
- Dikey mesafe: 10 metre (ICAO Annex 2)
- Çarpışma kontrol yarıçapı: 500 metre

**Risk Seviyeleri:**
- **LOW:** Risk skoru < 0.3 - Normal operasyon, izleme yeterli
- **MEDIUM:** Risk skoru 0.3-0.5 - Dikkat gerekli, mesafe artırılmalı
- **HIGH:** Risk skoru 0.5-0.8 - Yüksek risk, acil rota düzeltmesi gerekli
- **CRITICAL:** Risk skoru >= 0.8 - Kritik risk, acil müdahale gerekli

**Risk Skoru Hesaplama:**
- Mesafe faktörü: %40 ağırlık
- Minimum mesafe ihlali: %30 ağırlık (yatay), %20 ağırlık (dikey)
- Hız faktörü: %10 ağırlık
- Gelecek çarpışma riski: %30 ağırlık

**Tahmin Özellikleri:**
- Gelecek konum projeksiyonu: 30 saniye zaman ufku
- Tahmini çarpışma süresi hesaplama
- Yaklaşma hızı analizi

**Kullanım Senaryoları:**
- `checkCollisionRisks(vehicle, allVehicles, quadtree)` - Araç için çarpışma risklerini kontrol et
- `calculateCollisionRisk(v1, v2)` - İki araç arasındaki riski hesapla
- `checkMinimumSeparation(v1, v2)` - Minimum mesafe kontrolü
- `predictFuturePosition(vehicle, timeSeconds)` - Gelecek konum tahmini

**Havacılık Standartları:**
- ICAO Annex 2: Rules of the Air - Minimum separation standards
- ICAO Annex 11: Air Traffic Services - Collision avoidance requirements
- EASA U-Space: Real-time collision detection mandatory
- FAA AC 90-48D: Pilots' Role in Collision Avoidance

---

### 9. ICAO Standartları Uyumluluğu Kriterleri

**Kriter:**
- Sistem, ICAO Annex 2 standartlarına uygun olmalıdır
- Minimum separation standartları kontrol edilmelidir
- Uçuş kuralları (VFR/IFR) uyumluluğu kontrol edilmelidir
- İletişim gereksinimleri doğrulanmalıdır

**Separation Standartları:**
- Yatay minimum mesafe: 50 metre
- Dikey minimum mesafe: 10 metre
- Tüm aktif araç çiftleri için kontrol

**Uçuş Kuralları Kontrolü:**
- VFR (Visual Flight Rules) temel kontrolleri
- Yükseklik kontrolü (negatif yükseklik ihlali)
- Hız kontrolü (negatif hız ihlali)
- Durum kontrolü (IN_FLIGHT, TAKING_OFF)

**İletişim Gereksinimleri:**
- Minimum iletişim menzili: 5 km
- Baz istasyonu kapsama kontrolü
- Araç-baz istasyonu mesafe kontrolü

**Kullanım Senaryoları:**
- `checkSeparationStandards(v1, v2)` - İki araç arası separation kontrolü
- `checkFlightRulesCompliance(vehicle)` - Uçuş kuralları uyumluluğu
- `validateCommunicationRequirements(vehicle, baseStations)` - İletişim kontrolü
- `checkAllSeparationStandards(vehicles)` - Tüm araçlar için toplu kontrol

**Havacılık Standartları:**
- ICAO Annex 2: Rules of the Air
- ICAO Annex 11: Air Traffic Services
- EASA U-Space Regulation (EU) 2021/664

### 10. Dinamik Yükseklik Katmanları Kriterleri (Sprint 4 - Faz 1)

**Kriter:**
- Hava sahası dikey katmanlara bölünmelidir
- Her katman için minimum/maksimum yükseklik tanımlanmalıdır
- Araçların mevcut katmanları hesaplanabilmelidir
- Çarpışma kontrolünde katman bilgisi kullanılmalıdır
- ICAO standartları kontrolünde katman bilgisi dikkate alınmalıdır

**Katman Tanımları:**
- **LAYER_1_LOW:** 0-60m - Teslimat dronları, alçak irtifa trafiği
  - Önerilen hız limiti: 15 m/s
- **LAYER_2_MEDIUM:** 60-120m - Şehir içi yolcu dronları, normal trafik
  - Önerilen hız limiti: 25 m/s
- **LAYER_3_HIGH:** 120-180m - Acil durum araçları, öncelikli trafik
  - Önerilen hız limiti: 35 m/s

**Katman Hesaplama:**
- `AltitudeLayer.fromAltitude(double altitude)` - Yükseklikten katman hesaplama
- `CityMap.getLayerForAltitude(Position position)` - Konum için katman hesaplama (engeller ve yasak bölgeler dikkate alınarak)
- `Vehicle.getCurrentLayer(CityMap cityMap)` - Aracın mevcut katmanını hesaplama

**Güvenlik Kontrolleri:**
- Engeller (binalar, hastaneler) katman hesaplamasında göz önünde bulundurulmalıdır
- Yasak bölgeler katman hesaplamasında göz önünde bulundurulmalıdır
- Güvenli olmayan konumlar için null döndürülmelidir

**Çarpışma Kontrolü Entegrasyonu:**
- Farklı katmanlardaki araçlar için risk skoru azaltılmalıdır:
  - 100m+ dikey mesafe: Risk yok sayılmalı
  - 60-100m dikey mesafe: Risk skoru %70 azaltılmalı
  - 60m altı dikey mesafe: Risk skoru %50 azaltılmalı
- `CollisionDetectionService.calculateCollisionRisk(v1, v2, cityMap)` - Katman bilgisi ile çarpışma riski hesaplama

**ICAO Standartları Entegrasyonu:**
- Farklı katmanlardaki araçlar için 60m minimum dikey mesafe kabul edilmelidir
- `ICAOStandardsCompliance.checkSeparationStandards(v1, v2, cityMap)` - Katman bilgisi ile separation kontrolü
- `ICAOStandardsCompliance.checkFlightRulesCompliance(vehicle, cityMap)` - Katman ve yasak bölge kontrolü

**Kullanım Senaryoları:**
- `AltitudeLayer.fromAltitude(altitude)` - Yükseklikten katman belirleme
- `cityMap.getLayerForAltitude(position)` - Konum için katman belirleme
- `vehicle.getCurrentLayer(cityMap)` - Aracın katmanını belirleme
- `collisionService.calculateCollisionRisk(v1, v2, cityMap)` - Katman dikkate alınarak çarpışma riski hesaplama
- `icaoCompliance.checkSeparationStandards(v1, v2, cityMap)` - Katman dikkate alınarak separation kontrolü

---

## 🛫 Havacılık Standartları Uyumluluğu

### İlgili Standartlar

- **ICAO (International Civil Aviation Organization):** Uluslararası sivil havacılık standartları
- **FAA (Federal Aviation Administration):** ABD havacılık standartları
- **EASA (European Union Aviation Safety Agency):** Avrupa havacılık standartları
- **SHGM (Sivil Havacılık Genel Müdürlüğü):** Türkiye sivil havacılık otoritesi
- **ASTM UTM (Unmanned Traffic Management):** Dron trafik yönetimi standartları

### Detaylı Standart Dokümantasyonu

Detaylı havacılık standartları dokümantasyonu için: [HAVACILIK_STANDARTLARI_DOKUMANTASYONU.md](HAVACILIK_STANDARTLARI_DOKUMANTASYONU.md)

---

## 📝 Dokümantasyon Güncelleme Süreci

Bu dokümantasyon, sistem geliştikçe güncellenecektir:

1. **Yeni işlem/fonksiyon eklendiğinde:** İlgili kriterler buraya eklenecek
2. **Havacılık standartları değiştiğinde:** İlgili bölümler güncellenecek
3. **Test sonuçlarına göre:** Performans kriterleri revize edilecek
4. **Kullanıcı geri bildirimlerine göre:** Kullanım kriterleri iyileştirilecek

**Son Güncelleme:** 2025-12-16 (Sprint 4 Faz 2 Temel Yapı tamamlandı)  
**Sonraki Güncelleme:** Sprint 4 Faz 2 detayları (tartışma sonrası) - Ana yollar/tali yollar katman organizasyonu, geçiş yönetimi, kesişme yönetimi kriterleri eklenecek

---

## 📚 İlgili Dokümantasyon

- [HAVACILIK_STANDARTLARI_DOKUMANTASYONU.md](HAVACILIK_STANDARTLARI_DOKUMANTASYONU.md) - Detaylı havacılık standartları
- [MIMARI_VE_PERFORMANS_ANALIZI.md](MIMARI_VE_PERFORMANS_ANALIZI.md) - Mimari ve performans analizi
- [PROJE_CONTEXT.md](PROJE_CONTEXT.md) - Proje genel bağlamı
- [REQUIREMENTS.md](REQUIREMENTS.md) - İhtiyaçlar listesi
- [SPRINT_PLAN.md](SPRINT_PLAN.md) - Sprint planları

---

**Not:** Bu dokümantasyon, sistem geliştikçe genişletilecek ve güncellenecektir. Tüm işlemler ve fonksiyonlar için kriterler buraya eklenecektir.

