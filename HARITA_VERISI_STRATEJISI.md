# Harita Verisi Stratejisi

## 📋 Mevcut Durum

**Şu anda:** Test verisi kullanıyoruz
- `createSampleCityMap()` metodu ile örnek veri oluşturuluyor
- Istanbul koordinatları (41.0082, 28.9784)
- Örnek engel ve yasak bölge

**Sorun:** Gerçek harita verisi yok, sadece test verisi var.

---

## 🎯 Harita Verisi Stratejisi

### Seçenek 1: Test Verisi ile Devam (Şu Anki Durum) ✅

**Avantajlar:**
- Hızlı geliştirme
- Bağımlılık yok
- Test için yeterli

**Dezavantajlar:**
- Gerçekçi değil
- Kullanıcı gerçek harita göremez

**Kullanım:** UI geliştirme aşamasında test için

---

### Seçenek 2: OpenStreetMap (OSM) Verisi

**Açıklama:** Açık kaynak harita verisi

**Yöntem:**
- OSM XML/JSON formatından veri çekme
- Overpass API kullanarak belirli bölge verisi alma
- Java kütüphanesi: JOSM, OSMnx (Python'dan port)

**Avantajlar:**
- Gerçek harita verisi
- Ücretsiz
- Güncel veri

**Dezavantajlar:**
- API entegrasyonu gerekli
- Veri işleme gerekli
- Performans sorunları olabilir

**Kullanım:** Gerçek harita görselleştirmesi için

---

### Seçenek 3: GeoJSON Dosyası

**Açıklama:** Standart coğrafi veri formatı

**Yöntem:**
- GeoJSON dosyası yükleme (JSON formatı)
- Gson kütüphanesi ile parse etme
- Şehir sınırları, engeller, yasak bölgeler için kullanma

**Avantajlar:**
- Standart format
- Kolay parse
- Offline çalışabilir

**Dezavantajlar:**
- Dosya yönetimi gerekli
- Veri güncelleme manuel

**Kullanım:** Statik harita verisi için

---

### Seçenek 4: Harita Servisi API (Google Maps, Mapbox, vs.)

**Açıklama:** Ticari harita servisleri

**Yöntem:**
- API key ile servis kullanma
- Tile-based harita görselleştirme
- WebView veya native harita bileşeni

**Avantajlar:**
- Profesyonel görünüm
- Güncel veri
- Çok özellikli

**Dezavantajlar:**
- Ücretli (çoğu)
- API key gerekli
- İnternet bağlantısı gerekli
- Bağımlılık

**Kullanım:** Production ortamı için

---

## 💡 Önerilen Yaklaşım

### Faz 1: Test Verisi (Şu Anki - Sprint 1-2) ✅

**Amaç:** UI geliştirme ve test

**Yapılacaklar:**
- Test verisi ile devam
- UI bileşenleri geliştirme
- Temel görselleştirme

**Süre:** Sprint 1-2

---

### Faz 2: GeoJSON Desteği (Sprint 3-4)

**Amaç:** Gerçek harita verisi yükleme

**Yapılacaklar:**
- GeoJSON dosya yükleme
- JSON parse işlemi
- Harita verisi yönetimi
- File → Load Map menüsü aktif hale getirme

**Süre:** Sprint 3-4

**Örnek GeoJSON Formatı:**
```json
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "geometry": {
        "type": "Polygon",
        "coordinates": [[[28.6, 40.8], [29.3, 40.8], [29.3, 41.2], [28.6, 41.2], [28.6, 40.8]]]
      },
      "properties": {
        "name": "Istanbul",
        "type": "city_boundary"
      }
    }
  ]
}
```

---

### Faz 3: OpenStreetMap Entegrasyonu (Sprint 5+)

**Amaç:** Gerçek zamanlı harita verisi

**Yapılacaklar:**
- OSM API entegrasyonu
- Bina, yol, engel verisi çekme
- Dinamik harita güncelleme

**Süre:** Sprint 5+

---

## 🎯 Şu Anki Plan

### Sprint 1-2: Test Verisi ile Devam ✅

**Neden?**
- UI geliştirme odaklı
- Hızlı ilerleme
- Test için yeterli

**Yapılacaklar:**
- Test verisi ile UI geliştirme
- Harita görselleştirme bileşenleri
- Temel özellikler

---

### Sprint 3: GeoJSON Desteği Ekleme

**User Story:** US-3.5: Veri Kalıcılığı (Harita Yükleme)

**Yapılacaklar:**
- GeoJSON dosya yükleme
- JSON parse
- Harita verisi yönetimi
- File → Load Map menüsü

**Fayda:**
- Gerçek harita verisi kullanımı
- Offline çalışma
- Kullanıcı kendi haritasını yükleyebilir

---

## 📊 Karar

**Şu anki durum:** Test verisi ile devam ediyoruz ✅

**Sebepler:**
1. UI geliştirme aşamasındayız
2. Test verisi yeterli
3. Gerçek veri entegrasyonu daha sonra yapılacak

**Sonraki adım:** Sprint 3'te GeoJSON desteği ekleyeceğiz

---

## 🔧 Teknik Detaylar

### Test Verisi Yapısı

```java
CityMap cityMap = new CityMap("Istanbul");
cityMap.setCenter(new Position(41.0082, 28.9784, 50.0));
cityMap.setMinLatitude(40.8);
cityMap.setMaxLatitude(41.2);
cityMap.setMinLongitude(28.6);
cityMap.setMaxLongitude(29.3);
```

### GeoJSON Yapısı (Gelecek)

```java
// GeoJSON dosyası yükleme
File file = fileChooser.showOpenDialog(primaryStage);
GeoJSONParser parser = new GeoJSONParser();
CityMap cityMap = parser.parse(file);
```

---

## ❓ Sorular ve Cevaplar

**S: Neden şimdi gerçek harita verisi kullanmıyoruz?**
C: UI geliştirme aşamasındayız. Test verisi ile hızlı ilerliyoruz. Gerçek veri entegrasyonu Sprint 3'te yapılacak.

**S: Test verisi yeterli mi?**
C: UI geliştirme için evet. Gerçek kullanım için Sprint 3'te GeoJSON desteği eklenecek.

**S: Hangi harita formatını kullanacağız?**
C: GeoJSON (standart, kolay parse). Daha sonra OSM desteği eklenebilir.

**S: Kullanıcı kendi haritasını yükleyebilecek mi?**
C: Evet, Sprint 3'te File → Load Map menüsü ile GeoJSON dosyası yükleme özelliği eklenecek.

---

**Son Güncelleme:** 2025-12-13
**Durum:** Test verisi ile devam ediyoruz, Sprint 3'te GeoJSON desteği eklenecek

