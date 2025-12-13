# Urban Air Traffic Control System - Test Stratejisi

**Proje:** UrbanAirTrafficControlSystem  
**Versiyon:** 1.0-SNAPSHOT  
**Son Güncelleme:** 2025-12-11

---

## 📋 İçindekiler

1. [Test Stratejisi Genel Bakış](#test-stratejisi-genel-bakış)
2. [Mevcut Testler ve Rolleri](#mevcut-testler-ve-rolleri)
3. [Yeni Testler Gereksinimi](#yeni-testler-gereksinimi)
4. [Sprint Bazlı Test Planı](#sprint-bazlı-test-planı)
5. [Test Kategorileri](#test-kategorileri)
6. [Test Çalıştırma Süreci](#test-çalıştırma-süreci)

---

## 🎯 Test Stratejisi Genel Bakış

### Yaklaşım: Test-Driven Development (TDD)
- **Prensip:** Test önce yazılır, sonra kod geliştirilir
- **Hedef:** Minimum %80 kod kapsamı
- **Framework:** JUnit 5.9.2
- **Strateji:** Her sprint'te hem mevcut testler çalıştırılır (regression) hem de yeni testler yazılır

---

## ✅ Mevcut Testler ve Rolleri

### Mevcut Test Kapsamı (280 Test)

#### 1. Model Paketi Testleri (53 test)
- `PositionTest.java` - Konum hesaplamaları
- `VehicleTest.java` - Araç durumu ve validasyonlar
- `RouteTest.java` - Rota hesaplamaları

**Rolü:** 
- ✅ **Regression Test:** Her sprint'te çalıştırılır
- ✅ **Core Logic Doğrulama:** Temel iş mantığının çalıştığını garanti eder
- ✅ **Breaking Change Tespiti:** Yeni geliştirmeler mevcut işlevselliği bozarsa tespit eder

#### 2. Map Paketi Testleri (76 test)
- `CityMapTest.java` - Şehir haritası işlemleri
- `ObstacleTest.java` - Engel tespiti
- `RestrictedZoneTest.java` - Yasak bölge kontrolü
- `RouteNetworkTest.java` - Rota ağı yönetimi

**Rolü:**
- ✅ **Regression Test:** Her sprint'te çalıştırılır
- ✅ **Map Logic Doğrulama:** Harita işlemlerinin doğru çalıştığını garanti eder

#### 3. Rules Paketi Testleri (84 test)
- `TrafficRuleEngineTest.java` - Kural motoru
- `SpeedLimitRuleTest.java` - Hız limiti kuralları
- `EntryExitRuleTest.java` - Giriş/çıkış kuralları
- `TrafficRuleTest.java` - Temel kural sınıfı

**Rolü:**
- ✅ **Regression Test:** Her sprint'te çalıştırılır
- ✅ **Rule Logic Doğrulama:** Kural motorunun doğru çalıştığını garanti eder

#### 4. Control Paketi Testleri (67 test)
- `TrafficControlCenterTest.java` - Merkezi kontrol
- `BaseStationTest.java` - Baz istasyonu
- `FlightAuthorizationTest.java` - Uçuş izni yönetimi

**Rolü:**
- ✅ **Regression Test:** Her sprint'te çalıştırılır
- ✅ **Control Logic Doğrulama:** Kontrol sisteminin doğru çalıştığını garanti eder

---

## 🆕 Yeni Testler Gereksinimi

### Her Sprint'te Yeni Testler Yazılacak

**Neden?**
1. **TDD Yaklaşımı:** Her yeni özellik için önce test yazılır
2. **Yeni Fonksiyonalite:** Yeni kod için yeni testler gerekli
3. **Entegrasyon Testleri:** Yeni özellikler mevcut sistemle entegre edilirken test edilmeli
4. **UI Testleri:** UI geliştirmeleri için özel testler gerekli

---

## 📅 Sprint Bazlı Test Planı

### Sprint 1: UI Temelleri

#### Mevcut Testler (Regression)
- ✅ Tüm 280 test çalıştırılacak
- ✅ Herhangi bir test başarısız olursa öncelik verilecek

#### Yeni Testler (TDD)

**US-2.1: Ana Pencere Oluşturma**
- [ ] `AirTrafficMainWindowTest.java` oluşturulacak
- [ ] Pencere açılma testi
- [ ] Menü yapısı testi
- [ ] Pencere kapatma testi
- **Tahmini:** 5-8 test metodu

**US-2.2: Harita Görselleştirme (Başlangıç)**
- [ ] `MapVisualizationTest.java` oluşturulacak
- [ ] Canvas oluşturma testi
- [ ] Harita yükleme testi
- [ ] Temel görselleştirme testi
- **Tahmini:** 6-10 test metodu

**Sprint 1 Toplam Yeni Test:** ~15-18 test metodu

---

### Sprint 2: Harita ve Araç Listesi

#### Mevcut Testler (Regression)
- ✅ Tüm 280 test + Sprint 1'deki yeni testler çalıştırılacak

#### Yeni Testler (TDD)

**US-2.2: Harita Görselleştirme (Tamamlama)**
- [ ] Harita görselleştirme testleri genişletilecek
- [ ] Zoom/Pan testleri
- [ ] Engel görselleştirme testleri
- [ ] Yasak bölge görselleştirme testleri
- **Tahmini:** 8-12 test metodu

**US-2.3: Araç Listesi Görüntüleme**
- [ ] `VehicleListTest.java` oluşturulacak
- [ ] Liste oluşturma testi
- [ ] Gerçek zamanlı güncelleme testi
- [ ] Filtreleme testi
- **Tahmini:** 6-10 test metodu

**US-2.4: Sistem Durumu Paneli**
- [ ] `SystemStatusPanelTest.java` oluşturulacak
- [ ] Durum göstergeleri testi
- [ ] İstatistik güncelleme testi
- **Tahmini:** 4-6 test metodu

**Sprint 2 Toplam Yeni Test:** ~18-28 test metodu

---

### Sprint 3: Güvenlik ve Standartlar

#### Mevcut Testler (Regression)
- ✅ Tüm önceki testler çalıştırılacak

#### Yeni Testler (TDD)

**US-3.1: Çarpışma Önleme Sistemi**
- [ ] `CollisionAvoidanceTest.java` oluşturulacak
- [ ] Çarpışma tespit algoritması testleri
- [ ] Uyarı sistemi testleri
- [ ] Otomatik rota düzeltme testleri
- **Tahmini:** 15-20 test metodu

**US-4.1: ICAO Standartları (Başlangıç)**
- [ ] `ICAOComplianceTest.java` oluşturulacak
- [ ] Standart uyumluluk testleri
- **Tahmini:** 5-8 test metodu

**Sprint 3 Toplam Yeni Test:** ~20-28 test metodu

---

## 🧪 Test Kategorileri

### 1. Unit Testler (Mevcut 280 test)
- **Kapsam:** Tek bir sınıf/metod test edilir
- **Hız:** Çok hızlı
- **Bağımlılık:** Minimal (mock kullanılabilir)
- **Örnek:** `PositionTest.testHorizontalDistance()`

### 2. Integration Testler (Yeni)
- **Kapsam:** Birden fazla sınıf birlikte test edilir
- **Hız:** Orta
- **Bağımlılık:** Gerçek bağımlılıklar
- **Örnek:** `TrafficControlCenter` + `CityMap` + `Vehicle` entegrasyonu

### 3. UI Testler (Yeni - Sprint 1'den itibaren)
- **Kapsam:** JavaFX UI bileşenleri test edilir
- **Hız:** Yavaş (UI render gerektirir)
- **Bağımlılık:** JavaFX Application Thread
- **Framework:** JUnit 5 + TestFX (önerilir)
- **Örnek:** `AirTrafficMainWindowTest.testWindowOpens()`

### 4. End-to-End Testler (Gelecek)
- **Kapsam:** Tüm sistem akışı test edilir
- **Hız:** Çok yavaş
- **Bağımlılık:** Tüm sistem
- **Örnek:** Kullanıcı araç ekler → Rota planlar → Uçuş izni alır → Trafiğe girer

---

## 🔄 Test Çalıştırma Süreci

### Her Sprint'te

#### 1. Sprint Başında
- [ ] Mevcut tüm testler çalıştırılır (regression)
- [ ] Başarısız testler varsa öncelik verilir
- [ ] Yeni User Story için test planı yapılır

#### 2. Geliştirme Sırasında (TDD)
- [ ] Önce test yazılır (Red)
- [ ] Sonra kod yazılır (Green)
- [ ] Kod refactor edilir (Refactor)
- [ ] Test geçene kadar tekrarlanır

#### 3. Sprint Sonunda
- [ ] Tüm testler çalıştırılır (regression + yeni)
- [ ] Test kapsamı kontrol edilir
- [ ] Test sonuçları dokümante edilir

---

## 📊 Test Metrikleri

### Hedefler
- **Kod Kapsamı:** Minimum %80
- **Test Başarı Oranı:** %100
- **Regression Test Süresi:** < 5 dakika
- **Yeni Test/Sprint:** 15-30 test metodu

### Takip
- Her sprint sonunda metrikler güncellenir
- Test kapsamı raporu oluşturulur
- Başarısız testler analiz edilir

---

## 🛠️ Test Araçları

### Mevcut
- ✅ **JUnit 5.9.2** - Test framework
- ✅ **Maven Surefire** - Test çalıştırma

### Önerilen (UI Testleri için)
- [ ] **TestFX** - JavaFX UI test framework
- [ ] **Mockito** - Mock framework (gerekirse)
- [ ] **JaCoCo** - Kod kapsamı analizi

---

## 📝 Test Yazma Standartları

### Naming Convention
- Test sınıfı: `[ClassName]Test.java`
- Test metodu: `test[MethodName]()` veya `@DisplayName` kullanılır

### Test Yapısı
```java
@Test
@DisplayName("Test [feature description]")
void test[Feature]() {
    // Arrange (Hazırlık)
    // Act (Eylem)
    // Assert (Doğrulama)
}
```

### Best Practices
- ✅ Her test bağımsız olmalı
- ✅ Test isimleri açıklayıcı olmalı
- ✅ Edge case'ler test edilmeli
- ✅ Exception durumları test edilmeli
- ✅ Test yorumları İngilizce olmalı

---

## ✅ Özet

### Mevcut Testler (280 test)
- ✅ **Her sprint'te çalıştırılır** (regression test)
- ✅ **Yeterli mi?** Backend/core logic için evet
- ✅ **Yeni geliştirmeler için?** Hayır, yeni testler gerekli

### Yeni Testler
- 🆕 **Her sprint'te yazılacak** (TDD yaklaşımı)
- 🆕 **Yeni özellikler için gerekli**
- 🆕 **UI testleri Sprint 1'den itibaren başlayacak**

### Test Stratejisi
1. **Sprint Başında:** Mevcut testler çalıştırılır (regression)
2. **Geliştirme Sırasında:** Yeni testler yazılır (TDD)
3. **Sprint Sonunda:** Tüm testler çalıştırılır (regression + yeni)

---

**Not:** Bu test stratejisi, proje geliştirme sürecinde güncellenecektir. Yeni test kategorileri veya araçlar eklendikçe bu dosya revize edilecektir.

