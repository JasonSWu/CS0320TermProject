package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A Spark Route implementation for adding pins associated with a user. This class adds pins to a
 * storage handler based on user ID and pin coordinates.
 */
public class AddPinHandler implements Route {

  /** The storage handler for manipulating user data. */
  public StorageInterface storageHandler;

  /**
   * Constructs a new AddPinHandler with the specified storage handler.
   *
   * @param storageHandler The storage handler for manipulating user data.
   */
  public AddPinHandler(StorageInterface storageHandler) {
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
      // Collect parameters from the request
      String uid = request.queryParams("uid");
      String lat = request.queryParams("lat");
      String long_ = request.queryParams("long");
      Double.parseDouble(lat);
      Double.parseDouble(long_);

      // Create a map to store pin data
      Map<String, Object> data = new HashMap<>();
      data.put("lat", lat);
      data.put("long", long_);

      // Print pin details for logging
      System.out.println("Adding pin: " + lat + ", " + long_ + " for user: " + uid);

      // Get the current pin count to create a unique pin ID
      int pinCount = this.storageHandler.getCollection(uid, "pins").size();
      String pinId = "pin-" + pinCount;

      // Use the storage handler to add the pin document to the database
      this.storageHandler.addDocument(uid, "pins", pinId, data);

      // Populate response map with success and pin details
      responseMap.put("response_type", "success");
      responseMap.put("pin", lat + " " + long_);
    } catch (Exception e) {
      // Handle errors occurring during request processing
      e.printStackTrace(); // Print stack trace for debugging
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap); // Convert response map to JSON and return
  }
}
