# Urban Air Traffic Control System - Proje Bağlamı ve Mimari

**Bu dosya, proje hakkında tüm kritik bilgileri içerir. Yeni bir chat oturumunda bu dosya okunarak proje durumu anlaşılabilir.**

**Son Güncelleme:** 2025-12-13 (Sprint 4 Faz 1 tamamlandı)  
**Versiyon:** 2.0-SNAPSHOT

---

## 📋 İçindekiler

1. [Proje Genel Bakış](#proje-genel-bakış)
2. [Mimari Yapı](#mimari-yapı)
3. [Paket Detayları](#paket-detayları)
4. [Tasarım Kararları](#tasarım-kararları)
5. [Geliştirme Prensipleri](#geliştirme-prensipleri)
6. [Mevcut Durum](#mevcut-durum)
7. [Gelecek Planlar](#gelecek-planlar)

---

## 🎯 Proje Genel Bakış

### Amaç
Şehir içi hava taşımacılığı için kapsamlı hava trafik seyir ve yönetim programı. VTOL (Vertical Take-Off and Landing) araçlar için trafik kontrolü, rota yönetimi ve güvenlik sistemleri sağlar.

### Teknoloji Stack
- **Java:** 17
- **JavaFX:** 17.0.10 (UI için)
- **Maven:** 3.x (Build tool)
- **JUnit:** 5.9.2 (Testing)
- **Gson:** 2.10.1 (JSON işleme)
- **Log4j:** 2.20.0 (Logging)

### Proje Yapısı
```
UrbanAirTrafficControlSystem/
├── src/
│   ├── main/java/com/airtraffic/
│   │   ├── model/      # Veri modelleri (6 dosya)
│   │   ├── map/        # Harita yönetimi (6 dosya)
│   │   ├── rules/      # Trafik kuralları (5 dosya)
│   │   ├── control/    # Merkezi kontrol (4 dosya)
│   │   ├── spatial/    # Spatial indexing (1 dosya: Quadtree)
│   │   └── ui/         # Kullanıcı arayüzü (5 dosya)
│   │       ├── AirTrafficMainWindow.java
│   │       ├── MapVisualization.java
│   │       ├── VehicleListView.java
│   │       ├── SystemStatusPanel.java
│   │       └── RealTimeUpdateService.java
│   └── test/java/com/airtraffic/
│       ├── model/      # Model testleri (3 dosya, 53 test)
│       ├── map/        # Map testleri (4 dosya, 76 test)
│       ├── rules/      # Rules testleri (4 dosya, 84 test)
│       ├── control/    # Control testleri (3 dosya, 67 test)
│       ├── spatial/    # Spatial testleri (1 dosya, 18 test)
│       └── ui/         # UI testleri (5 dosya, 42 test)
├── pom.xml
└── Dokümantasyon dosyaları
```

---

## 🏗️ Mimari Yapı

### Katmanlı Mimari

```
┌─────────────────────────────────────┐
│         UI Layer (JavaFX)           │  ← ✅ Tamamlandı (Sprint 1)
│  ┌──────────────────────────────┐   │
│  │ AirTrafficMainWindow          │   │  ← Ana pencere
│  │ MapVisualization               │   │  ← Harita görselleştirme
│  │ VehicleListView                │   │  ← Araç listesi
│  │ SystemStatusPanel              │   │  ← Sistem durumu
│  │ RealTimeUpdateService           │   │  ← Gerçek zamanlı güncelleme
│  └──────────────────────────────┘   │
├─────────────────────────────────────┤
│      Control Layer                  │
│  ┌──────────────────────────────┐   │
│  │ TrafficControlCenter         │   │  ← Singleton, merkezi koordinasyon
│  │ BaseStation                  │   │  ← İletişim altyapısı
│  │ FlightAuthorization            │   │  ← İzin yönetimi
│  │ AsyncProcessingService       │   │  ← Asenkron işleme (Sprint 2)
│  │ BatchProcessor               │   │  ← Batch processing (Sprint 2)
│  └──────────────────────────────┘   │
├─────────────────────────────────────┤
│      Spatial Layer                  │
│  ┌──────────────────────────────┐   │
│  │ Quadtree                      │   │  ← Spatial indexing (Sprint 2)
│  └──────────────────────────────┘   │
├─────────────────────────────────────┤
│      Rules Layer                    │
│  ┌──────────────────────────────┐   │
│  │ TrafficRuleEngine            │   │  ← Kural motoru
│  │ TrafficRule (abstract)       │   │  ← Temel kural sınıfı
│  │ SpeedLimitRule               │   │  ← Hız limiti kuralları
│  │ EntryExitRule                │   │  ← Giriş/çıkış kuralları
│  └──────────────────────────────┘   │
├─────────────────────────────────────┤
│      Map Layer                      │
│  ┌──────────────────────────────┐   │
│  │ CityMap                      │   │  ← Şehir hava sahası
│  │ RouteNetwork                 │   │  ← Rota ağı
│  │ Obstacle                     │   │  ← Engeller
│  │ RestrictedZone               │   │  ← Yasak bölgeler
│  └──────────────────────────────┘   │
├─────────────────────────────────────┤
│      Model Layer                    │
│  ┌──────────────────────────────┐   │
│  │ Position                     │   │  ← 3D konum
│  │ Vehicle                      │   │  ← VTOL araç
│  │ Route                        │   │  ← Uçuş rotası
│  │ Enums (VehicleType, etc.)    │   │  ← Sabitler
│  └──────────────────────────────┘   │
└─────────────────────────────────────┘
```

### Sınıf İlişkileri

```
TrafficControlCenter (Singleton)
    ├── CityMap
    │   ├── RouteNetwork
    │   ├── Obstacle[]
    │   └── RestrictedZone[]
    ├── TrafficRuleEngine
    │   └── TrafficRule[]
    │       ├── SpeedLimitRule
    │       └── EntryExitRule
    ├── BaseStation[]
    ├── Vehicle[] (activeVehicles)
    ├── FlightAuthorization[]
    ├── Quadtree (spatial indexing)
    ├── AsyncProcessingService
    └── BatchProcessor

AirTrafficMainWindow
    ├── MapVisualization
    ├── VehicleListView
    ├── SystemStatusPanel
    └── RealTimeUpdateService

Vehicle
    ├── Position (3D konum)
    ├── Route (planlanan rota)
    ├── VehicleType (enum)
    ├── VehicleStatus (enum)
    └── AutomationLevel (enum)

Route
    └── Position[] (waypoints)
```

---

## 📦 Paket Detayları

### 1. Model Paketi (`com.airtraffic.model`)

**Amaç:** Temel veri modelleri ve enum'lar

#### Position.java
- **Sorumluluk:** 3D konum bilgisi (latitude, longitude, altitude)
- **Özellikler:**
  - Haversine formülü ile yatay mesafe hesaplama
  - Dikey mesafe hesaplama
  - 3D mesafe hesaplama
  - Timestamp desteği
- **Kullanım:** Tüm konum tabanlı işlemler için temel sınıf

#### Vehicle.java
- **Sorumluluk:** VTOL araç modeli
- **Özellikler:**
  - 3D konum takibi
  - Hız ve yön yönetimi
  - Yakıt seviyesi takibi
  - Acil durum modu
  - Otomasyon seviyesi desteği
- **Validasyonlar:**
  - Hız negatif olamaz
  - Hız maksimum hızı aşamaz
  - Yükseklik maksimum yüksekliği aşamaz
  - Yakıt seviyesi 0-100 arasında

#### Route.java
- **Sorumluluk:** Uçuş rotası tanımları
- **Özellikler:**
  - Waypoint listesi
  - Rota uzunluğu hesaplama
  - Hız limiti ve yükseklik kısıtlamaları
  - Rotaya yakınlık kontrolü
- **Kullanım:** Uçuş planlaması ve rota takibi

#### Enum'lar
- **VehicleType:** DRONE, HELICOPTER, AIR_TAXI, CARGO_DRONE
- **VehicleStatus:** IDLE, IN_FLIGHT, LANDING, EMERGENCY, MAINTENANCE
- **AutomationLevel:** MANUAL, SEMI_AUTOMATED, FULLY_AUTOMATED

---

### 2. Map Paketi (`com.airtraffic.map`)

**Amaç:** Harita ve coğrafi veri yönetimi

#### CityMap.java
- **Sorumluluk:** Şehir hava sahası modeli
- **Özellikler:**
  - Şehir sınırları (min/max lat/lon)
  - Güvenlik kontrolü (isPositionSafe)
  - Güvenli geçiş yüksekliği hesaplama
  - Engel ve yasak bölge yönetimi
- **İlişkiler:**
  - RouteNetwork (rota ağı)
  - Obstacle[] (engeller)
  - RestrictedZone[] (yasak bölgeler)

#### RouteNetwork.java
- **Sorumluluk:** Rota ağı yönetimi
- **Özellikler:**
  - Ana caddeler (mainStreets)
  - Yan sokaklar (sideStreets)
  - Rota ekleme/çıkarma

#### Obstacle.java
- **Sorumluluk:** Engeller (binalar, köprüler, vb.)
- **Özellikler:**
  - 3D konum ve boyutlar
  - Konum içerme kontrolü (contains)
  - ObstacleType enum desteği

#### RestrictedZone.java
- **Sorumluluk:** Yasak bölgeler
- **Özellikler:**
  - Bölge tanımları
  - Konum içerme kontrolü
  - RestrictedZoneType enum desteği

---

### 3. Rules Paketi (`com.airtraffic.rules`)

**Amaç:** Trafik kuralı yönetimi ve uygulaması

#### TrafficRuleEngine.java
- **Sorumluluk:** Kural motoru - tüm kuralları yönetir ve uygular
- **Özellikler:**
  - Kural ekleme/çıkarma
  - İhlal kontrolü (checkViolations)
  - Uyarı kontrolü (checkWarnings)
  - Öncelik bazlı kural sıralama
  - Varsayılan kurallar (initializeDefaultRules)
- **Varsayılan Kurallar:**
  - Ana cadde hız limiti: 60 km/h (16.67 m/s)
  - Sokak hız limiti: 40 km/h (11.11 m/s)
  - Giriş/çıkış kuralları (yüksek öncelik)

#### TrafficRule.java (Abstract)
- **Sorumluluk:** Temel kural sınıfı
- **Özellikler:**
  - Kural ID, adı, önceliği
  - isApplicable() - Kural uygulanabilir mi?
  - isViolated() - Kural ihlal edildi mi?
  - RuleType enum desteği

#### SpeedLimitRule.java
- **Sorumluluk:** Hız limiti kuralları
- **Özellikler:**
  - Maksimum hız tanımlama
  - Hız ihlali kontrolü
  - Uyarı eşiği kontrolü (isWarningNeeded)

#### EntryExitRule.java
- **Sorumluluk:** Giriş/çıkış kuralları
- **Özellikler:**
  - Trafiğe giriş kontrolü
  - Trafikten çıkış kontrolü

---

### 4. Control Paketi (`com.airtraffic.control`)

**Amaç:** Merkezi kontrol ve koordinasyon

#### TrafficControlCenter.java
- **Sorumluluk:** Merkezi trafik kontrol sistemi (Singleton)
- **Özellikler:**
  - Singleton pattern
  - Aktif araç yönetimi (ConcurrentHashMap)
  - Uçuş izni yönetimi
  - Baz istasyonu yönetimi
  - Şehir haritası yükleme
  - Kural motoru entegrasyonu
- **Thread Safety:** ConcurrentHashMap kullanımı

#### BaseStation.java
- **Sorumluluk:** Baz istasyonu - araçlarla iletişim
- **Özellikler:**
  - Kapsama yarıçapı (varsayılan: 5km)
  - Araç bağlantı yönetimi
  - Kapsama alanı kontrolü (isInCoverage)

#### FlightAuthorization.java
- **Sorumluluk:** Uçuş izni yönetimi
- **Özellikler:**
  - İzin onaylama/reddetme
  - Geçerlilik süresi kontrolü
  - AuthorizationStatus enum desteği
- **Durumlar:** PENDING, APPROVED, REJECTED, EXPIRED

---

## 🎨 Tasarım Kararları

### 1. Singleton Pattern
- **TrafficControlCenter:** Sistemde tek bir kontrol merkezi olmalı
- **Neden:** Merkezi koordinasyon için gerekli

### 2. Immutability
- **Position, Route:** Waypoint listeleri defensive copy döndürür
- **Neden:** Veri bütünlüğü ve thread safety

### 3. Validation
- **Vehicle:** Hız, yükseklik, yakıt seviyesi validasyonları
- **Route:** Waypoint null kontrolü, hız limiti negatif olamaz
- **Neden:** Veri tutarlılığı ve güvenlik

### 4. Enum Kullanımı
- Tüm sabit değerler enum olarak tanımlanmış
- **Neden:** Tip güvenliği ve kod okunabilirliği

### 5. Haversine Formülü
- **Position.horizontalDistanceTo():** Dünya yüzeyi mesafe hesaplama
- **Neden:** Havacılık standartlarına uygun doğru mesafe hesaplama

### 6. Thread Safety
- **TrafficControlCenter:** ConcurrentHashMap kullanımı
- **Neden:** Çoklu thread ortamında güvenli erişim

---

## 📐 Geliştirme Prensipleri

### Dil Standartları
- **Kod ve Ekran:** İngilizce (havacılık standartlarına uygun)
- **İletişim:** Türkçe (düşünceleri en iyi şekilde aktarabilmek için)
- **Yorumlar:** İngilizce (test yorumları dahil)

### Geliştirme Yaklaşımı
- **Agile Development:** İteratif geliştirme
- **Test-Driven Development (TDD):** Test önce, kod sonra
- **Her geliştirme sonrası testlerle doğrulama**
- **Her işlemden önce onay alma**

### Test Stratejisi
- Her ünite için kapsamlı testler
- Test kapsamı:
  - Constructor testleri
  - Getter/Setter testleri
  - Business logic testleri
  - Edge case testleri
  - Exception/validation testleri
  - Immutability testleri

### Kod Standartları
- Java naming conventions
- Javadoc yorumları
- Defensive programming
- Null safety kontrolleri

---

## 📊 Mevcut Durum

### Tamamlanan İşlemler

#### ✅ Proje Kurulumu
- Bağımsız proje oluşturuldu
- Maven yapısı kuruldu
- Package yapısı: `com.airtraffic`
- 21 Java dosyası hazır

#### ✅ Model Paketi
- Position.java ✅
- Vehicle.java ✅
- Route.java ✅
- Enum'lar ✅
- **Testler:** 3 dosya, 53 test metodu ✅

#### ✅ Map Paketi
- CityMap.java ✅
- RouteNetwork.java ✅
- Obstacle.java ✅
- RestrictedZone.java ✅
- Enum'lar ✅
- **Testler:** 4 dosya, 76 test metodu ✅

#### ✅ Rules Paketi
- TrafficRuleEngine.java ✅
- TrafficRule.java ✅
- SpeedLimitRule.java ✅
- EntryExitRule.java ✅
- RuleType.java ✅
- **Testler:** 4 dosya, 84 test metodu ✅

#### ✅ Control Paketi
- TrafficControlCenter.java ✅
- BaseStation.java ✅
- FlightAuthorization.java ✅
- AuthorizationStatus.java ✅
- **Testler:** 3 dosya, 67 test metodu ✅

#### ✅ Spatial Paketi (Sprint 2)
- Quadtree.java ✅
- **Testler:** 1 dosya, 18 test metodu ✅

#### ✅ Performans Sınıfları (Sprint 2)
- AsyncProcessingService.java ✅
- BatchProcessor.java ✅
- RealTimeUpdateService.java ✅
- **Testler:** 3 dosya, 22 test metodu ✅

#### ✅ UI Paketi (Sprint 1)
- AirTrafficMainWindow.java ✅
- MapVisualization.java ✅
- VehicleListView.java ✅
- SystemStatusPanel.java ✅
- RealTimeUpdateService.java ✅
- **Testler:** 5 dosya, 42 test metodu ✅

#### ✅ Çarpışma Önleme Sistemi (Sprint 3)
- CollisionDetectionService.java ✅
- CollisionRisk.java ✅
- RiskLevel.java (enum) ✅
- TrafficControlCenter entegrasyonu ✅
- **Testler:** 2 dosya, ~40 test metodu ✅

#### ✅ ICAO Standartları (Sprint 3)
- ICAOStandardsCompliance.java ✅
- ComplianceResult.java ✅
- **Testler:** 1 dosya, 18 test metodu ✅

#### ✅ Dinamik Yükseklik Katmanları (Sprint 4 - Faz 1)
- AltitudeLayer.java (enum) ✅
- CityMap.getLayerForAltitude() ✅
- Vehicle.getCurrentLayer() ✅
- CollisionDetectionService katman entegrasyonu ✅
- ICAOStandardsCompliance katman entegrasyonu ✅
- **Testler:** 5 dosya, ~35 yeni test metodu ✅

### Test İstatistikleri
- **Backend Testleri:** 14 dosya, 280 test metodu ✅
- **UI Testleri:** 5 dosya, 42 test metodu ✅
- **Performans Testleri:** 4 dosya, 51 test metodu ✅
- **Çarpışma Önleme Testleri:** 2 dosya, ~40 test metodu ✅
- **ICAO Standartları Testleri:** 1 dosya, 18 test metodu ✅
- **Yükseklik Katmanları Testleri:** 5 dosya, ~35 test metodu ✅
- **Toplam:** 31 dosya, ~466 test metodu ✅

---

## 🚀 Gelecek Planlar

### ✅ Tamamlanan Sprint'ler

#### Sprint 1: UI Temelleri ✅
- ✅ AirTrafficMainWindow.java
- ✅ Harita görselleştirme bileşeni (MapVisualization)
- ✅ Araç listesi görüntüleme (VehicleListView)
- ✅ Sistem durumu paneli (SystemStatusPanel)
- ✅ Gerçek zamanlı güncelleme (RealTimeUpdateService)
- ✅ 42 UI testi yazıldı ve geçti

#### Sprint 2: Performans Optimizasyonu ✅
- ✅ Spatial Indexing (Quadtree) - 18 test
- ✅ Asenkron İşleme (AsyncProcessingService) - 7 test
- ✅ Batch Processing (BatchProcessor) - 7 test
- ✅ Gerçek zamanlı güncelleme servisi - 8 test
- ✅ Harita üzerinde araç görselleştirmesi - 6 test
- ✅ 51 yeni performans testi yazıldı ve geçti

#### Sprint 3: Güvenlik ve Standartlar ✅
- ✅ Çarpışma Önleme Sistemi (CollisionDetectionService) - ~40 test
- ✅ ICAO Standartları Entegrasyonu (ICAOStandardsCompliance) - 18 test
- ✅ TrafficControlCenter çarpışma kontrolü entegrasyonu
- ✅ 58 yeni test yazıldı

#### Sprint 4 Faz 1: Dinamik Yükseklik Katmanları ✅
- ✅ AltitudeLayer enum (LAYER_1_LOW, LAYER_2_MEDIUM, LAYER_3_HIGH)
- ✅ CityMap katman entegrasyonu (getLayerForAltitude)
- ✅ Vehicle katman entegrasyonu (getCurrentLayer)
- ✅ CollisionDetectionService katman entegrasyonu
- ✅ ICAOStandardsCompliance katman entegrasyonu
- ✅ ~35 yeni test yazıldı ve geçti

### ✅ Sprint 3: Güvenlik ve Standartlar - TAMAMLANDI
- ✅ US-3.1: Çarpışma önleme sistemi (8 gün)
- ✅ US-4.1: ICAO Standartları Entegrasyonu (Başlangıç, 2 gün)

### Öncelik 1: Sprint 4 - Gelişmiş Özellikler
- [ ] Çarpışma önleme sistemi geliştirmeleri
- [ ] Dinamik yükseklik katmanları
- [ ] Hava durumu entegrasyonu

### Öncelik 2: Gelişmiş Özellikler
- [ ] Dinamik yükseklik katmanları
- [ ] Hava durumu entegrasyonu
- [ ] Simülasyon modülü
- [ ] Veri kalıcılığı (JSON/XML dosya yükleme/kaydetme)

### Öncelik 3: Havacılık Standartları
- [ ] ICAO standartları entegrasyonu (devam)
- [ ] FAA uyumluluk kontrolleri
- [ ] EASA U-Space uyumluluğu
- [ ] ASTM UTM standartları

### Öncelik 4: Performans ve Güvenilirlik
- [ ] Yüksek kullanılabilirlik (HA) yapısı
- [ ] Ölçeklenebilirlik iyileştirmeleri (devam - Distributed Computing, GPU Acceleration)
- [ ] Veri güvenliği
- [ ] Loglama ve izleme

---

## 🔗 İlişkili Dosyalar

- **CHAT_GECMISI.md:** Chat geçmişi ve önemli kararlar
- **GELISTIRME_DURUMU.md:** Geliştirme durumu ve TODO listesi
- **TEST_DURUMU.md:** Test durumu ve sonuçları
- **README.md:** Proje genel bilgileri

---

## 📝 Önemli Notlar

1. **Proje Bağımsızlığı:** Proje tamamen bağımsız, ProfilAppSolution ile hiçbir bağlantı yok
2. **Workspace:** Cursor'da AirTrafficControlSystem workspace'i açık olmalı
3. **Chat Geçmişi:** Workspace değiştiğinde chat geçmişine erişilemediği için CHAT_GECMISI.md ve bu dosya oluşturuldu
4. **Context Dosyası:** Bu dosya, yeni chat oturumlarında proje durumunu anlamak için kullanılmalı

---

**Son Güncelleme:** 2025-12-13 - Sprint 1, Sprint 2, Sprint 3 ve Sprint 4 Faz 1 tamamlandı, Sprint 4 Faz 2 planlandı






