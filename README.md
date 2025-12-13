# Urban Air Traffic Control System

Şehir içi hava taşımacılığı için kapsamlı hava trafik seyir ve yönetim programı.

## Proje Yapısı

```
UrbanAirTrafficControlSystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── airtraffic/
│   │   │           ├── model/      # Veri modelleri
│   │   │           ├── map/         # Harita yönetimi
│   │   │           ├── rules/       # Trafik kuralları
│   │   │           ├── control/     # Merkezi kontrol
│   │   │           └── ui/          # Kullanıcı arayüzü
│   │   └── resources/
│   └── test/
│       └── java/
│           └── com/
│               └── airtraffic/
└── pom.xml
```

## Teknoloji Stack

- Java 17
- JavaFX 17
- Maven
- JUnit 5
- Gson

## Kurulum

```bash
mvn clean install
```

## Çalıştırma

```bash
mvn javafx:run
```

## Test

```bash
mvn test
```

## Dokümantasyon

### Proje Bilgileri
- **PROJE_CONTEXT.md** ⭐ - Kapsamlı proje bilgileri, mimari, tasarım kararları
- **CHAT_GECMISI.md** - Geliştirme sürecindeki önemli konuşmalar ve kararlar
- **GELISTIRME_DURUMU.md** - Geliştirme durumu ve TODO listesi
- **TEST_DURUMU.md** - Test durumu ve sonuçları

### Agile Planlama
- **REQUIREMENTS.md** ⭐ - İhtiyaçlar listesi (Epic'ler, User Stories)
- **WBS.md** ⭐ - İş Kırılım Yapısı (Work Breakdown Structure)
- **BACKLOG.md** ⭐ - Product Backlog ve Sprint Backlog
- **SPRINT_PLAN.md** ⭐ - Sprint planlama ve takip
- **TEST_STRATEJISI.md** ⭐ - Test stratejisi ve planlama

### Chat Kayıtları
- **chat_logs/** - Tüm sohbet oturumlarının detaylı kayıtları

**Not:** Yeni bir chat oturumunda proje durumunu anlamak için önce `PROJE_CONTEXT.md` dosyasını okuyun.











