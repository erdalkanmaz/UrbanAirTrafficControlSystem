package com.airtraffic.map;

import com.airtraffic.model.Position;

/**
 * Engel tanımı (bina, köprü, yüksek gerilim hattı, vb.)
 */
public class Obstacle {
    private String id;
    private String name;
    private ObstacleType type;
    private Position position;          // Merkez noktası
    private double height;               // Yükseklik (metre)
    private double radius;               // Yarıçap (metre) - dairesel engel için
    private double width;                // Genişlik (metre) - dikdörtgen engel için
    private double length;               // Uzunluk (metre) - dikdörtgen engel için
    private double heading;              // Yön (derece) - dikdörtgen engel için

    public Obstacle() {
    }

    public Obstacle(String name, ObstacleType type, Position position, double height) {
        this.name = name;
        this.type = type;
        this.position = position;
        this.height = height;
    }

    /**
     * Belirli bir konumun engelin içinde olup olmadığını kontrol eder
     * @param checkPosition Kontrol edilecek konum
     * @return Engelin içindeyse true
     */
    public boolean contains(Position checkPosition) {
        if (checkPosition.getAltitude() > position.getAltitude() + height) {
            return false; // Yükseklik engelin üstünde
        }

        double horizontalDistance = position.horizontalDistanceTo(checkPosition);

        // Dairesel engel
        if (radius > 0) {
            return horizontalDistance <= radius;
        }

        // Dikdörtgen engel (basitleştirilmiş - daha gelişmiş geometri gerekebilir)
        if (width > 0 && length > 0) {
            // Basit dikdörtgen kontrolü (daha gelişmiş dönüşüm gerekebilir)
            double maxDistance = Math.max(width, length) / 2.0;
            return horizontalDistance <= maxDistance;
        }

        return false;
    }

    /**
     * Güvenli geçiş yüksekliğini döndürür (engelin üstünden geçiş için)
     * @return Güvenli yükseklik (metre)
     */
    public double getSafePassageAltitude() {
        return position.getAltitude() + height + 10.0; // 10m güvenlik payı
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObstacleType getType() {
        return type;
    }

    public void setType(ObstacleType type) {
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }
}











