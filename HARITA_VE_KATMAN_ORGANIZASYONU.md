# Harita Görselleştirmesi ve Yol Bazlı Katman Organizasyonu

**Tarih:** 2025-12-16  
**Durum:** Planlama ve Değerlendirme Aşaması

---

## 🗺️ Mevcut Harita Görselleştirmesi

### Şu Anki Durum

**Mevcut Görselleştirme:**
- JavaFX Canvas kullanılıyor
- Basit geometrik şekiller (daireler, çizgiler)
- Koordinat sistemi: Lat/Lon → Pixel dönüşümü
- Araçlar nokta olarak gösteriliyor
- Engeller ve yasak bölgeler renkli alanlar olarak gösteriliyor

**Sorun:**
- Gerçek dünya haritası yok
- Sadece koordinat bazlı çizim var
- Yol ağı görselleştirilmiyor
- Gerçekçi şehir haritası yok

**Kod Konumu:**
- `src/main/java/com/airtraffic/ui/MapVisualization.java`

---

## 🎯 Harita Çözümleri

### Seçenek 1: OpenStreetMap (OSM) Entegrasyonu

**Avantajlar:**
- ✅ Ücretsiz ve açık kaynak
- ✅ Gerçek dünya haritaları
- ✅ Detaylı yol ağı bilgisi
- ✅ JavaFX ile entegre edilebilir

**Kütüphaneler:**
- **JMapViewer:** Java için OSM görüntüleyici
- **JavaFX WebView:** OSM web haritasını gömme
- **Leaflet.js:** WebView içinde interaktif harita

**Nasıl Temin Edilir:**
1. **Online (Internet bağlantısı gerekir):**
   - OpenStreetMap tile server'larından tile'lar indirilir
   - Gerçek zamanlı harita gösterimi

2. **Offline (Yerel dosya):**
   - OSM dosyası (.osm, .pbf formatı) indirilir
   - JOSM veya Osmosis ile işlenir
   - Yerel veritabanına yüklenir

**Örnek Kullanım:**
```java
// JMapViewer ile
JMapViewer map = new JMapViewer();
map.setDisplayPosition(new Point(41.0082, 28.9784), 12); // Istanbul, zoom level 12
```

**Gereksinimler:**
- Maven dependency: `org.openstreetmap:jmapviewer:2.14`
- Internet bağlantısı (online mod için)
- OSM dosyası (offline mod için)

---

### Seçenek 2: Google Maps / Mapbox Entegrasyonu

**Avantajlar:**
- ✅ Yüksek kaliteli haritalar
- ✅ Detaylı yol ağı
- ✅ Trafik bilgisi (Google Maps)
- ✅ 3D görünüm (Mapbox)

**Dezavantajlar:**
- ❌ API key gerektirir (ücretli olabilir)
- ❌ Kullanım limitleri var
- ❌ Ticari kullanım için lisans gerekir

**Nasıl Temin Edilir:**
1. **Google Maps:**
   - Google Cloud Console'dan API key alınır
   - JavaFX WebView ile Google Maps JavaScript API kullanılır

2. **Mapbox:**
   - Mapbox hesabı oluşturulur
   - Access token alınır
   - JavaFX WebView ile Mapbox GL JS kullanılır

---

### Seçenek 3: Basit Vektör Harita (Önerilen - Hızlı Başlangıç)

**Avantajlar:**
- ✅ Hızlı implementasyon
- ✅ Internet bağlantısı gerekmez
- ✅ Tam kontrol
- ✅ Özelleştirilebilir

**Nasıl Yapılır:**
1. **Yol Ağı Verisi:**
   - RouteNetwork sınıfından yol verileri alınır
   - Her Route → çizgi olarak çizilir
   - Ana caddeler kalın, sokaklar ince çizilir

2. **Görselleştirme:**
   - JavaFX Canvas üzerinde çizim
   - Koordinat → Pixel dönüşümü
   - Zoom ve pan desteği

**Kod Örneği:**
```java
// RouteNetwork'ten yolları çiz
for (Route route : cityMap.getRouteNetwork().getMainStreets()) {
    List<Position> waypoints = route.getWaypoints();
    for (int i = 0; i < waypoints.size() - 1; i++) {
        Position p1 = waypoints.get(i);
        Position p2 = waypoints.get(i + 1);
        
        // Lat/Lon → Pixel dönüşümü
        double x1 = latLonToX(p1.getLatitude());
        double y1 = latLonToY(p1.getLongitude());
        double x2 = latLonToX(p2.getLatitude());
        double y2 = latLonToY(p2.getLongitude());
        
        // Çizgi çiz
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(3.0);
        gc.strokeLine(x1, y1, x2, y2);
    }
}
```

---

## 🛣️ Yol Bazlı Katman Organizasyonu

### Mevcut Sorun

**Tespit Edilen Sorun:**
- Binlerce aracı aynı yol üzerinde farklı yükseklikte yerleştirmek mümkün değil
- 20m'lik bir yükseklik kanalı içinde binlerce araç için yeterli alan yok
- Her gidiş istikametinde araçlar aynı yükseklikte ve hızda olmalı

**Mevcut Sistem:**
- Sadece genel yükseklik katmanları var (LAYER_1: 0-60m, LAYER_2: 60-120m, LAYER_3: 120-180m)
- Yol bazlı organizasyon yok
- Yön bazlı (gidiş/geliş) organizasyon yok

---

### Önerilen Çözüm: Yol Segmenti Bazlı Katman Organizasyonu

#### 1. RouteSegment Modeli

**Yeni Sınıf:** `RouteSegment.java`

```java
public class RouteSegment {
    private String segmentId;
    private Route parentRoute;           // Hangi rotaya ait
    private Position startPoint;         // Segment başlangıç noktası
    private Position endPoint;           // Segment bitiş noktası
    private RouteDirection direction;    // FORWARD (gidiş) veya REVERSE (geliş)
    private double altitude;             // Bu segment için sabit yükseklik (örn: 100m)
    private double speedLimit;           // Bu segment için hız limiti
    private int maxVehicles;             // Bu segment için maksimum araç sayısı
}
```

**Yeni Enum:** `RouteDirection.java`

```java
public enum RouteDirection {
    FORWARD,   // Gidiş yönü
    REVERSE    // Geliş yönü
}
```

#### 2. Yol Segmenti Organizasyonu

**Her Yol İçin:**
- Yol, segmentlere bölünür (örn: her 100m bir segment)
- Her segment için gidiş ve geliş yönleri ayrı katmanlar
- Her katman için:
  - Sabit yükseklik (örn: gidiş 100m, geliş 105m)
  - Sabit hız limiti (örn: 25 m/s)
  - Maksimum araç kapasitesi

**Örnek:**
```
Ana Cadde 1:
  Segment 1 (0-100m):
    FORWARD:  yükseklik=100m, hız=25m/s, kapasite=50 araç
    REVERSE:  yükseklik=105m, hız=25m/s, kapasite=50 araç
  Segment 2 (100-200m):
    FORWARD:  yükseklik=100m, hız=25m/s, kapasite=50 araç
    REVERSE:  yükseklik=105m, hız=25m/s, kapasite=50 araç
```

#### 3. Trafik Akışı Yönetimi

**Kurallar:**
1. **Aynı Segment, Aynı Yön:**
   - Tüm araçlar aynı yükseklikte (örn: 100m)
   - Tüm araçlar aynı hızda (örn: 25 m/s)
   - Sıralı hareket (öncelik sırası)

2. **Kesişen Yollar:**
   - Farklı yükseklikteki yollara dönüş için geçiş katmanı
   - Geçiş sırasında özel kontrol

3. **Dönüş ve Geçiş:**
   - Farklı segmentlere geçiş için yükseklik/hız geçiş kuralları
   - Geçiş sırasında çarpışma kontrolü artırılır

#### 4. Implementasyon Planı

**Faz 2: Yol Bazlı Katman Organizasyonu**

**Yapılacaklar:**
1. `RouteSegment` modeli oluştur
2. `RouteDirection` enum oluştur
3. `RouteNetwork`'e segment yönetimi ekle
4. `Vehicle`'a mevcut segment bilgisi ekle
5. Trafik akışı yönetimi servisi oluştur
6. Kesişim yönetimi servisi oluştur
7. UI'da segment ve yön görselleştirmesi

**Tahmini Süre:** 5-7 gün

---

## 📋 Öneriler

### Kısa Vadeli (Hemen Yapılabilir)

1. **Basit Vektör Harita Görselleştirmesi:**
   - RouteNetwork'ten yolları çiz
   - Araçları yollar üzerinde göster
   - Zoom ve pan desteği
   - **Süre:** 1-2 gün

2. **Yol Segmenti Modeli:**
   - RouteSegment sınıfı oluştur
   - RouteDirection enum oluştur
   - RouteNetwork'e segment desteği ekle
   - **Süre:** 2-3 gün

### Orta Vadeli (Sprint 4 Faz 2)

1. **Yol Bazlı Katman Organizasyonu:**
   - Segment bazlı yükseklik ve hız yönetimi
   - Yön bazlı (gidiş/geliş) organizasyon
   - Trafik akışı yönetimi
   - **Süre:** 5-7 gün

2. **OpenStreetMap Entegrasyonu:**
   - JMapViewer entegrasyonu
   - Gerçek dünya haritası
   - Yol ağı görselleştirmesi
   - **Süre:** 3-5 gün

### Uzun Vadeli

1. **Gerçekçi Şehir Haritası:**
   - İstanbul için detaylı OSM verisi
   - Yol ağı, binalar, engeller
   - Yasak bölgeler (hastaneler, havalimanları)
   - **Süre:** 5-10 gün

---

## 🎯 Sonraki Adımlar

1. **Hangi harita çözümünü tercih edersiniz?**
   - Basit vektör harita (hızlı)
   - OpenStreetMap (gerçekçi)
   - Google Maps/Mapbox (en detaylı, ücretli)

2. **Yol bazlı katman organizasyonu için:**
   - Sprint 4 Faz 2 olarak planlanmış
   - Öncelik vermek ister misiniz?

3. **Görselleştirme iyileştirmesi:**
   - Şu anki basit görselleştirmeyi iyileştirelim mi?
   - Yol ağı görselleştirmesi ekleyelim mi?

---

**Not:** Bu dokümantasyon, harita ve katman organizasyonu konularında karar vermek için referans olarak kullanılacaktır.

