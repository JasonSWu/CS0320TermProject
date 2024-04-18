package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.GeoMapCollection.GeoMap.Property;
import edu.brown.cs.student.main.server.GeoMapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.adapters.GeoMapAdapter;
import edu.brown.cs.student.main.server.parser.ParserInterface;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class GeoJSONHandler implements Route {

  /** The JSONParser instance for handling GeoJSON data. */
  public ParserInterface<GeoMapCollection> jsonData;

  /**
   * Constructs a new GeoJSONHandler with the specified JSONParser.
   *
   * @param jsonData The JSONParser instance containing GeoJSON data.
   */
  public GeoJSONHandler(ParserInterface<GeoMapCollection> jsonData) {
    this.jsonData = jsonData;
  }

  /**
   * Safely parses a string to double, handling null values gracefully.
   *
   * @param s The string to parse.
   * @return The parsed double value, or null if the input string is null.
   */
  public Double safeParseDouble(String s) {
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      // Handle the cases when parsing fails
      // System.err.println("Error parsing double value: " + e.getMessage());
      return null; // Or any other appropriate action, like returning a default value
    } catch (NullPointerException e) {
      // System.err.println("Error parsing null string:" + e.getMessage());
      return null;
    }
  }

  /**
   * Handles incoming HTTP requests and generates appropriate responses.
   *
   * @param request The request object providing information about the HTTP request.
   * @param response The response object providing functionality for modifying the response.
   * @return The content to be set in the HTTP response.
   */
  @Override
  public Object handle(Request request, Response response) {
    // Initialize the response map
    Map<String, Object> responseMap = new HashMap<>();

    // Check if GeoJSON data has been successfully loaded
    if (!jsonData.isSuccessfulParse()) {
      responseMap.put("response_type", "failure");
      responseMap.put("error", "GeoJSON has not been loaded.");
      return Utils.toMoshiJson(responseMap); // Convert response map to JSON and return
    }

    // Retrieve property names from GeoMap.Property enum
    List<String> propertyNames =
        Arrays.stream(Property.class.getDeclaredFields()).map((f) -> f.getName()).toList();

    // Initialize properties map to store request parameters
    Map<String, Object> properties = new HashMap<>();

    try {
      // Parse request parameters to doubles
      Double centerLat = safeParseDouble(request.queryParams("centerLat"));
      Double centerLng = safeParseDouble(request.queryParams("centerLng"));
      Double boxLat = safeParseDouble(request.queryParams("boxLat"));
      Double boxLng = safeParseDouble(request.queryParams("boxLng"));

      // Extract property values from request parameters
      for (String p : propertyNames) {
        if (p.equals("neighborhood_id")) {
          properties.put(p, safeParseDouble(request.queryParams(p)));
          continue;
        }
        properties.put(p, request.queryParams(p));
      }

      // Populate response map with success and filtered GeoJSON data
      GeoMapCollection filteredData =
          jsonData.getData().filter(centerLat, centerLng, boxLat, boxLng, properties);
      responseMap.put("response_type", "success");
      responseMap.put("result", new GeoMapAdapter().toJson(filteredData));
    } catch (Exception e) {
      // Handle errors occurring during request processing
      e.printStackTrace(); // Print stack trace for debugging
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap); // Convert response map to JSON and return
  }
}
