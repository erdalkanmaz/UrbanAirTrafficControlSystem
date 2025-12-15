package com.airtraffic.ui;

import com.airtraffic.control.FlightAuthorization;
import com.airtraffic.control.TrafficControlCenter;
import com.airtraffic.map.CityMap;
import com.airtraffic.map.Obstacle;
import com.airtraffic.map.ObstacleType;
import com.airtraffic.map.RestrictedZone;
import com.airtraffic.map.RestrictedZoneType;
import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import com.airtraffic.model.VehicleType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Ana hava trafik kontrol sistemi penceresi
 * JavaFX Application sınıfı - sistemin giriş noktası
 */
public class AirTrafficMainWindow extends Application {
    
    private Stage primaryStage;
    private BorderPane rootPane;
    private MenuBar menuBar;
    private MapVisualization mapVisualization;
    private VehicleListView vehicleListView;
    private SystemStatusPanel systemStatusPanel;
    private RealTimeUpdateService updateService;

    /**
     * JavaFX Application başlangıç metodu
     * @param primaryStage Ana pencere
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Pencere başlığı
        primaryStage.setTitle("Urban Air Traffic Control System");
        
        // Root pane oluştur
        rootPane = new BorderPane();
        
        // Menü çubuğu oluştur
        createMenuBar();
        rootPane.setTop(menuBar);
        
        // Araç listesi görüntüleme oluştur
        TrafficControlCenter controlCenter = TrafficControlCenter.getInstance();
        CityMap sampleMap = createSampleCityMap();
        controlCenter.loadCityMap(sampleMap); // Control center'a harita yükle
        
        // Test için baz istasyonu ekle (ICAO standartları testi için)
        com.airtraffic.control.BaseStation baseStation = new com.airtraffic.control.BaseStation(
            "BS-001", 
            new Position(41.0082, 28.9784, 50.0), 
            5000.0 // 5km menzil
        );
        controlCenter.addBaseStation(baseStation);
        
        // Harita görselleştirmesi oluştur
        mapVisualization = new MapVisualization();
        mapVisualization.setCityMap(sampleMap); // Test için örnek harita
        mapVisualization.setControlCenter(controlCenter); // Control center'ı bağla (araç görselleştirmesi için)
        rootPane.setCenter(mapVisualization.getView());
        
        vehicleListView = new VehicleListView(controlCenter);
        rootPane.setRight(vehicleListView.getView());
        
        // Sistem durumu paneli oluştur
        systemStatusPanel = new SystemStatusPanel(controlCenter);
        rootPane.setBottom(systemStatusPanel.getView());
        
        // Test için örnek araçlar ekle
        createSampleVehicles(controlCenter, sampleMap);
        
        // Sistem durumu panelini güncelle
        systemStatusPanel.refresh();
        
        // Real-time update service'i başlat
        // Havacılık standartlarına göre: ICAO Annex 11 - konum güncellemeleri < 1 saniye aralıklarla
        // 100ms (10 FPS) - gerçek zamanlı hava trafiği için uygun
        updateService = new RealTimeUpdateService(vehicleListView, systemStatusPanel, mapVisualization);
        updateService.start(100); // Her 100ms'de bir güncelle (10 FPS)
        
        // Scene oluştur
        Scene scene = new Scene(rootPane, 1200, 800);
        primaryStage.setScene(scene);
        
        // Pencere özellikleri
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(true);
        
        // Pencere kapatıldığında update service'i durdur
        primaryStage.setOnCloseRequest(e -> {
            if (updateService != null) {
                updateService.stop();
            }
        });
        
        // Pencereyi göster
        primaryStage.show();
    }

    /**
     * Menü çubuğu oluşturur
     */
    private void createMenuBar() {
        menuBar = new MenuBar();
        
        // File Menüsü
        Menu fileMenu = new Menu("File");
        MenuItem loadMapItem = new MenuItem("Load Map");
        MenuItem saveMapItem = new MenuItem("Save Map");
        MenuItem exitItem = new MenuItem("Exit");
        
        exitItem.setOnAction(e -> primaryStage.close());
        
        fileMenu.getItems().addAll(loadMapItem, saveMapItem, exitItem);
        
        // View Menüsü
        Menu viewMenu = new Menu("View");
        MenuItem zoomInItem = new MenuItem("Zoom In");
        MenuItem zoomOutItem = new MenuItem("Zoom Out");
        MenuItem resetViewItem = new MenuItem("Reset View");
        
        // Zoom işlemlerini harita görselleştirmesine bağla
        zoomInItem.setOnAction(e -> {
            if (mapVisualization != null) {
                mapVisualization.zoomIn();
            }
        });
        zoomOutItem.setOnAction(e -> {
            if (mapVisualization != null) {
                mapVisualization.zoomOut();
            }
        });
        resetViewItem.setOnAction(e -> {
            if (mapVisualization != null) {
                mapVisualization.resetView();
            }
        });
        
        viewMenu.getItems().addAll(zoomInItem, zoomOutItem, resetViewItem);
        
        // Tools Menüsü
        Menu toolsMenu = new Menu("Tools");
        MenuItem addVehicleItem = new MenuItem("Add Vehicle");
        MenuItem addObstacleItem = new MenuItem("Add Obstacle");
        MenuItem addRestrictedZoneItem = new MenuItem("Add Restricted Zone");
        
        toolsMenu.getItems().addAll(addVehicleItem, addObstacleItem, addRestrictedZoneItem);
        
        // Help Menüsü
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        
        helpMenu.getItems().add(aboutItem);
        
        // Menüleri ekle
        menuBar.getMenus().addAll(fileMenu, viewMenu, toolsMenu, helpMenu);
    }

    /**
     * Ana metod - uygulamayı başlatır
     * @param args Komut satırı argümanları
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Pencereyi döndürür
     * @return Ana pencere
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Root pane'i döndürür
     * @return Root pane
     */
    public BorderPane getRootPane() {
        return rootPane;
    }

    /**
     * Menü çubuğunu döndürür
     * @return Menü çubuğu
     */
    public MenuBar getMenuBar() {
        return menuBar;
    }
    
    /**
     * Harita görselleştirmesini döndürür
     * @return Map visualization
     */
    public MapVisualization getMapVisualization() {
        return mapVisualization;
    }
    
    /**
     * Araç listesi görüntülemesini döndürür
     * @return Vehicle list view
     */
    public VehicleListView getVehicleListView() {
        return vehicleListView;
    }
    
    /**
     * Sistem durumu panelini döndürür
     * @return System status panel
     */
    public SystemStatusPanel getSystemStatusPanel() {
        return systemStatusPanel;
    }
    
    /**
     * Real-time update service'i döndürür
     * @return Real-time update service
     */
    public RealTimeUpdateService getUpdateService() {
        return updateService;
    }
    
    /**
     * Test için örnek şehir haritası oluşturur
     * @return Sample city map
     */
    private CityMap createSampleCityMap() {
        CityMap cityMap = new CityMap("Istanbul");
        cityMap.setCenter(new Position(41.0082, 28.9784, 50.0));
        cityMap.setMinLatitude(40.8);
        cityMap.setMaxLatitude(41.2);
        cityMap.setMinLongitude(28.6);
        cityMap.setMaxLongitude(29.3);
        
        // Örnek engel ekle
        Obstacle building = new Obstacle("Test Building", ObstacleType.BUILDING, 
            new Position(41.0082, 28.9784, 50.0), 100.0);
        building.setRadius(50.0);
        cityMap.addObstacle(building);
        
        // Örnek yasak bölge ekle
        RestrictedZone zone = new RestrictedZone("Government Area", RestrictedZoneType.GOVERNMENT);
        zone.addBoundaryPoint(new Position(41.0, 28.9, 0.0));
        zone.addBoundaryPoint(new Position(41.0, 29.0, 0.0));
        zone.addBoundaryPoint(new Position(41.1, 29.0, 0.0));
        zone.addBoundaryPoint(new Position(41.1, 28.9, 0.0));
        zone.setMinAltitude(0.0);
        zone.setMaxAltitude(500.0);
        cityMap.addRestrictedZone(zone);
        
        return cityMap;
    }
    
    /**
     * Test için örnek araçlar oluşturur ve trafiğe kaydeder
     * @param controlCenter Traffic control center
     * @param cityMap City map
     */
    private void createSampleVehicles(TrafficControlCenter controlCenter, CityMap cityMap) {
        // Örnek araç 1: Kargo aracı
        Vehicle vehicle1 = new Vehicle(VehicleType.CARGO, new Position(41.0082, 28.9784, 100.0));
        vehicle1.setVelocity(15.0);
        vehicle1.setStatus(VehicleStatus.IN_FLIGHT);
        vehicle1.setFuelLevel(75.0);
        vehicle1.setPilotLicense("PILOT-001");
        vehicle1.setHeading(45.0); // Northeast direction
        vehicle1.setMaxSpeed(50.0);
        
        // Örnek araç 2: Yolcu aracı - ÇARPIŞMA RİSKİ TESTİ İÇİN YAKIN KONUMLANDIRILDI
        // Aynı konumda ama farklı yükseklikte (5m fark - minimum 10m gerekli)
        Vehicle vehicle2 = new Vehicle(VehicleType.PASSENGER, new Position(41.0082, 28.9784, 105.0));
        vehicle2.setVelocity(20.0);
        vehicle2.setStatus(VehicleStatus.IN_FLIGHT);
        vehicle2.setFuelLevel(85.0);
        vehicle2.setPilotLicense("PILOT-002");
        vehicle2.setHeading(90.0); // East direction
        vehicle2.setMaxSpeed(50.0);
        
        // Örnek araç 3: Acil durum aracı - Güvenli mesafede
        Vehicle vehicle3 = new Vehicle(VehicleType.EMERGENCY, new Position(41.0150, 28.9850, 120.0));
        vehicle3.setVelocity(25.0);
        vehicle3.setStatus(VehicleStatus.IN_FLIGHT);
        vehicle3.setFuelLevel(90.0);
        vehicle3.setPilotLicense("PILOT-003");
        vehicle3.setHeading(180.0); // South direction
        vehicle3.setMaxSpeed(50.0);
        
        // Uçuş izinleri al ve araçları kaydet
        // Şehir sınırları: lat 40.8-41.2, lon 28.6-29.3
        // Yasak bölge: 41.0-41.1, 28.9-29.0 (bu bölgeden kaçınmalıyız)
        try {
            // Araç 1 için izin - şehir sınırları içinde, yasak bölge dışında
            Position dep1 = new Position(40.85, 28.65, 0.0);  // Güvenli bölge
            Position dest1 = new Position(40.95, 28.85, 0.0); // Güvenli bölge (yasak bölge dışında)
            FlightAuthorization auth1 = controlCenter.requestFlightAuthorization(vehicle1, dep1, dest1);
            if (auth1.getStatus() == com.airtraffic.control.AuthorizationStatus.APPROVED) {
                controlCenter.registerVehicle(vehicle1);
                System.out.println("Araç 1 kaydedildi: " + vehicle1.getId());
            } else {
                System.out.println("Araç 1 izni reddedildi: " + auth1.getReason());
            }
            
            // Araç 2 için izin
            Position dep2 = new Position(40.90, 28.70, 0.0);  // Güvenli bölge
            Position dest2 = new Position(41.15, 29.10, 0.0); // Güvenli bölge
            FlightAuthorization auth2 = controlCenter.requestFlightAuthorization(vehicle2, dep2, dest2);
            if (auth2.getStatus() == com.airtraffic.control.AuthorizationStatus.APPROVED) {
                controlCenter.registerVehicle(vehicle2);
                System.out.println("Araç 2 kaydedildi: " + vehicle2.getId());
            } else {
                System.out.println("Araç 2 izni reddedildi: " + auth2.getReason());
            }
            
            // Araç 3 için izin
            Position dep3 = new Position(40.88, 28.68, 0.0);  // Güvenli bölge
            Position dest3 = new Position(41.12, 29.15, 0.0); // Güvenli bölge
            FlightAuthorization auth3 = controlCenter.requestFlightAuthorization(vehicle3, dep3, dest3);
            if (auth3.getStatus() == com.airtraffic.control.AuthorizationStatus.APPROVED) {
                controlCenter.registerVehicle(vehicle3);
                System.out.println("Araç 3 kaydedildi: " + vehicle3.getId());
            } else {
                System.out.println("Araç 3 izni reddedildi: " + auth3.getReason());
            }
            
            // Araç listesini güncelle
            vehicleListView.refresh();
            System.out.println("Aktif araç sayısı: " + controlCenter.getActiveVehicles().size());
            
            // Sprint 3 Test: Çarpışma kontrolü testi için araç konumlarını güncelle
            // Araç 1 ve 2'yi birbirine yakın konumlandır (çarpışma riski oluştur)
            System.out.println("\n=== SPRINT 3 TEST: Çarpışma Kontrolü ===");
            System.out.println("Araç 1 ve 2 birbirine çok yakın konumlandırıldı (çarpışma riski testi)");
            System.out.println("Konum güncellemesi yapıldığında çarpışma kontrolü otomatik çalışacak...\n");
            
            // Konum güncellemesi yaparak çarpışma kontrolünü tetikle
            // Araç 1 ve 2 aynı konumda ama farklı yükseklikte (5m fark - minimum 10m gerekli)
            controlCenter.updateVehiclePosition(vehicle1.getId(), new Position(41.0082, 28.9784, 100.0));
            controlCenter.updateVehiclePosition(vehicle2.getId(), new Position(41.0082, 28.9784, 105.0));
            
            // Sprint 3 Test: ICAO Standartları kontrolü
            System.out.println("\n=== SPRINT 3 TEST: ICAO Standartları Kontrolü ===");
            com.airtraffic.standards.ICAOStandardsCompliance icaoCompliance = 
                new com.airtraffic.standards.ICAOStandardsCompliance();
            com.airtraffic.standards.ComplianceResult complianceResult = 
                icaoCompliance.checkSeparationStandards(vehicle1, vehicle2);
            System.out.println("ICAO Uyumluluk Durumu: " + (complianceResult.isCompliant() ? "UYUMLU" : "UYUMSUZ"));
            if (!complianceResult.getViolations().isEmpty()) {
                System.out.println("İhlaller:");
                complianceResult.getViolations().forEach(v -> System.out.println("  - " + v));
            }
            if (!complianceResult.getRecommendations().isEmpty()) {
                System.out.println("Öneriler:");
                complianceResult.getRecommendations().forEach(r -> System.out.println("  - " + r));
            }
            System.out.println();
            
            // Çarpışma risklerini sorgula
            System.out.println("=== Çarpışma Riskleri Sorgulama ===");
            java.util.List<com.airtraffic.model.CollisionRisk> risks1 = controlCenter.getCollisionRisks(vehicle1.getId());
            java.util.List<com.airtraffic.model.CollisionRisk> risks2 = controlCenter.getCollisionRisks(vehicle2.getId());
            System.out.println("Araç 1 için çarpışma riskleri: " + risks1.size());
            System.out.println("Araç 2 için çarpışma riskleri: " + risks2.size());
            
            java.util.List<com.airtraffic.model.CollisionRisk> criticalRisks = controlCenter.getCriticalCollisionRisks();
            System.out.println("Kritik çarpışma riskleri: " + criticalRisks.size());
            System.out.println();
            
            // Sprint 4 Test: Yükseklik Katmanları Kontrolü
            System.out.println("=== SPRINT 4 TEST: Yükseklik Katmanları ===");
            com.airtraffic.model.AltitudeLayer layer1 = vehicle1.getCurrentLayer(cityMap);
            com.airtraffic.model.AltitudeLayer layer2 = vehicle2.getCurrentLayer(cityMap);
            com.airtraffic.model.AltitudeLayer layer3 = vehicle3.getCurrentLayer(cityMap);
            System.out.println("Araç 1 (" + vehicle1.getId().substring(0, 8) + "...) - Yükseklik: " + 
                vehicle1.getPosition().getAltitude() + "m - Katman: " + layer1);
            System.out.println("Araç 2 (" + vehicle2.getId().substring(0, 8) + "...) - Yükseklik: " + 
                vehicle2.getPosition().getAltitude() + "m - Katman: " + layer2);
            System.out.println("Araç 3 (" + vehicle3.getId().substring(0, 8) + "...) - Yükseklik: " + 
                vehicle3.getPosition().getAltitude() + "m - Katman: " + layer3);
            
            // Farklı katmanlardaki araçlar için çarpışma kontrolü (CityMap ile)
            if (layer1 != null && layer2 != null && !layer1.equals(layer2)) {
                System.out.println("\nAraç 1 ve 2 farklı katmanlarda - çarpışma riski azaltılmış olmalı");
                com.airtraffic.control.CollisionDetectionService collisionService = 
                    new com.airtraffic.control.CollisionDetectionService();
                com.airtraffic.model.CollisionRisk riskWithLayer = 
                    collisionService.calculateCollisionRisk(vehicle1, vehicle2, cityMap);
                if (riskWithLayer != null) {
                    System.out.println("Katman dikkate alınarak risk skoru: " + 
                        String.format("%.2f", riskWithLayer.getRiskScore()));
                } else {
                    System.out.println("Katman dikkate alınarak risk yok (farklı katmanlar yeterli mesafe sağlıyor)");
                }
            }
            System.out.println();
        } catch (Exception e) {
            // Hata durumunda detaylı log
            System.err.println("Örnek araç ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
}




