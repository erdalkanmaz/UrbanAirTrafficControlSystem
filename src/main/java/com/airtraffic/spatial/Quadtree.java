package com.airtraffic.spatial;

import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Quadtree spatial indexing structure for efficient vehicle location queries
 * 
 * This implementation provides O(log n) average case complexity for spatial queries,
 * significantly improving performance when dealing with thousands of vehicles.
 * 
 * The quadtree divides the 2D space into four quadrants recursively until
 * each quadrant contains a maximum number of vehicles (default: 10).
 */
public class Quadtree {
    
    private static final int DEFAULT_CAPACITY = 10;
    private static final int MAX_DEPTH = 20; // Prevent infinite recursion
    
    private final double minLat;
    private final double maxLat;
    private final double minLon;
    private final double maxLon;
    private final int capacity;
    private final int depth;
    
    private List<Vehicle> vehicles;
    private Quadtree[] children; // NW, NE, SW, SE
    private boolean isDivided;
    
    /**
     * Create a quadtree with default capacity
     * @param minLat Minimum latitude
     * @param maxLat Maximum latitude
     * @param minLon Minimum longitude
     * @param maxLon Maximum longitude
     */
    public Quadtree(double minLat, double maxLat, double minLon, double maxLon) {
        this(minLat, maxLat, minLon, maxLon, DEFAULT_CAPACITY, 0);
    }
    
    /**
     * Create a quadtree with custom capacity
     * @param minLat Minimum latitude
     * @param maxLat Maximum latitude
     * @param minLon Minimum longitude
     * @param maxLon Maximum longitude
     * @param capacity Maximum vehicles per node before splitting
     * @param depth Current depth in the tree
     */
    private Quadtree(double minLat, double maxLat, double minLon, double maxLon, 
                     int capacity, int depth) {
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
        this.capacity = capacity;
        this.depth = depth;
        this.vehicles = new ArrayList<>();
        this.isDivided = false;
    }
    
    /**
     * Insert a vehicle into the quadtree
     * @param vehicle Vehicle to insert
     * @throws IllegalArgumentException if vehicle is null or has null position
     */
    public void insert(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (vehicle.getPosition() == null) {
            throw new IllegalArgumentException("Vehicle position cannot be null");
        }
        
        if (!contains(vehicle.getPosition())) {
            return; // Vehicle is outside quadtree bounds
        }
        
        if (!isDivided) {
            if (vehicles.size() < capacity || depth >= MAX_DEPTH) {
                vehicles.add(vehicle);
                return;
            }
            // Capacity exceeded, subdivide
            subdivide();
        }
        
        // Insert into appropriate child
        insertIntoChild(vehicle);
    }
    
    /**
     * Query vehicles within a radius of a center point
     * @param center Center position
     * @param radius Radius in meters
     * @return List of vehicles within the radius
     */
    public List<Vehicle> query(Position center, double radius) {
        List<Vehicle> result = new ArrayList<>();
        query(center, radius, result);
        return result;
    }
    
    /**
     * Recursive query implementation
     */
    private void query(Position center, double radius, List<Vehicle> result) {
        // Check if query circle intersects with this node's bounds
        if (!intersects(center, radius)) {
            return;
        }
        
        // Check vehicles in this node
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getPosition() != null) {
                double distance = vehicle.getPosition().horizontalDistanceTo(center);
                if (distance <= radius) {
                    result.add(vehicle);
                }
            }
        }
        
        // Query children if divided
        if (isDivided) {
            for (Quadtree child : children) {
                if (child != null) {
                    child.query(center, radius, result);
                }
            }
        }
    }
    
    /**
     * Remove a vehicle from the quadtree
     * @param vehicle Vehicle to remove
     * @return true if vehicle was found and removed
     */
    public boolean remove(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (vehicle.getPosition() == null) {
            return false;
        }
        
        if (!contains(vehicle.getPosition())) {
            return false;
        }
        
        // Try to remove from this node
        boolean removed = vehicles.remove(vehicle);
        
        // Try to remove from children
        if (isDivided) {
            for (Quadtree child : children) {
                if (child != null && child.remove(vehicle)) {
                    removed = true;
                }
            }
        }
        
        return removed;
    }
    
    /**
     * Update vehicle position in the quadtree
     * This is equivalent to remove + insert
     * @param vehicle Vehicle with updated position
     */
    public void update(Vehicle vehicle) {
        remove(vehicle);
        insert(vehicle);
    }
    
    /**
     * Clear all vehicles from the quadtree
     */
    public void clear() {
        vehicles.clear();
        if (isDivided) {
            for (int i = 0; i < children.length; i++) {
                children[i] = null;
            }
            isDivided = false;
        }
    }
    
    /**
     * Get the total number of vehicles in the quadtree
     * @return Total vehicle count
     */
    public int size() {
        int count = vehicles.size();
        if (isDivided) {
            for (Quadtree child : children) {
                if (child != null) {
                    count += child.size();
                }
            }
        }
        return count;
    }
    
    /**
     * Check if a position is within this node's bounds
     */
    private boolean contains(Position position) {
        return position.getLatitude() >= minLat && position.getLatitude() <= maxLat &&
               position.getLongitude() >= minLon && position.getLongitude() <= maxLon;
    }
    
    /**
     * Check if a query circle intersects with this node's bounds
     */
    private boolean intersects(Position center, double radius) {
        // Calculate the closest point on the bounding box to the center
        double closestLat = Math.max(minLat, Math.min(maxLat, center.getLatitude()));
        double closestLon = Math.max(minLon, Math.min(maxLon, center.getLongitude()));
        
        Position closestPoint = new Position(closestLat, closestLon, center.getAltitude());
        double distance = center.horizontalDistanceTo(closestPoint);
        
        return distance <= radius;
    }
    
    /**
     * Subdivide this node into four children
     */
    private void subdivide() {
        double midLat = (minLat + maxLat) / 2.0;
        double midLon = (minLon + maxLon) / 2.0;
        
        children = new Quadtree[4];
        children[0] = new Quadtree(minLat, midLat, minLon, midLon, capacity, depth + 1); // NW
        children[1] = new Quadtree(minLat, midLat, midLon, maxLon, capacity, depth + 1); // NE
        children[2] = new Quadtree(midLat, maxLat, minLon, midLon, capacity, depth + 1); // SW
        children[3] = new Quadtree(midLat, maxLat, midLon, maxLon, capacity, depth + 1); // SE
        
        isDivided = true;
        
        // Redistribute existing vehicles to children
        List<Vehicle> vehiclesToRedistribute = new ArrayList<>(vehicles);
        vehicles.clear();
        
        for (Vehicle vehicle : vehiclesToRedistribute) {
            insertIntoChild(vehicle);
        }
    }
    
    /**
     * Insert a vehicle into the appropriate child node
     */
    private void insertIntoChild(Vehicle vehicle) {
        Position pos = vehicle.getPosition();
        double midLat = (minLat + maxLat) / 2.0;
        double midLon = (minLon + maxLon) / 2.0;
        
        int index;
        if (pos.getLatitude() < midLat) {
            if (pos.getLongitude() < midLon) {
                index = 0; // NW
            } else {
                index = 1; // NE
            }
        } else {
            if (pos.getLongitude() < midLon) {
                index = 2; // SW
            } else {
                index = 3; // SE
            }
        }
        
        children[index].insert(vehicle);
    }
}

