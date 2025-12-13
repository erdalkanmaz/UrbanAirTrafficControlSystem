# Urban Air Traffic Control System - İhtiyaçlar Listesi

**Proje:** UrbanAirTrafficControlSystem  
**Versiyon:** 1.0-SNAPSHOT  
**Son Güncelleme:** 2025-12-11

---

## 📋 İçindekiler

1. [Epic'ler](#epicler)
2. [User Stories](#user-stories)
3. [Teknik Gereksinimler](#teknik-gereksinimler)
4. [Havacılık Standartları](#havacılık-standartları)

---

## 🎯 Epic'ler

### Epic 1: Temel Altyapı ve Model Yapısı ✅ TAMAMLANDI
**Durum:** Tamamlandı  
**Açıklama:** Proje temel yapısı, model sınıfları, harita yapısı, kural motoru ve kontrol sistemi

**Kapsam:**
- Model paketi (Position, Vehicle, Route)
- Map paketi (CityMap, Obstacle, RestrictedZone)
- Rules paketi (TrafficRuleEngine, SpeedLimitRule, EntryExitRule)
- Control paketi (TrafficControlCenter, BaseStation, FlightAuthorization)
- Test altyapısı (280 test metodu)

---

### Epic 2: Kullanıcı Arayüzü (UI) ⏳ PLANLANDI
**Durum:** Planlandı  
**Öncelik:** Yüksek  
**Açıklama:** JavaFX tabanlı kullanıcı arayüzü geliştirme

**Kapsam:**
- Ana pencere (AirTrafficMainWindow)
- Harita görselleştirme
- Araç listesi görüntüleme
- Sistem durumu paneli
- Gerçek zamanlı güncelleme

---

### Epic 3: Gelişmiş Özellikler ⏳ PLANLANDI
**Durum:** Planlandı  
**Öncelik:** Orta  
**Açıklama:** Çarpışma önleme, dinamik yükseklik katmanları, hava durumu entegrasyonu

**Kapsam:**
- Çarpışma önleme sistemi
- Dinamik yükseklik katmanları
- Hava durumu entegrasyonu
- Simülasyon modülü
- Veri kalıcılığı (JSON/XML)

---

### Epic 4: Havacılık Standartları Uyumluluğu ⏳ PLANLANDI
**Durum:** Planlandı  
**Öncelik:** Yüksek (Üretim için kritik)  
**Açıklama:** ICAO, FAA, EASA U-Space, ASTM UTM standartlarına uyumluluk

**Kapsam:**
- ICAO standartları entegrasyonu
- FAA uyumluluk kontrolleri
- EASA U-Space uyumluluğu
- ASTM UTM standartları

---

### Epic 5: Performans ve Güvenilirlik ⏳ PLANLANDI
**Durum:** Planlandı  
**Öncelik:** Orta-Yüksek  
**Açıklama:** Yüksek kullanılabilirlik, ölçeklenebilirlik, güvenlik

**Kapsam:**
- Yüksek kullanılabilirlik (HA) yapısı
- Ölçeklenebilirlik iyileştirmeleri
- Veri güvenliği
- Loglama ve izleme

---

## 📝 User Stories

### Epic 2: UI Geliştirme

#### US-2.1: Ana Pencere Oluşturma
**As a** sistem operatörü  
**I want to** ana pencereyi açabilmek  
**So that** hava trafik kontrol sistemini kullanabilirim

**Kabul Kriterleri:**
- [ ] AirTrafficMainWindow.java sınıfı oluşturuldu
- [ ] JavaFX Scene oluşturuldu
- [ ] Pencere başarıyla açılıyor
- [ ] Temel menü yapısı var

**Öncelik:** Yüksek  
**Tahmini Süre:** 2 gün

---

#### US-2.2: Harita Görselleştirme
**As a** sistem operatörü  
**I want to** şehir haritasını görselleştirebilmek  
**So that** araçların konumlarını görebilirim

**Kabul Kriterleri:**
- [ ] Harita canvas'ı oluşturuldu
- [ ] Şehir sınırları gösteriliyor
- [ ] Engeller görselleştiriliyor
- [ ] Yasak bölgeler gösteriliyor
- [ ] Zoom ve pan özellikleri var

**Öncelik:** Yüksek  
**Tahmini Süre:** 5 gün

---

#### US-2.3: Araç Listesi Görüntüleme
**As a** sistem operatörü  
**I want to** aktif araçları listeleyebilmek  
**So that** araç durumlarını takip edebilirim

**Kabul Kriterleri:**
- [ ] Araç listesi tablosu oluşturuldu
- [ ] Araç bilgileri gösteriliyor (ID, tip, konum, hız, durum)
- [ ] Gerçek zamanlı güncelleme var
- [ ] Filtreleme özelliği var

**Öncelik:** Yüksek  
**Tahmini Süre:** 3 gün

---

#### US-2.4: Sistem Durumu Paneli
**As a** sistem operatörü  
**I want to** sistem durumunu görebilmek  
**So that** sistem sağlığını izleyebilirim

**Kabul Kriterleri:**
- [ ] Sistem durumu paneli oluşturuldu
- [ ] Aktif araç sayısı gösteriliyor
- [ ] Baz istasyonu durumu gösteriliyor
- [ ] Kural motoru durumu gösteriliyor
- [ ] Sistem sağlık göstergeleri var

**Öncelik:** Orta  
**Tahmini Süre:** 2 gün

---

### Epic 3: Gelişmiş Özellikler

#### US-3.1: Çarpışma Önleme Sistemi
**As a** sistem operatörü  
**I want to** çarpışma risklerini tespit edebilmek  
**So that** güvenliği sağlayabilirim

**Kabul Kriterleri:**
- [ ] Çarpışma tespit algoritması geliştirildi
- [ ] Uyarı sistemi entegre edildi
- [ ] Otomatik rota düzeltme özelliği var
- [ ] Testler yazıldı

**Öncelik:** Yüksek  
**Tahmini Süre:** 8 gün

---

#### US-3.2: Dinamik Yükseklik Katmanları
**As a** sistem operatörü  
**I want to** dinamik yükseklik katmanları kullanabilmek  
**So that** trafik yoğunluğunu yönetebilirim

**Kabul Kriterleri:**
- [ ] Yükseklik katmanı yönetimi geliştirildi
- [ ] Otomatik katman atama özelliği var
- [ ] Katman kapasitesi kontrolü var
- [ ] Testler yazıldı

**Öncelik:** Orta  
**Tahmini Süre:** 5 gün

---

#### US-3.3: Hava Durumu Entegrasyonu
**As a** sistem operatörü  
**I want to** hava durumu verilerini kullanabilmek  
**So that** güvenli rotalar belirleyebilirim

**Kabul Kriterleri:**
- [ ] Hava durumu API entegrasyonu yapıldı
- [ ] Hava durumu verileri saklanıyor
- [ ] Rota planlamada hava durumu dikkate alınıyor
- [ ] Testler yazıldı

**Öncelik:** Orta  
**Tahmini Süre:** 6 gün

---

#### US-3.4: Simülasyon Modülü
**As a** geliştirici  
**I want to** simülasyon modülü kullanabilmek  
**So that** sistemi test edebilirim

**Kabul Kriterleri:**
- [ ] Simülasyon motoru geliştirildi
- [ ] Senaryo tanımlama özelliği var
- [ ] Gerçek zamanlı simülasyon çalışıyor
- [ ] Testler yazıldı

**Öncelik:** Düşük  
**Tahmini Süre:** 10 gün

---

#### US-3.5: Veri Kalıcılığı
**As a** sistem operatörü  
**I want to** verileri kaydedip yükleyebilmek  
**So that** sistem durumunu koruyabilirim

**Kabul Kriterleri:**
- [ ] JSON/XML export/import özelliği var
- [ ] Harita verileri kaydediliyor
- [ ] Araç verileri kaydediliyor
- [ ] Sistem ayarları kaydediliyor
- [ ] Testler yazıldı

**Öncelik:** Orta  
**Tahmini Süre:** 4 gün

---

### Epic 4: Havacılık Standartları

#### US-4.1: ICAO Standartları Entegrasyonu
**As a** sistem operatörü  
**I want to** ICAO standartlarına uygun çalışabilmek  
**So that** uluslararası standartlara uyumlu olabilirim

**Kabul Kriterleri:**
- [ ] ICAO standartları dokümante edildi
- [ ] Standartlara uyumluluk kontrolleri eklendi
- [ ] Testler yazıldı

**Öncelik:** Yüksek  
**Tahmini Süre:** 7 gün

---

#### US-4.2: FAA Uyumluluk Kontrolleri
**As a** sistem operatörü  
**I want to** FAA standartlarına uygun çalışabilmek  
**So that** ABD pazarında kullanılabilir

**Kabul Kriterleri:**
- [ ] FAA standartları dokümante edildi
- [ ] Uyumluluk kontrolleri eklendi
- [ ] Testler yazıldı

**Öncelik:** Orta  
**Tahmini Süre:** 7 gün

---

#### US-4.3: EASA U-Space Uyumluluğu
**As a** sistem operatörü  
**I want to** EASA U-Space standartlarına uygun çalışabilmek  
**So that** Avrupa pazarında kullanılabilir

**Kabul Kriterleri:**
- [ ] EASA U-Space standartları dokümante edildi
- [ ] Uyumluluk kontrolleri eklendi
- [ ] Testler yazıldı

**Öncelik:** Yüksek  
**Tahmini Süre:** 8 gün

---

#### US-4.4: ASTM UTM Standartları
**As a** sistem operatörü  
**I want to** ASTM UTM standartlarına uygun çalışabilmek  
**So that** küresel standartlara uyumlu olabilirim

**Kabul Kriterleri:**
- [ ] ASTM UTM standartları dokümante edildi
- [ ] Uyumluluk kontrolleri eklendi
- [ ] Testler yazıldı

**Öncelik:** Orta  
**Tahmini Süre:** 7 gün

---

### Epic 5: Performans ve Güvenilirlik

#### US-5.1: Yüksek Kullanılabilirlik (HA) Yapısı
**As a** sistem operatörü  
**I want to** yüksek kullanılabilirlik yapısı kullanabilmek  
**So that** sistem kesintisiz çalışabilir

**Kabul Kriterleri:**
- [ ] HA mimarisi tasarlandı
- [ ] Failover mekanizması eklendi
- [ ] Yedekleme sistemi kuruldu
- [ ] Testler yazıldı

**Öncelik:** Orta  
**Tahmini Süre:** 10 gün

---

#### US-5.2: Ölçeklenebilirlik İyileştirmeleri
**As a** sistem operatörü  
**I want to** sistemi ölçeklendirebilmek  
**So that** daha fazla araç yönetebilirim

**Kabul Kriterleri:**
- [ ] Performans analizi yapıldı
- [ ] Bottleneck'ler tespit edildi
- [ ] Optimizasyonlar yapıldı
- [ ] Testler yazıldı

**Öncelik:** Orta  
**Tahmini Süre:** 8 gün

---

#### US-5.3: Veri Güvenliği
**As a** sistem operatörü  
**I want to** veri güvenliğini sağlayabilmek  
**So that** hassas bilgiler korunabilir

**Kabul Kriterleri:**
- [ ] Şifreleme mekanizması eklendi
- [ ] Erişim kontrolü yapıldı
- [ ] Güvenlik testleri yazıldı

**Öncelik:** Yüksek  
**Tahmini Süre:** 6 gün

---

#### US-5.4: Loglama ve İzleme
**As a** sistem operatörü  
**I want to** sistem loglarını izleyebilmek  
**So that** sorunları tespit edebilirim

**Kabul Kriterleri:**
- [ ] Loglama sistemi kuruldu
- [ ] İzleme dashboard'u oluşturuldu
- [ ] Alarm sistemi eklendi
- [ ] Testler yazıldı

**Öncelik:** Orta  
**Tahmini Süre:** 4 gün

---

## 🔧 Teknik Gereksinimler

### TR-1: Java 17
- Minimum Java versiyonu: 17
- LTS desteği gerekli

### TR-2: JavaFX 17.0.10
- UI framework olarak kullanılacak
- Platform bağımsız çalışmalı

### TR-3: Maven 3.x
- Build tool olarak kullanılacak
- Dependency management

### TR-4: JUnit 5.9.2
- Test framework
- Minimum %80 kod kapsamı hedefleniyor

### TR-5: Gson 2.10.1
- JSON işleme için
- Veri kalıcılığı için kullanılacak

### TR-6: Log4j 2.20.0
- Loglama için
- Yapılandırılabilir log seviyeleri

---

## ✈️ Havacılık Standartları

### HS-1: ICAO Standartları
- International Civil Aviation Organization
- Uluslararası hava trafik kontrol standartları

### HS-2: FAA Standartları
- Federal Aviation Administration
- ABD hava trafik kontrol standartları

### HS-3: EASA U-Space
- European Union Aviation Safety Agency
- Avrupa düşük seviye hava sahası yönetimi

### HS-4: ASTM UTM
- ASTM International
- Küresel UTM (Unmanned Traffic Management) standartları

---

**Not:** Bu dosya, proje geliştirme sürecinde güncellenecektir. Yeni ihtiyaçlar eklendikçe bu dosya revize edilecektir.

