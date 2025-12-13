# Urban Air Traffic Control System - İş Kırılım Yapısı (WBS)

**Proje:** UrbanAirTrafficControlSystem  
**Versiyon:** 1.0-SNAPSHOT  
**Son Güncelleme:** 2025-12-11

---

## 📊 WBS Hiyerarşisi

```
AirTrafficControlSystem
├── 1. Proje Yönetimi ve Planlama
│   ├── 1.1 Proje Kurulumu ✅
│   ├── 1.2 Dokümantasyon ✅
│   ├── 1.3 Test Stratejisi ✅
│   └── 1.4 Agile Yapısı Kurulumu ⏳
│
├── 2. Temel Altyapı (Epic 1) ✅ TAMAMLANDI
│   ├── 2.1 Model Paketi ✅
│   │   ├── 2.1.1 Position.java ✅
│   │   ├── 2.1.2 Vehicle.java ✅
│   │   ├── 2.1.3 Route.java ✅
│   │   └── 2.1.4 Enum'lar ✅
│   │
│   ├── 2.2 Map Paketi ✅
│   │   ├── 2.2.1 CityMap.java ✅
│   │   ├── 2.2.2 Obstacle.java ✅
│   │   ├── 2.2.3 RestrictedZone.java ✅
│   │   ├── 2.2.4 RouteNetwork.java ✅
│   │   └── 2.2.5 Enum'lar ✅
│   │
│   ├── 2.3 Rules Paketi ✅
│   │   ├── 2.3.1 TrafficRule.java ✅
│   │   ├── 2.3.2 TrafficRuleEngine.java ✅
│   │   ├── 2.3.3 SpeedLimitRule.java ✅
│   │   ├── 2.3.4 EntryExitRule.java ✅
│   │   └── 2.3.5 RuleType.java ✅
│   │
│   ├── 2.4 Control Paketi ✅
│   │   ├── 2.4.1 TrafficControlCenter.java ✅
│   │   ├── 2.4.2 BaseStation.java ✅
│   │   ├── 2.4.3 FlightAuthorization.java ✅
│   │   └── 2.4.4 AuthorizationStatus.java ✅
│   │
│   └── 2.5 Test Altyapısı ✅
│       ├── 2.5.1 Model Testleri ✅ (53 test)
│       ├── 2.5.2 Map Testleri ✅ (76 test)
│       ├── 2.5.3 Rules Testleri ✅ (84 test)
│       └── 2.5.4 Control Testleri ✅ (67 test)
│
├── 3. Kullanıcı Arayüzü (Epic 2) ⏳ PLANLANDI
│   ├── 3.1 Ana Pencere (US-2.1)
│   │   ├── 3.1.1 AirTrafficMainWindow.java
│   │   ├── 3.1.2 Menü Yapısı
│   │   └── 3.1.3 Pencere Yönetimi
│   │
│   ├── 3.2 Harita Görselleştirme (US-2.2)
│   │   ├── 3.2.1 Harita Canvas
│   │   ├── 3.2.2 Şehir Sınırları Görselleştirme
│   │   ├── 3.2.3 Engel Görselleştirme
│   │   ├── 3.2.4 Yasak Bölge Görselleştirme
│   │   └── 3.2.5 Zoom ve Pan Özellikleri
│   │
│   ├── 3.3 Araç Listesi (US-2.3)
│   │   ├── 3.3.1 Araç Tablosu
│   │   ├── 3.3.2 Gerçek Zamanlı Güncelleme
│   │   └── 3.3.3 Filtreleme Özellikleri
│   │
│   └── 3.4 Sistem Durumu Paneli (US-2.4)
│       ├── 3.4.1 Durum Göstergeleri
│       ├── 3.4.2 İstatistikler
│       └── 3.4.3 Alarm Sistemi
│
├── 4. Gelişmiş Özellikler (Epic 3) ⏳ PLANLANDI
│   ├── 4.1 Çarpışma Önleme (US-3.1)
│   │   ├── 4.1.1 Çarpışma Tespit Algoritması
│   │   ├── 4.1.2 Uyarı Sistemi
│   │   └── 4.1.3 Otomatik Rota Düzeltme
│   │
│   ├── 4.2 Dinamik Yükseklik Katmanları (US-3.2)
│   │   ├── 4.2.1 Katman Yönetimi
│   │   ├── 4.2.2 Otomatik Atama
│   │   └── 4.2.3 Kapasite Kontrolü
│   │
│   ├── 4.3 Hava Durumu Entegrasyonu (US-3.3)
│   │   ├── 4.3.1 API Entegrasyonu
│   │   ├── 4.3.2 Veri Saklama
│   │   └── 4.3.3 Rota Planlamada Kullanım
│   │
│   ├── 4.4 Simülasyon Modülü (US-3.4)
│   │   ├── 4.4.1 Simülasyon Motoru
│   │   ├── 4.4.2 Senaryo Tanımlama
│   │   └── 4.4.3 Gerçek Zamanlı Simülasyon
│   │
│   └── 4.5 Veri Kalıcılığı (US-3.5)
│       ├── 4.5.1 JSON Export/Import
│       ├── 4.5.2 XML Export/Import
│       └── 4.5.3 Veri Yönetimi
│
├── 5. Havacılık Standartları (Epic 4) ⏳ PLANLANDI
│   ├── 5.1 ICAO Standartları (US-4.1)
│   │   ├── 5.1.1 Standart Dokümantasyonu
│   │   └── 5.1.2 Uyumluluk Kontrolleri
│   │
│   ├── 5.2 FAA Uyumluluğu (US-4.2)
│   │   ├── 5.2.1 Standart Dokümantasyonu
│   │   └── 5.2.2 Uyumluluk Kontrolleri
│   │
│   ├── 5.3 EASA U-Space (US-4.3)
│   │   ├── 5.3.1 Standart Dokümantasyonu
│   │   └── 5.3.2 Uyumluluk Kontrolleri
│   │
│   └── 5.4 ASTM UTM (US-4.4)
│       ├── 5.4.1 Standart Dokümantasyonu
│       └── 5.4.2 Uyumluluk Kontrolleri
│
└── 6. Performans ve Güvenilirlik (Epic 5) ⏳ PLANLANDI
    ├── 6.1 Yüksek Kullanılabilirlik (US-5.1)
    │   ├── 6.1.1 HA Mimarisi
    │   ├── 6.1.2 Failover Mekanizması
    │   └── 6.1.3 Yedekleme Sistemi
    │
    ├── 6.2 Ölçeklenebilirlik (US-5.2)
    │   ├── 6.2.1 Performans Analizi
    │   ├── 6.2.2 Bottleneck Tespiti
    │   └── 6.2.3 Optimizasyonlar
    │
    ├── 6.3 Veri Güvenliği (US-5.3)
    │   ├── 6.3.1 Şifreleme
    │   └── 6.3.2 Erişim Kontrolü
    │
    └── 6.4 Loglama ve İzleme (US-5.4)
        ├── 6.4.1 Loglama Sistemi
        ├── 6.4.2 İzleme Dashboard'u
        └── 6.4.3 Alarm Sistemi
```

---

## 📈 İlerleme Durumu

### Tamamlanan İşler ✅
- **2. Temel Altyapı:** %100 tamamlandı
  - Model Paketi: ✅
  - Map Paketi: ✅
  - Rules Paketi: ✅
  - Control Paketi: ✅
  - Test Altyapısı: ✅ (280 test)

### Planlanan İşler ⏳
- **3. Kullanıcı Arayüzü:** %0 (Planlandı)
- **4. Gelişmiş Özellikler:** %0 (Planlandı)
- **5. Havacılık Standartları:** %0 (Planlandı)
- **6. Performans ve Güvenilirlik:** %0 (Planlandı)

---

## 🎯 Öncelik Sıralaması

1. **Yüksek Öncelik:**
   - 3. Kullanıcı Arayüzü (Epic 2)
   - 5.1 ICAO Standartları
   - 5.3 EASA U-Space
   - 4.1 Çarpışma Önleme

2. **Orta Öncelik:**
   - 4.2 Dinamik Yükseklik Katmanları
   - 4.3 Hava Durumu Entegrasyonu
   - 4.5 Veri Kalıcılığı
   - 5.2 FAA Uyumluluğu
   - 5.4 ASTM UTM
   - 6.2 Ölçeklenebilirlik
   - 6.4 Loglama ve İzleme

3. **Düşük Öncelik:**
   - 4.4 Simülasyon Modülü
   - 6.1 Yüksek Kullanılabilirlik

---

## 📊 Tahmini Süreler

### Epic 2: UI Geliştirme
- **Toplam:** ~12 gün
- US-2.1: 2 gün
- US-2.2: 5 gün
- US-2.3: 3 gün
- US-2.4: 2 gün

### Epic 3: Gelişmiş Özellikler
- **Toplam:** ~33 gün
- US-3.1: 8 gün
- US-3.2: 5 gün
- US-3.3: 6 gün
- US-3.4: 10 gün
- US-3.5: 4 gün

### Epic 4: Havacılık Standartları
- **Toplam:** ~29 gün
- US-4.1: 7 gün
- US-4.2: 7 gün
- US-4.3: 8 gün
- US-4.4: 7 gün

### Epic 5: Performans ve Güvenilirlik
- **Toplam:** ~28 gün
- US-5.1: 10 gün
- US-5.2: 8 gün
- US-5.3: 6 gün
- US-5.4: 4 gün

**Toplam Tahmini Süre:** ~102 gün (yaklaşık 5 ay, 2 haftalık sprint'lerle)

---

**Not:** Bu WBS, proje geliştirme sürecinde güncellenecektir. Yeni ihtiyaçlar eklendikçe veya öncelikler değiştikçe bu dosya revize edilecektir.

