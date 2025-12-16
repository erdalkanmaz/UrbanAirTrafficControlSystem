# Urban Air Traffic Control System - Sprint Planlama

**Proje:** UrbanAirTrafficControlSystem  
**Versiyon:** 4.0-SNAPSHOT  
**Son Güncelleme:** 2025-12-13  
**Plan Versiyonu:** v4.0

---

## 📅 Sprint Takvimi

**Sprint Uzunluğu:** 2 hafta (10 iş günü)  
**Sprint Başlangıç:** Her Pazartesi  
**Sprint Bitiş:** Her Cuma (2 hafta sonra)

---

## 🎯 Sprint 1: UI Temelleri ✅ TAMAMLANDI

**Konu Başlığı:** Kontrol Merkezi Yazılımı - Kullanıcı Arayüzü Geliştirme  
**Tarih:** 2025-12-11 - 2025-12-13  
**Sprint Hedefi:** Ana pencere ve temel UI yapısı oluşturulacak  
**Durum:** ✅ Tamamlandı

### Sprint Backlog

| PBI | User Story | Tahmini Süre | Durum | Atanan |
|-----|-----------|--------------|-------|--------|
| US-2.1 | Ana Pencere Oluşturma | 2 gün | ✅ Tamamlandı | - |
| US-2.2 | Harita Görselleştirme (Temel) | 3 gün | ✅ Tamamlandı | - |
| US-2.3 | Araç Listesi Görüntüleme | 3 gün | ✅ Tamamlandı | - |
| US-2.4 | Sistem Durumu Paneli | 2 gün | ✅ Tamamlandı | - |

**Toplam Tahmini Süre:** 10 gün  
**Sprint Kapasitesi:** 10 gün  
**Tamamlanan Süre:** 10 gün  
**Velocity:** 10 gün ✅

### Definition of Done
- [x] Kod yazıldı ve commit edildi (US-2.1, US-2.2, US-2.3, US-2.4)
- [x] Testler yazıldı (US-2.1 - 10 test, US-2.2 - 12 test, US-2.3 - 11 test, US-2.4 - 9 test)
- [x] Uygulama çalıştırıldı (JavaFX SDK yapılandırması tamamlandı)
- [x] Testler geçti (UI testleri - 42/42 test başarılı ✅)
- [x] Dokümantasyon güncellendi (SISTEM_KRITERLERI.md, HAVACILIK_STANDARTLARI_DOKUMANTASYONU.md, MIMARI_VE_PERFORMANS_ANALIZI.md)

### Sprint Review Kriterleri
- [x] Kullanıcılar ana pencereyi açabilir (US-2.1 ✅)
- [x] Temel harita görselleştirmesi çalışır (US-2.2 ✅)
- [x] Menü yapısı oluşturuldu (US-2.1 ✅)
- [x] Araç listesi görüntülenebilir (US-2.3 ✅)
- [x] Sistem durumu paneli çalışır (US-2.4 ✅)

### Sprint 1 Notları
- ✅ Tüm user story'ler tamamlandı (US-2.1, US-2.2, US-2.3, US-2.4)
- ✅ 42 UI testi yazıldı ve geçti (10 + 12 + 11 + 9)
- ✅ JavaFX SDK yapılandırması tamamlandı (17.0.17)
- ✅ Havacılık standartları dokümantasyonu oluşturuldu (HAVACILIK_STANDARTLARI_DOKUMANTASYONU.md)
- ✅ Mimari ve performans analizi hazırlandı (MIMARI_VE_PERFORMANS_ANALIZI.md)
- ✅ Sistem kriterleri dokümantasyonu oluşturuldu (SISTEM_KRITERLERI.md)
- ✅ Mouse wheel zoom özelliği eklendi
- ✅ Örnek araçlar eklendi (test için - 3 araç)
- ✅ Harita görselleştirmesi çalışıyor (şehir sınırları, engeller, yasak bölgeler)
- ✅ Araç listesi çalışıyor (8 sütun: ID, Type, Latitude, Longitude, Altitude, Velocity, Status, Fuel)
- ✅ Sistem durumu paneli çalışıyor (10 kontrol satırı: Operational, Active Vehicles, Pending Authorizations, Approved (Not Registered), In Flight, Landing, Emergency, Base Stations, Rule Engine, System Health)

---

## 🎯 Sprint 2: Performans Optimizasyonu ve Gelişmiş Özellikler ✅ TAMAMLANDI

**Konu Başlığı:** Kontrol Merkezi Yazılımı - Performans ve Ölçeklenebilirlik  
**Tarih:** 2025-12-13 - 2025-12-13  
**Sprint Hedefi:** Sistem performansını artırmak ve binlerce aracı yönetebilmek için optimizasyonlar yapılacak  
**Durum:** ✅ Tamamlandı

### Sprint Backlog

| PBI | User Story | Tahmini Süre | Durum | Atanan |
|-----|-----------|--------------|-------|--------|
| US-5.2 (Başlangıç) | Spatial Indexing (Quadtree/R-Tree) | 3 gün | ✅ Tamamlandı | - |
| US-5.2 (Devam) | Asenkron İşleme (Async Processing) | 2 gün | ✅ Tamamlandı | - |
| US-5.2 (Devam) | Batch Processing | 2 gün | ✅ Tamamlandı | - |
| US-2.5 | Gerçek Zamanlı Güncelleme (Real-time Updates) | 2 gün | ✅ Tamamlandı | - |
| US-2.6 | Harita Üzerinde Araç Görselleştirme | 1 gün | ✅ Tamamlandı | - |

**Toplam Tahmini Süre:** 10 gün  
**Sprint Kapasitesi:** 10 gün  
**Tamamlanan Süre:** 10 gün  
**Velocity:** 10 gün ✅

### Definition of Done
- [x] Kod yazıldı ve commit edildi (Tüm user story'ler)
- [x] Testler yazıldı ve geçti (51 yeni test, toplam ~377 test)
- [x] Performans testleri yapıldı (Quadtree: 1000 araç < 200ms)
- [x] Dokümantasyon güncellendi (SISTEM_KRITERLERI.md, SPRINT_PLAN.md)

### Sprint Review Kriterleri
- [x] Spatial indexing çalışıyor (Quadtree implementasyonu ✅)
- [x] Asenkron işleme aktif (AsyncProcessingService ✅)
- [x] Batch processing çalışıyor (BatchProcessor ✅)
- [x] Gerçek zamanlı güncelleme çalışıyor (100ms güncelleme, 10 FPS ✅)
- [x] Harita üzerinde araçlar görselleştiriliyor (Araç tipine göre renk kodlaması ✅)

### Sprint 2 Notları
- ✅ Quadtree spatial indexing implementasyonu tamamlandı (18 test)
- ✅ TrafficControlCenter ile Quadtree entegrasyonu yapıldı (5 entegrasyon testi)
- ✅ AsyncProcessingService oluşturuldu (7 test) - Paralel işleme desteği
- ✅ BatchProcessor oluşturuldu (7 test) - Toplu güncelleme desteği
- ✅ RealTimeUpdateService oluşturuldu (8 test) - 100ms güncelleme aralığı (havacılık standartlarına uygun)
- ✅ MapVisualization'a araç görselleştirmesi eklendi (6 test) - Araç tipine göre renk kodlaması, yön göstergesi
- ✅ Performans iyileştirmeleri: O(n) → O(log n) (spatial queries)
- ✅ Toplam 51 yeni test eklendi, tüm testler başarılı
- ✅ Güncelleme süresi: 100ms (10 FPS) - ICAO Annex 11 standartlarına uygun (< 1 saniye)

---

## 🎯 Sprint 3: Güvenlik ve Standartlar ✅ TAMAMLANDI

**Konu Başlığı:** Kontrol Merkezi Yazılımı - Güvenlik ve Standartlar Entegrasyonu  
**Tarih:** 2025-12-13 - 2025-12-13  
**Sprint Hedefi:** Çarpışma önleme sistemi ve ICAO standartları entegrasyonu  
**Durum:** ✅ Tamamlandı

### Sprint Backlog

| PBI | User Story | Tahmini Süre | Durum | Atanan |
|-----|-----------|--------------|-------|--------|
| US-3.1 | Çarpışma Önleme Sistemi | 8 gün | ✅ Tamamlandı | - |
| US-4.1 (Başlangıç) | ICAO Standartları (Temel) | 2 gün | ✅ Tamamlandı | - |

**Toplam Tahmini Süre:** 10 gün  
**Sprint Kapasitesi:** 10 gün  
**Tamamlanan Süre:** 10 gün  
**Velocity:** 10 gün ✅

### Definition of Done
- [x] Kod yazıldı ve commit edildi (US-3.1, US-4.1)
- [x] Testler yazıldı ve geçti (CollisionRiskTest: 22 test, CollisionDetectionServiceTest: 18 test, ICAOStandardsComplianceTest: 18 test)
- [x] Dokümantasyon güncellendi (SISTEM_KRITERLERI.md, PROJE_CONTEXT.md, GELISTIRME_DURUMU.md)
- [x] PROJE_CONTEXT.md güncellendi
- [x] GELISTIRME_DURUMU.md güncellendi

### Sprint Review Kriterleri
- [x] Çarpışma önleme sistemi çalışıyor (CollisionDetectionService ✅)
- [x] ICAO standartları entegrasyonu başladı (ICAOStandardsCompliance ✅)
- [x] Uyarı sistemi aktif (TrafficControlCenter entegrasyonu ✅)

### Sprint 3 Notları
- ✅ CollisionDetectionService oluşturuldu - çarpışma tespiti algoritması
- ✅ CollisionRisk model sınıfı oluşturuldu (RiskLevel enum ile)
- ✅ TrafficControlCenter'a çarpışma kontrolü entegre edildi
- ✅ ICAOStandardsCompliance oluşturuldu - ICAO Annex 2 uyumluluk kontrolü
- ✅ ComplianceResult model sınıfı oluşturuldu
- ✅ Toplam 58 yeni test yazıldı ve geçti:
  - CollisionRiskTest: 22 test
  - CollisionDetectionServiceTest: 18 test
  - ICAOStandardsComplianceTest: 18 test
- ✅ Minimum güvenli mesafeler: 50m yatay, 10m dikey (ICAO Annex 2)
- ✅ Risk seviyeleri: LOW, MEDIUM, HIGH, CRITICAL
- ✅ Gelecek konum projeksiyonu: 30 saniye zaman ufku
- ✅ Separation standartları kontrolü tamamlandı
- ✅ Uçuş kuralları uyumluluğu kontrolü tamamlandı
- ✅ İletişim gereksinimleri kontrolü tamamlandı (5km menzil)

---

## 🎯 Sprint 4: Gelişmiş Güvenlik ve Operasyonellik 🔄 DEVAM EDİYOR

**Konu Başlığı:** Kontrol Merkezi Yazılımı - Dinamik Yükseklik Katmanları ve Veri Kalıcılığı  
**Tarih:** 2025-12-13 - [Devam Ediyor]  
**Sprint Hedefi:** Yükseklik katmanları sistemi ve veri kalıcılığı özellikleri geliştirilecek  
**Durum:** 🔄 Devam Ediyor (Faz 1 Tamamlandı ✅)

### Sprint Backlog

| PBI | User Story | Tahmini Süre | Durum | Atanan |
|-----|-----------|--------------|-------|--------|
| US-3.2 (Faz 1) | Dinamik Yükseklik Katmanları - Temel Katmanlar | 5 gün | ✅ Tamamlandı | - |
| US-3.2 (Faz 2) | Yol Bazlı Katman Organizasyonu | [Planlanıyor] | ⏳ Planlandı | - |
| US-3.5 | Veri Kalıcılığı (JSON/XML) | 4 gün | ⏳ Planlandı | - |

**Toplam Tahmini Süre:** 9 gün (Faz 1: 5 gün tamamlandı)  
**Sprint Kapasitesi:** 10 gün  
**Tamamlanan Süre:** 5 gün  
**Velocity:** 5 gün (Faz 1 tamamlandı)

### Sprint 4 Teknik Detaylar

#### US-3.2: Dinamik Yükseklik Katmanları (Faz 1 - Temel Katmanlar)

**Amaç:** Hava sahasını dikey katmanlara bölerek trafik organizasyonunu ve güvenliği artırmak.

**Yapılacaklar:**
1. **AltitudeLayer Model/Enum Oluşturma**
   - `AltitudeLayer.java` enum/model sınıfı
   - Katman tanımları:
     - **LAYER_1 (Low Altitude):** 0-60m - Teslimat dronları, alçak irtifa trafiği
     - **LAYER_2 (Medium Altitude):** 60-120m - Şehir içi yolcu dronları, normal trafik
     - **LAYER_3 (High Altitude):** 120-180m - Acil durum araçları, öncelikli trafik
   - Her katman için özellikler:
     - Minimum/Maksimum yükseklik
     - Hız limiti
     - İzin gereksinimleri
     - Araç tipi kısıtlamaları

2. **CityMap Entegrasyonu**
   - `CityMap` sınıfına katman yönetimi ekleme
   - `getLayerForAltitude(double altitude)` metodu
   - `getSafePassageAltitude()` metodunu katman bilgisiyle güncelleme
   - Engelleri (binalar, hastaneler) göz önünde bulundurarak katman yüksekliklerini dinamik hesaplama

3. **Vehicle Entegrasyonu**
   - `Vehicle` sınıfına `getCurrentLayer()` metodu ekleme
   - Katman değişikliği tespiti ve uyarı sistemi

4. **CollisionDetectionService Entegrasyonu**
   - Çarpışma kontrolünde katman bilgisini kullanma
   - Aynı katmandaki araçlar için daha sıkı kontrol
   - Farklı katmanlardaki araçlar için gevşetilmiş kontrol (dikey mesafe yeterliyse)

5. **ICAOStandardsCompliance Entegrasyonu**
   - Katman bazlı ayrım standartları kontrolü
   - Katman kurallarına uyumluluk kontrolü

6. **UI Güncellemeleri (Opsiyonel)**
   - Harita görselleştirmesinde katman bilgisini gösterme
   - Sistem durumu panelinde katman istatistikleri

**Test Gereksinimleri:**
- AltitudeLayer testleri (katman hesaplama, sınır kontrolleri)
- CityMap katman entegrasyonu testleri
- Vehicle katman hesaplama testleri
- CollisionDetectionService katman entegrasyonu testleri
- ICAOStandardsCompliance katman entegrasyonu testleri

**Beklenen Test Sayısı:** ~30-40 yeni test

#### US-3.5: Veri Kalıcılığı (JSON/XML)

**Amaç:** Sistem durumunu (harita, araçlar, ayarlar) dosyaya kaydetme ve yükleme özelliği.

**Yapılacaklar:**
1. **PersistenceService Oluşturma**
   - `PersistenceService.java` servis sınıfı
   - JSON formatında kaydetme/yükleme (Gson kullanarak)
   - XML formatında kaydetme/yükleme (opsiyonel, gelecek sprint)

2. **Model Serialization**
   - `CityMap` serialization desteği
   - `Vehicle` serialization desteği
   - `TrafficControlCenter` state serialization
   - `BaseStation` serialization
   - `Route` serialization

3. **TrafficControlCenter Entegrasyonu**
   - `saveState(String filePath)` metodu
   - `loadState(String filePath)` metodu
   - Hata yönetimi (dosya bulunamadı, format hatası vb.)

4. **UI Entegrasyonu**
   - Menüye "Kaydet" ve "Yükle" seçenekleri ekleme
   - Dosya seçici dialog'ları

**Test Gereksinimleri:**
- PersistenceService testleri (kaydetme/yükleme)
- Serialization/Deserialization testleri
- Hata durumu testleri (geçersiz dosya, eksik veri vb.)

**Beklenen Test Sayısı:** ~20-25 yeni test

### Definition of Done

**Faz 1 (Temel Katmanlar):**
- [x] Kod yazıldı ve commit edildi (US-3.2 Faz 1)
- [x] Testler yazıldı ve geçti (~35 yeni test)
- [x] Dokümantasyon güncellendi (SISTEM_KRITERLERI.md, PROJE_CONTEXT.md, GELISTIRME_DURUMU.md, CHAT_GECMISI.md)
- [x] Uygulama çalıştırıldı ve özellikler doğrulandı
- [x] Katman sistemi çarpışma kontrolünde kullanılıyor
- [x] Katman sistemi ICAO standartları kontrolünde kullanılıyor

**Faz 2 ve US-3.5:**
- [ ] Yol bazlı katman organizasyonu (Faz 2)
- [ ] Tek yönlü trafik organizasyonu (Faz 2)
- [ ] Kesişen yollar ve dönüş kuralları (Faz 2)
- [ ] Veri kalıcılığı çalışıyor (kaydetme/yükleme) (US-3.5)

### Sprint Review Kriterleri

**Faz 1 (Temel Katmanlar) - ✅ TAMAMLANDI:**
- [x] Yükseklik katmanları tanımlanmış ve çalışıyor (3 katman: 0-60m, 60-120m, 120-180m)
- [x] Araçlar için katman hesaplama yapılıyor
- [x] Çarpışma kontrolünde katman bilgisi kullanılıyor
- [x] Engeller (binalar, hastaneler) katman hesaplamasında göz önünde bulunduruluyor
- [x] ICAO standartları kontrolünde katman bilgisi kullanılıyor
- [x] Tüm testler geçiyor

**Faz 2 ve US-3.5:**
- [ ] Yol bazlı katman organizasyonu çalışıyor
- [ ] Tek yönlü trafik organizasyonu çalışıyor
- [ ] Kesişen yollar ve dönüş kuralları çalışıyor
- [ ] Sistem durumu dosyaya kaydedilebiliyor (JSON formatında)
- [ ] Sistem durumu dosyadan yüklenebiliyor (JSON formatında)

### Sprint 4 Notları

**Faz 1 (Temel Katmanlar) - ✅ TAMAMLANDI:**
- [x] AltitudeLayer enum/model oluşturuldu (LAYER_1_LOW, LAYER_2_MEDIUM, LAYER_3_HIGH)
- [x] CityMap katman yönetimi entegre edildi (`getLayerForAltitude()` metodu)
- [x] Vehicle katman hesaplama eklendi (`getCurrentLayer()` metodu)
- [x] CollisionDetectionService katman entegrasyonu yapıldı (risk skoru azaltma mekanizması)
- [x] ICAOStandardsCompliance katman entegrasyonu yapıldı (katman bazlı separation kontrolleri)
- [x] ~35 yeni test yazıldı ve geçti
- [x] Uygulama çalıştırıldı ve doğrulandı
- [x] Konsol çıktısında katman bilgileri gösteriliyor

**Faz 2 (Yol Bazlı Organizasyon) - ✅ TEMEL YAPI TAMAMLANDI:**
- [x] RouteDirection enum oluşturuldu (FORWARD, REVERSE)
- [x] RouteSegment model sınıfı oluşturuldu
- [x] Route.createSegments() metodu eklendi
- [x] RouteNetwork segment yönetimi eklendi (createSegmentsForRoute, findNearestSegment, getSegmentsByDirection)
- [x] Vehicle'a currentSegment desteği eklendi
- [x] TrafficFlowService oluşturuldu (temel trafik akışı yönetimi)
- [ ] Ana yollar için doğu-batı/güney-kuzey katman organizasyonu (tartışma sonrası)
- [ ] Tali yollar için tek katman organizasyonu (tartışma sonrası)
- [ ] Geçiş yönetimi (ana yol ↔ tali yol) (tartışma sonrası)
- [ ] Kesişme yönetimi (tali yollarda) (tartışma sonrası)
- [ ] Gerçekçi şehir haritası entegrasyonu

**US-3.5 (Veri Kalıcılığı) - ⏳ PLANLANDI:**
- [ ] PersistenceService oluşturuldu
- [ ] JSON serialization/deserialization tamamlandı
- [ ] UI menü güncellemeleri yapıldı

**Gözlemler ve Tespit Edilen Sorunlar:**

1. **Yol Bazlı Katman Organizasyonu İhtiyacı:**
   - Sorun: Binlerce aracı aynı yol üzerinde, yolun gidiş ve geliş olarak kendi içinde katmanlara bölündüğünü düşünürsek, en fazla 20m'lik bir yükseklik içinde farklı yükseklik katmanlarına yerleştirmek pek mümkün değil.
   - Öneri: Yol bazlı katman organizasyonu gerekiyor. Her yol segmenti için gidiş ve geliş yönleri ayrı katmanlar olmalı. Her katman içinde tüm araçlar aynı seviyede (yükseklikte) olmalı.

2. **Tek Yönlü Trafik Organizasyonu:**
   - Sorun: Tek yönlü bir trafik olacağı için herhangi bir katman içinde tek bir yöne doğru trafikte bütün araçlar aynı seviyede yer almalı.
   - Öneri: Ana yolda tüm araçlar aynı hız ve seviyede hareket etmeli. Sadece kesişen ve farklı yükseklikteki yollara dönüş yapan araçlar farklı hız ve seviyelere geçmeli. Yol segmenti bazlı hız limitleri ve yükseklik seviyeleri tanımlanmalı.

3. **Kesişen Yollar ve Dönüşler:**
   - Sorun: Farklı yükseklikteki yollara dönüş yapan araçlar için geçiş mekanizması gerekiyor.
   - Öneri: Kesişen yollar için geçiş katmanları tanımlanmalı. Dönüş yapan araçlar için yükseklik ve hız geçiş kuralları olmalı. Geçiş sırasında çarpışma riski artacağı için özel kontrol mekanizmaları gerekiyor.

4. **Uygulama Haritası İhtiyacı:**
   - Mevcut Durum: Şu anda örnek/test haritası kullanılıyor, gerçekçi bir şehir haritası yok.
   - Öneri: Gerçekçi bir şehir haritası temin edilmeli. Harita üzerinde yol ağı (RouteNetwork) detaylı olmalı, engeller (binalar, hastaneler) gerçekçi konumlarda olmalı, yasak bölgeler tanımlanmalı, yol segmentleri ve kesişimler net olmalı.

---

## 📊 Sprint Metrikleri

### Velocity Tracking

| Sprint | Planlanan | Tamamlanan | Velocity | Notlar |
|--------|-----------|------------|----------|--------|
| Sprint 1 | 10 gün | 10 gün | 10 gün | ✅ Tamamlandı - Tüm user story'ler başarıyla tamamlandı |
| Sprint 2 | 10 gün | 10 gün | 10 gün | ✅ Tamamlandı - Performans optimizasyonları ve gerçek zamanlı güncelleme |
| Sprint 3 | 10 gün | 10 gün | 10 gün | ✅ Tamamlandı - Çarpışma önleme sistemi ve ICAO standartları entegrasyonu |
| Sprint 4 | 9 gün | 0 gün | - | ⏳ Planlandı - Dinamik yükseklik katmanları ve veri kalıcılığı |

**Hedef Velocity:** 8-10 gün/sprint  
**Ortalama Velocity:** 10 gün/sprint (Sprint 1, Sprint 2, Sprint 3)

---

## 🔄 Sprint Süreci

### Sprint Planning
- **Süre:** 2 saat
- **Katılımcılar:** Tüm ekip
- **Aktivite:** Backlog'dan sprint backlog'u oluşturma

### Daily Standup
- **Süre:** 15 dakika
- **Sıklık:** Her gün
- **Sorular:**
  - Dün ne yaptım?
  - Bugün ne yapacağım?
  - Herhangi bir engel var mı?

### Sprint Review
- **Süre:** 2 saat
- **Sıklık:** Sprint sonunda
- **Aktivite:** Tamamlanan işlerin gösterimi

### Sprint Retrospective
- **Süre:** 1 saat
- **Sıklık:** Sprint sonunda
- **Aktivite:** İyileştirme önerileri

---

## 📝 Plan Versiyon Geçmişi

| Versiyon | Tarih | Değişiklikler |
|----------|-------|---------------|
| v4.0 | 2025-12-13 | Sprint 4 planlandı - Dinamik yükseklik katmanları ve veri kalıcılığı |
| v3.0 | 2025-12-13 | Sprint 3 tamamlandı - Çarpışma önleme sistemi ve ICAO standartları entegrasyonu |
| v2.0 | 2025-12-13 | Sprint 1 tamamlandı, Sprint 2 güncellendi (performans optimizasyonu odaklı), Sprint 3 planlandı |
| v1.0 | 2025-12-11 | İlk sprint planı oluşturuldu |

**Not:** Eski plan versiyonları `planning/` klasöründe saklanmaktadır.

---

**Not:** Bu sprint planı, her sprint sonunda güncellenecektir. Yeni sprint'ler eklendikçe bu dosya revize edilecektir.
