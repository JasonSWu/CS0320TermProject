package edu.brown.cs.student.main.server.GeoMapCollection;

import edu.brown.cs.student.main.server.GeoMapCollection.GeoMap.GeoMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Represents a collection of geographical maps, containing a type and a list of GeoMap features.
 */
public class GeoMapCollection {

  /** The type of the map collection. */
  public String type;

  /** The list of geographical map features. */
  public List<GeoMap> features;

  /**
   * Constructs a new GeoMapCollection with the specified type and list of features.
   *
   * @param type The type of the map collection.
   * @param features The list of geographical map features.
   */
  public GeoMapCollection(String type, List<GeoMap> features) {
    this.type = type;
    this.features = features;
  }

  /**
   * Constructs a new GeoMapCollection with the specified type and array of features.
   *
   * @param type The type of the map collection.
   * @param features The array of geographical map features.
   */
  public GeoMapCollection(String type, GeoMap[] features) {
    this.type = type;
    this.features = Arrays.asList(features);
  }

  /**
   * Checks if this GeoMapCollection is equal to another object.
   *
   * @param obj The object to compare with this GeoMapCollection.
   * @return True if the object is a GeoMapCollection with equal type and features, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GeoMapCollection g)) {
      return false;
    }
    if (this.features.size() != g.features.size()) {
      return false;
    }
    int n = this.features.size();
    for (int i = 0; i < n; i++) {
      if (!this.features.get(i).equals(g.features.get(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Filters the GeoMapCollection based on the provided parameters and properties.
   *
   * @param centerLat The latitude of the center point for bounding box filtering.
   * @param centerLng The longitude of the center point for bounding box filtering.
   * @param boxLat The latitude range for bounding box filtering.
   * @param boxLng The longitude range for bounding box filtering.
   * @param properties The properties to filter the map collection by.
   * @return A new GeoMapCollection containing the filtered features.
   */
  public GeoMapCollection filter(
      Double centerLat,
      Double centerLng,
      Double boxLat,
      Double boxLng,
      Map<String, Object> properties) {
    List<GeoMap> filtered = new ArrayList<>();
    for (GeoMap g : features) {
      if (g.filterCheck(centerLat, centerLng, boxLat, boxLng, properties)) {
        filtered.add(g);
      }
    }
    return new GeoMapCollection(this.type, filtered);
  }
}
