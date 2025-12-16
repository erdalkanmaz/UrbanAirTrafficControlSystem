# Yol Bazlı Katman Organizasyonu - Detaylı Tartışma ve Analiz

**Tarih:** 2025-12-16  
**Durum:** Öneri Aşaması - Tartışma ve Değerlendirme

---

## 📋 Önerilen Mimari

### Ana Yollar - İki Katmanlı Sistem

**Katman 1: Doğu-Batı İstikameti**
- Gidiş yönü: Doğu → Batı (örn: 100m yükseklik)
- Geliş yönü: Batı → Doğu (örn: 105m yükseklik)
- Yüksek hız (örn: 25-30 m/s)
- Yüksek kapasite

**Katman 2: Güney-Kuzey İstikameti**
- Gidiş yönü: Güney → Kuzey (örn: 110m yükseklik)
- Geliş yönü: Kuzey → Güney (örn: 115m yükseklik)
- Yüksek hız (örn: 25-30 m/s)
- Yüksek kapasite

**Önemli:** Ana yollar hiçbir zaman aynı seviyede kesişmeyecek.

### Tali Yollar - Tek Katmanlı Sistem

**Tali Yol Katmanı:**
- Tüm tali yollar aynı seviyede (örn: 80m yükseklik)
- Tüm araç tipleri için aynı seviye (kargo, insan taşıma)
- Düşük hız (örn: 10-15 m/s)
- Kesişme, durma, kalkma manevraları mümkün
- Ana yollardan inen araçlar bu katmana geçer

---

## ✅ Avantajlar

### 1. Basitlik ve Anlaşılabilirlik

**Avantaj:**
- ✅ Mimari açık ve anlaşılır
- ✅ Operatörler için kolay yönetim
- ✅ Araçlar için net kurallar
- ✅ Karmaşıklık azalır

**Açıklama:**
- Ana yollar için sadece 2 katman (doğu-batı, güney-kuzey)
- Tali yollar için tek katman
- Toplam 3 ana katman (2 ana yol + 1 tali yol)

### 2. Kesişme Probleminin Çözümü

**Avantaj:**
- ✅ Ana yollar farklı katmanlarda → kesişme yok
- ✅ Tali yollarda kesişme var ama düşük hız → güvenli
- ✅ Karmaşık kesişim yönetimi gerekmez

**Açıklama:**
- Doğu-batı yolları 100-105m
- Güney-kuzey yolları 110-115m
- Fiziksel kesişme yok, sadece dikey geçiş

### 3. Trafik Akışı Optimizasyonu

**Avantaj:**
- ✅ Ana yollarda yüksek hız → hızlı ulaşım
- ✅ Tali yollarda düşük hız → güvenli manevra
- ✅ Hız farkı → trafik akışı optimize

**Açıklama:**
- Ana yollar: 25-30 m/s (90-108 km/h)
- Tali yollar: 10-15 m/s (36-54 km/h)
- Hız farkı manevra için zaman sağlar

### 4. Esneklik

**Avantaj:**
- ✅ Tali yollarda durma/kalkma mümkün
- ✅ Kesişme manevraları yapılabilir
- ✅ Farklı araç tipleri aynı katmanda

**Açıklama:**
- Kargo ve insan taşıma araçları tali yollarda aynı seviyede
- Düşük hız sayesinde güvenli manevra

### 5. Kapasite Yönetimi

**Avantaj:**
- ✅ Ana yollarda yüksek kapasite (hızlı geçiş)
- ✅ Tali yollarda kontrollü kapasite (düşük hız)
- ✅ Trafik dağılımı optimize

---

## ⚠️ Potansiyel Sorunlar ve Zorluklar

### 1. Geçiş Yönetimi (Ana Yol ↔ Tali Yol)

**Sorun:**
- Ana yoldan tali yola geçiş: 100m → 80m (20m alçalma)
- Tali yoldan ana yola geçiş: 80m → 100m (20m yükselme)
- Geçiş sırasında çarpışma riski artar

**Zorluklar:**
- Geçiş noktalarında trafik sıkışıklığı
- Geçiş sırasında hız değişimi (25 m/s → 10 m/s)
- Geçiş sırasında yükseklik değişimi
- Çoklu geçiş senaryoları (aynı anda birden fazla araç)

**Çözüm Önerileri:**
1. **Geçiş Bölgeleri:**
   - Geçiş için özel bölgeler tanımla
   - Geçiş sırasında özel kontrol
   - Geçiş öncelik sırası

2. **Geçiş Kuralları:**
   - Önce hız azalt (25 → 15 → 10 m/s)
   - Sonra yükseklik değiştir (100m → 80m)
   - Geçiş tamamlanana kadar özel izleme

3. **Geçiş Kapasitesi:**
   - Aynı anda maksimum X araç geçiş yapabilir
   - Kuyruk yönetimi

### 2. Tali Yollarda Kesişme Yönetimi

**Sorun:**
- Tüm tali yollar aynı seviyede → kesişme var
- Düşük hız güvenliği artırır ama yeterli mi?
- Kesişme noktalarında öncelik kuralları gerekir

**Zorluklar:**
- Kesişme noktalarında çarpışma riski
- Öncelik kuralları (kim önce geçer?)
- Trafik sıkışıklığı (kesişme noktalarında)
- Acil durum senaryoları

**Çözüm Önerileri:**
1. **Kesişme Kontrolü:**
   - Kesişme noktalarında otomatik kontrol
   - Öncelik kuralları (sağdan gelen, dönüş yapan, vb.)
   - Dur-kalk sinyalleri

2. **Hız Limitleri:**
   - Kesişme yaklaşımında hız daha da azaltılabilir (10 → 5 m/s)
   - Kesişme geçişinde minimum hız

3. **Görsel İşaretler:**
   - Kesişme noktalarında görsel uyarılar
   - Araçlar arası iletişim

### 3. Ana Yol Yön Belirleme

**Sorun:**
- Doğu-batı yolları: Hangi yön doğu, hangi yön batı?
- Güney-kuzey yolları: Hangi yön güney, hangi yön kuzey?
- Yol segmenti bazında yön belirleme gerekir

**Zorluklar:**
- Yol segmenti başlangıç/bitiş noktalarına göre yön belirleme
- Yol segmenti yönü değişirse (örn: kuzey-doğu yönünde)
- Karmaşık yol ağında yön belirleme

**Çözüm Önerileri:**
1. **Segment Yönü:**
   - Her segment için başlangıç → bitiş yönü
   - Bearing (açı) hesaplama ile yön belirleme
   - 0-180° → Doğu-Batı katmanı
   - 90-270° → Güney-Kuzey katmanı

2. **Yön Toleransı:**
   - Tam doğu-batı değil, ±30° tolerans
   - Örn: 330°-30° → Doğu-Batı
   - Örn: 60°-120° → Güney-Kuzey

### 4. Tali Yollarda Araç Karışımı

**Sorun:**
- Kargo ve insan taşıma araçları aynı katmanda
- Farklı hız gereksinimleri olabilir
- Farklı öncelik seviyeleri (acil durum araçları)

**Zorluklar:**
- Hız farkı → çarpışma riski
- Öncelik çatışması
- Trafik akışı optimizasyonu

**Çözüm Önerileri:**
1. **Hız Standardizasyonu:**
   - Tali yollarda tüm araçlar aynı hızda (örn: 12 m/s)
   - Hız limiti zorunlu

2. **Öncelik Kuralları:**
   - Acil durum araçları öncelikli
   - Öncelikli araçlar için özel geçiş kuralları

### 5. Geçiş Sırasında Çarpışma Riski

**Sorun:**
- Ana yoldan tali yola geçiş sırasında çarpışma riski
- Tali yoldan ana yola geçiş sırasında çarpışma riski
- Geçiş sırasında araç hem yükseklik hem hız değiştiriyor

**Zorluklar:**
- Geçiş sırasında kontrol kaybı riski
- Çoklu geçiş senaryoları
- Geçiş sırasında çarpışma tespiti

**Çözüm Önerileri:**
1. **Geçiş Öncesi Kontrol:**
   - Geçiş öncesi çarpışma riski kontrolü
   - Geçiş için güvenli alan kontrolü
   - Geçiş onayı

2. **Geçiş Sırası İzleme:**
   - Geçiş sırasında sürekli izleme
   - Geçiş sırasında çarpışma kontrolü artırılır
   - Geçiş tamamlanana kadar özel kontrol

3. **Geçiş Hızı:**
   - Geçiş sırasında yavaş geçiş (örn: 5 m/s)
   - Geçiş tamamlandıktan sonra hız artırılır

---

## 🔄 Alternatif Yaklaşımlar

### Alternatif 1: Tali Yollar İçin Ayrı Katmanlar

**Öneri:**
- Kargo tali yolları: 75m
- İnsan taşıma tali yolları: 80m
- Acil durum tali yolları: 85m

**Avantajlar:**
- ✅ Araç tipleri ayrı katmanlarda
- ✅ Hız farkı sorunu yok
- ✅ Öncelik yönetimi kolay

**Dezavantajlar:**
- ❌ Daha fazla katman → karmaşıklık
- ❌ Geçiş yönetimi daha zor
- ❌ Kesişme yönetimi daha zor

### Alternatif 2: Tali Yollar İçin Hız Bazlı Organizasyon

**Öneri:**
- Tali yollarda hız bazlı şeritler (yükseklik farkı yok, sadece hız)
- Hızlı şerit: 15 m/s
- Yavaş şerit: 10 m/s

**Avantajlar:**
- ✅ Tek katman → basit
- ✅ Hız farkı yönetimi

**Dezavantajlar:**
- ❌ Şerit yönetimi gerekir
- ❌ Şerit değiştirme riski

### Alternatif 3: Kesişme Noktalarında Özel Yönetim

**Öneri:**
- Tali yollarda kesişme noktalarında özel yönetim
- Kesişme noktalarında dur-kalk veya öncelik kuralları
- Kesişme dışında normal akış

**Avantajlar:**
- ✅ Basit yapı
- ✅ Kesişme noktalarında kontrollü geçiş

**Dezavantajlar:**
- ❌ Kesişme noktalarında trafik sıkışıklığı
- ❌ Dur-kalk manevraları → enerji kaybı

---

## 🎯 Önerilen Çözüm (Tartışma İçin)

### Mimari Tasarım

**1. Katman Yapısı:**
```
LAYER_MAIN_EW_FORWARD:  100m (Doğu-Batı, Gidiş)
LAYER_MAIN_EW_REVERSE:  105m (Doğu-Batı, Geliş)
LAYER_MAIN_NS_FORWARD:  110m (Güney-Kuzey, Gidiş)
LAYER_MAIN_NS_REVERSE:  115m (Güney-Kuzey, Geliş)
LAYER_SECONDARY:        80m  (Tali Yollar, Tüm Yönler)
```

**2. Geçiş Bölgeleri:**
- Ana yol → Tali yol: Özel geçiş bölgesi (95m-85m arası)
- Tali yol → Ana yol: Özel geçiş bölgesi (85m-95m arası)
- Geçiş sırasında: 5 m/s hız, sürekli izleme

**3. Kesişme Yönetimi:**
- Tali yollarda kesişme noktalarında: 5 m/s hız limiti
- Öncelik kuralları: Sağdan gelen, dönüş yapan, acil durum
- Otomatik çarpışma kontrolü

**4. Hız Yönetimi:**
- Ana yollar: 25-30 m/s
- Tali yollar: 10-15 m/s
- Geçiş bölgeleri: 5 m/s
- Kesişme noktaları: 5 m/s

---

## ❓ Tartışma Soruları

1. **Geçiş Yönetimi:**
   - Geçiş bölgeleri yeterli mi?
   - Geçiş sırasında hız/yükseklik değişimi sırası doğru mu?
   - Geçiş kapasitesi nasıl yönetilmeli?

2. **Kesişme Yönetimi:**
   - Tali yollarda kesişme güvenli mi?
   - Öncelik kuralları yeterli mi?
   - Dur-kalk manevraları kabul edilebilir mi?

3. **Hız Yönetimi:**
   - Tali yollarda tek hız yeterli mi?
   - Farklı araç tipleri için farklı hız gerekir mi?

4. **Kapasite Yönetimi:**
   - Tali yollarda kapasite nasıl yönetilmeli?
   - Geçiş bölgelerinde kapasite nasıl yönetilmeli?

5. **Acil Durum:**
   - Acil durum araçları için özel kurallar gerekir mi?
   - Acil durum araçları tali yollarda öncelikli mi?

---

## 📋 Sonraki Adımlar

1. **Tartışma:**
   - Bu dokümantasyon üzerinde tartışma
   - Soruların cevaplanması
   - Alternatiflerin değerlendirilmesi

2. **Karar:**
   - Final mimari kararı
   - Detaylı tasarım dokümantasyonu

3. **Implementasyon:**
   - Sprint 4 Faz 2 planlaması
   - Kod geliştirme

---

**Not:** Bu dokümantasyon, yol bazlı katman organizasyonu için tartışma ve karar verme sürecinde referans olarak kullanılacaktır.

