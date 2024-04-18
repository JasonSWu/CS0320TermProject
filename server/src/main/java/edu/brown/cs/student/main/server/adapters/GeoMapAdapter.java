package edu.brown.cs.student.main.server.adapters;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.GeoMapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.GeoMapCollection.GeoMap.Geometry;
import edu.brown.cs.student.main.server.GeoMapCollection.GeoMap.Property;
import edu.brown.cs.student.main.server.GeoMapCollection.GeoMapCollection;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Adapter class for serializing and deserializing GeoMapCollection objects to/from JSON using
 * Moshi.
 */
public class GeoMapAdapter implements AdapterInterface<GeoMapCollection> {

  private static final Moshi moshi = new Moshi.Builder().build();
  private static final Type GEOMETRY_TYPE =
      Types.newParameterizedType(Geometry.class, String.class, List.class);
  private static final Type GEOMAP_TYPE =
      Types.newParameterizedType(GeoMap.class, String.class, GEOMETRY_TYPE, Property.class);
  private static final Type TYPE =
      Types.newParameterizedType(GeoMapCollection.class, String.class, List.class, GEOMAP_TYPE);

  /**
   * Serializes a GeoMapCollection object to JSON.
   *
   * @param map The GeoMapCollection object to serialize.
   * @return The JSON representation of the GeoMapCollection object.
   */
  @ToJson
  public String toJson(GeoMapCollection map) {
    JsonAdapter<GeoMapCollection> adapter = moshi.adapter(TYPE);
    return adapter.toJson(map);
  }

  /**
   * Deserializes JSON to a GeoMapCollection object.
   *
   * @param map The JSON representation of the GeoMapCollection object.
   * @return The deserialized GeoMapCollection object.
   * @throws IOException If an I/O error occurs during deserialization.
   */
  @FromJson
  public GeoMapCollection fromJson(String map) throws IOException {
    JsonAdapter<GeoMapCollection> adapter = moshi.adapter(TYPE);
    return adapter.fromJson(map);
  }

  /**
   * Retrieves the JSON adapter for GeoMapCollection objects.
   *
   * @return The JSON adapter for GeoMapCollection objects.
   */
  public static JsonAdapter<GeoMapCollection> getAdapter() {
    return moshi.adapter(TYPE);
  }
}
