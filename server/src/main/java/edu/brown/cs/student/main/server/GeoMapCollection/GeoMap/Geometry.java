package edu.brown.cs.student.main.server.GeoMapCollection.GeoMap;

import java.util.List;

/** Represents the geometry of a feature, extending GeoMap, containing coordinates. */
public class Geometry extends GeoMap {

  /** The coordinates associated with the geometry. */
  public List<List<List<List<Double>>>> coordinates;

  /**
   * Checks if this geometry is equal to another object.
   *
   * @param obj The object to compare with this geometry.
   * @return True if the object is a Geometry with equal coordinates, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Geometry g) && this.coordinates.equals(g.coordinates);
  }

  /**
   * Retrieves the coordinates associated with the geometry.
   *
   * @return The coordinates of the geometry.
   */
  public List<List<List<List<Double>>>> getCoordinates() {
    return this.coordinates;
  }

  /**
   * Checks if this geometry falls within the specified bounds.
   *
   * @param centerLat The latitude of the center point for bounding box checking.
   * @param centerLng The longitude of the center point for bounding box checking.
   * @param boxLat The latitude range for bounding box checking.
   * @param boxLng The longitude range for bounding box checking.
   * @return True if the geometry falls within the specified bounds, false otherwise.
   */
  public boolean inBounds(Double centerLat, Double centerLng, Double boxLat, Double boxLng) {
    // Coordinates are in (Longitude, Latitude) format
    for (List<List<List<Double>>> multiPolygon : this.coordinates) {
      for (List<List<Double>> polygon : multiPolygon) {
        for (List<Double> coordinate : polygon) {
          if (centerLat != null
              && boxLat != null
              && !(Math.abs(coordinate.get(1) - centerLat) < boxLat / 2)) {
            return false;
          }
          if (centerLng != null
              && boxLng != null
              && !(Math.abs(coordinate.get(0) - centerLng) < boxLng / 2)) {
            return false;
          }
        }
      }
    }
    return true;
  }
}
