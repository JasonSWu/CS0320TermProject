package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/** A utility class providing helper methods for JSON conversion and field validation. */
public class Utils {

  /**
   * Converts a map to JSON using Moshi library.
   *
   * @param map The map to be converted to JSON.
   * @return The JSON representation of the input map.
   */
  public static String toMoshiJson(Map<String, Object> map) {
    // Create a Moshi instance
    Moshi moshi = new Moshi.Builder().build();

    // Define the type for the map with String keys and Object values
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);

    // Create a JSON adapter for the map type
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    // Convert the map to JSON using the adapter
    return adapter.toJson(map);
  }

  /**
   * Checks if an object has fields with specified values.
   *
   * @param obj The object to be checked for fields.
   * @param fieldsToCheck A map containing field names and their expected values.
   * @return True if the object has the specified fields with matching values, false otherwise.
   * @throws IllegalAccessException If an illegal access to a field occurs.
   */
  public static boolean hasFields(
      Inspectable obj, Map<String, Object> fieldsToCheck, List<String> exclude) {
    Map<String, Object> objFields = obj.getFields();
    // Iterate through the declared fields of the object
    for (String fieldName : fieldsToCheck.keySet()) {
      if (!objFields.containsKey(fieldName)
          || !objFields.get(fieldName).equals(fieldsToCheck.get(fieldName))) {
        return false;
      }
    }
    return true; // Return true if all specified fields have matching values
  }
}
