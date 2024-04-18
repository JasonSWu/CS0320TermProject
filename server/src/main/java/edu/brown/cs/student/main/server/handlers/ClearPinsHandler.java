package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A Spark Route implementation for clearing pins associated with a user. This class removes pins
 * associated with a user from a storage handler based on user ID.
 */
public class ClearPinsHandler implements Route {

  /** The storage handler for manipulating user data. */
  public StorageInterface storageHandler;

  /**
   * Constructs a new ClearPinsHandler with the specified storage handler.
   *
   * @param storageHandler The storage handler for manipulating user data.
   */
  public ClearPinsHandler(StorageInterface storageHandler) {
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

      // Remove the user's pins from the database
      this.storageHandler.clearUser(uid);

      // Populate response map with success
      responseMap.put("response_type", "success");
    } catch (Exception e) {
      // Handle errors occurring during request processing
      e.printStackTrace(); // Print stack trace for debugging
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap); // Convert response map to JSON and return
  }
}
