package edu.brown.cs.student.main.server.GeoMapCollection.GeoMap;

import java.util.Map;

/**
 * Represents a geographical map entity containing information about its type, geometry, and
 * properties.
 */
public class GeoMap {

  /** The type of the geographical map. */
  public String type;

  /** The geometry associated with the geographical map. */
  public Geometry geometry;

  /** The properties associated with the geographical map. */
  public Property properties;

  /**
   * Retrieves the geometry associated with the geographical map.
   *
   * @return The geometry of the geographical map.
   */
  public Geometry getGeometry() {
    return this.geometry;
  }

  /**
   * Retrieves the properties associated with the geographical map.
   *
   * @return The properties of the geographical map.
   */
  public Property getProperty() {
    return this.properties;
  }

  /**
   * Checks if the geographical map passes the filter conditions based on the provided parameters.
   *
   * @param centerLat The latitude of the center point for bounding box filtering.
   * @param centerLng The longitude of the center point for bounding box filtering.
   * @param boxLat The latitude range for bounding box filtering.
   * @param boxLng The longitude range for bounding box filtering.
   * @param properties The properties to filter the map by.
   * @return True if the geographical map passes the filter conditions, false otherwise.
   */
  public boolean filterCheck(
      Double centerLat,
      Double centerLng,
      Double boxLat,
      Double boxLng,
      Map<String, Object> properties) {
    if (this.geometry != null && !this.geometry.inBounds(centerLat, centerLng, boxLat, boxLng)) {
      return false;
    }
    if (this.properties != null && !this.properties.filterCheck(properties)) {
      return false;
    }
    return true;
  }

  /**
   * Checks if this geographical map is equal to another object.
   *
   * @param obj The object to compare with this geographical map.
   * @return True if the object is a GeoMap with equal geometry and properties, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof GeoMap g)
        && this.geometry.equals(g.geometry)
        && this.properties.equals(g.properties);
  }
}
