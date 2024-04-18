package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
   * @param fields A map containing field names and their expected values.
   * @return True if the object has the specified fields with matching values, false otherwise.
   * @throws IllegalAccessException If an illegal access to a field occurs.
   */
  public static boolean hasFields(Object obj, Map<String, Object> fields, List<String> exclude)
      throws IllegalAccessException {
    // Iterate through the declared fields of the object
    for (Field f : obj.getClass().getDeclaredFields()) {
      if (!Modifier.isPublic(f.getModifiers())) continue;
      String fieldName = f.getName();
      // Check if the field is present in the specified fields map
      if (exclude.contains(fieldName)) continue;
      if (fields.containsKey(fieldName)) {
        Object value = fields.get(fieldName);
        // System.out.println(fieldName + ": " + value + " " + f.get(obj));
        // Compare the field value with the expected value
        if (value != null && f.get(obj) != null && !f.get(obj).equals(value)) {
          return false; // Return false if values don't match
        }
      }
    }
    return true; // Return true if all specified fields have matching values
  }
}
