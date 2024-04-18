package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A Spark Route implementation for retrieving pins associated with a user. This class retrieves
 * pins from a storage handler based on user ID and returns them as JSON.
 */
public class GetPinsHandler implements Route {

  /** The storage handler for retrieving pins. */
  public StorageInterface storageHandler;

  /**
   * Constructs a new GetPinsHandler with the specified storage handler.
   *
   * @param storageHandler The storage handler for retrieving pins.
   */
  public GetPinsHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
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
    try {
      // Retrieve user ID from request parameters
      String uid = request.queryParams("uid");

      // Get all pins for the user from the storage handler
      List<Map<String, Object>> vals = this.storageHandler.getCollection(uid, "pins");

      // Convert the key-value map to just a list of pins (latitude and longitude)
      List<String> pins = vals.stream().map(pin -> pin.get("lat") + " " + pin.get("long")).toList();

      // Populate response map with success and list of pins
      responseMap.put("response_type", "success");
      responseMap.put("pins", pins);
    } catch (Exception e) {
      // Handle errors occurring during request processing
      e.printStackTrace(); // Print stack trace for debugging
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap); // Convert response map to JSON and return
  }
}
