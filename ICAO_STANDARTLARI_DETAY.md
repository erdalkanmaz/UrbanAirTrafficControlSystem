# ICAO Standartları Uyumluluğu - Detaylı Açıklama

**Tarih:** 2025-12-16  
**Sprint:** Sprint 3 - ICAO Standartları Entegrasyonu

---

## 📋 ICAO Standartları Nedir?

**ICAO (International Civil Aviation Organization)** - Uluslararası Sivil Havacılık Örgütü, havacılık güvenliği ve standartları için uluslararası kurallar belirler.

### Kullanılan Standart: ICAO Annex 2 - Rules of the Air

Sistemimizde **ICAO Annex 2: Rules of the Air** standartlarına uyumluluk kontrolü yapılmaktadır.

---

## 🔍 Kontrol Edilen Kriterler ve Veriler

### 1. Minimum Separation Standartları (Minimum Ayrım Standartları)

**Kontrol Edilen Veriler:**
- Araçların konumları (Position: latitude, longitude, altitude)
- İki araç arasındaki yatay mesafe
- İki araç arasındaki dikey mesafe

**Kriterler:**
- **Yatay Minimum Mesafe:** 50 metre
  - İki araç arasındaki yatay (lat/lon) mesafe en az 50m olmalı
  - Haversine formülü ile hesaplanır
  
- **Dikey Minimum Mesafe:** 10 metre
  - İki araç arasındaki yükseklik farkı en az 10m olmalı
  - `altitude1 - altitude2` mutlak değeri

**Kod Konumu:**
```java
// src/main/java/com/airtraffic/standards/ICAOStandardsCompliance.java
// Satır 24-26: Sabitler
private static final double MIN_HORIZONTAL_SEPARATION = 50.0; // metre
private static final double MIN_VERTICAL_SEPARATION = 10.0;   // metre

// Satır 54-114: checkSeparationStandards() metodu
```

**Nasıl Kontrol Edilir:**
1. `checkSeparationStandards(vehicle1, vehicle2)` metodu çağrılır
2. İki aracın konumları alınır
3. Yatay ve dikey mesafeler hesaplanır
4. Minimum değerlerle karşılaştırılır
5. İhlal varsa `ComplianceResult` içinde ihlal ve öneri mesajları döndürülür

**Örnek Kullanım:**
```java
ICAOStandardsCompliance compliance = new ICAOStandardsCompliance();
ComplianceResult result = compliance.checkSeparationStandards(vehicle1, vehicle2);

if (!result.isCompliant()) {
    // İhlaller var
    for (String violation : result.getViolations()) {
        System.out.println("İhlal: " + violation);
    }
    for (String recommendation : result.getRecommendations()) {
        System.out.println("Öneri: " + recommendation);
    }
}
```

---

### 2. Uçuş Kuralları Uyumluluğu (VFR/IFR Compliance)

**Kontrol Edilen Veriler:**
- Araç durumu (VehicleStatus: IN_FLIGHT, TAKING_OFF, vb.)
- Araç konumu (Position)
- Araç yüksekliği (altitude)
- Araç hızı (velocity)
- Konumun güvenli olup olmadığı (engeller, yasak bölgeler)
- Yükseklik katmanı uygunluğu

**Kriterler:**
- **VFR (Visual Flight Rules) Temel Kontrolleri:**
  - Yükseklik negatif olamaz
  - Hız negatif olamaz
  - Araç durumu IN_FLIGHT veya TAKING_OFF olmalı
  - Konum güvenli olmalı (engel içinde, yasak bölgede veya sınırlar dışında olmamalı)
  - Araç geçerli bir yükseklik katmanında olmalı

**Kod Konumu:**
```java
// src/main/java/com/airtraffic/standards/ICAOStandardsCompliance.java
// Satır 132-186: checkFlightRulesCompliance() metodu
```

**Nasıl Kontrol Edilir:**
1. `checkFlightRulesCompliance(vehicle, cityMap)` metodu çağrılır
2. Araç durumu kontrol edilir
3. Konum güvenliği kontrol edilir (CityMap varsa)
4. Yükseklik katmanı kontrol edilir (CityMap varsa)
5. Temel VFR kuralları kontrol edilir (yükseklik, hız)
6. İhlal varsa `ComplianceResult` içinde ihlal ve öneri mesajları döndürülür

---

### 3. İletişim Gereksinimleri (Communication Requirements)

**Kontrol Edilen Veriler:**
- Araç konumu
- Baz istasyonu konumları
- Araç-baz istasyonu mesafesi

**Kriterler:**
- **Minimum İletişim Menzili:** 5 km (5000 metre)
  - Araç, en az bir baz istasyonunun 5km menzili içinde olmalı
  - İletişim kurulamazsa uçuş izni verilmemeli

**Kod Konumu:**
```java
// src/main/java/com/airtraffic/standards/ICAOStandardsCompliance.java
// Satır 29: Sabit
private static final double COMMUNICATION_RANGE = 5000.0; // 5 km

// Satır 194-218: validateCommunicationRequirements() metodu
```

**Nasıl Kontrol Edilir:**
1. `validateCommunicationRequirements(vehicle, baseStationPositions)` metodu çağrılır
2. Araç konumu alınır
3. Her baz istasyonu için mesafe hesaplanır
4. En az bir baz istasyonu 5km içindeyse `true`, değilse `false` döndürülür

---

## 🔄 Otomatik Kontrol Mekanizması

### Ne Zaman Kontrol Edilir?

**1. Araç Kaydı Sırasında:**
- `TrafficControlCenter.registerVehicle()` çağrıldığında
- Uçuş izni kontrolü yapılır (FlightAuthorization)
- İletişim gereksinimleri kontrol edilir

**2. Konum Güncellemesi Sırasında:**
- `TrafficControlCenter.updateVehiclePosition()` çağrıldığında
- Çarpışma kontrolü otomatik yapılır (CollisionDetectionService)
- Separation standartları kontrol edilir (ICAOStandardsCompliance)

**3. Manuel Kontrol:**
- Operatör istediğinde `checkSeparationStandards()` veya `checkFlightRulesCompliance()` çağrılabilir
- Tüm araçlar için toplu kontrol: `checkAllSeparationStandards(vehicles)`

### Arka Planda Otomatik Kontrol

**Evet, arka planda otomatik kontrol yapılıyor:**

1. **Çarpışma Kontrolü:**
   - Her konum güncellemesinde otomatik çalışır
   - `CollisionDetectionService` kullanılır
   - Kritik riskler loglanır ve uyarı verilir

2. **Separation Kontrolü:**
   - Çarpışma kontrolü içinde dolaylı olarak yapılır
   - Minimum mesafe standartları çarpışma riski hesaplamasında kullanılır

3. **Uçuş Kuralları Kontrolü:**
   - Araç kaydı sırasında yapılır
   - Konum güncellemesi sırasında güvenlik kontrolü yapılır

---

## 📊 Test Edilen Senaryolar

**Test Dosyası:** `src/test/java/com/airtraffic/standards/ICAOStandardsComplianceTest.java`

**Test Edilen Senaryolar:**
1. ✅ Geçerli separation (50m+ yatay, 10m+ dikey)
2. ✅ Yatay separation ihlali (< 50m)
3. ✅ Dikey separation ihlali (< 10m)
4. ✅ Farklı katmanlardaki araçlar (60m+ dikey mesafe yeterli)
5. ✅ Uçuş kuralları uyumluluğu (VFR)
6. ✅ İletişim gereksinimleri (5km menzil)
7. ✅ Null konum kontrolü
8. ✅ Yasak bölge kontrolü
9. ✅ Engel kontrolü
10. ✅ Yükseklik katmanı kontrolü

**Toplam Test:** 23 test, hepsi geçti ✅

---

## 📝 ComplianceResult Yapısı

Kontrol sonuçları `ComplianceResult` sınıfında saklanır:

```java
public class ComplianceResult {
    private boolean isCompliant;        // Uyumlu mu?
    private List<String> violations;    // İhlal edilen kurallar
    private List<String> recommendations; // Öneriler
    private String standardName;         // "ICAO Annex 2"
}
```

**Örnek Çıktı:**
```
ComplianceResult {
    isCompliant: false
    violations: [
        "Horizontal separation violation: 30.50 m < 50.00 m (minimum required)",
        "Vertical separation violation: 5.00 m < 10.00 m (minimum required)"
    ]
    recommendations: [
        "Increase horizontal separation distance",
        "Adjust altitude to maintain minimum vertical separation"
    ]
    standardName: "ICAO Annex 2"
}
```

---

## 🎯 Sonuç

**ICAO standartları uyumluluğu:**
- ✅ **Otomatik kontrol yapılıyor** - Her konum güncellemesinde
- ✅ **Manuel kontrol mümkün** - İstenildiğinde çağrılabilir
- ✅ **Detaylı raporlama** - İhlaller ve öneriler listeleniyor
- ✅ **Test edilmiş** - 23 test ile doğrulanmış

**Kontrol Edilen Veriler:**
- Araç konumları (lat, lon, altitude)
- Araç durumları (status)
- Araç hızları (velocity)
- Baz istasyonu konumları
- Şehir haritası (engeller, yasak bölgeler)
- Yükseklik katmanları

**Kontrol Edilen Kriterler:**
- Minimum separation standartları (50m yatay, 10m dikey)
- VFR uçuş kuralları
- İletişim gereksinimleri (5km menzil)
- Güvenlik kontrolleri (engeller, yasak bölgeler)

---

**Not:** Bu kontroller gerçek zamanlı olarak yapılmaktadır ve sistem operatörüne raporlanmaktadır. Kritik ihlaller loglanır ve uyarı verilir.

