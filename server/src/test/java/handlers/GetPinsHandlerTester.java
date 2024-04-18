package handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.GetPinsHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class GetPinsHandlerTester {
  private static final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private static JsonAdapter<Map<String, Object>> adapter;
  static StorageInterface firebaseUtils;
  static String uid = "test_id";

  @BeforeAll
  public static void initGlobals() throws FileNotFoundException {
    // Set up Spark server
    Spark.port(0);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    try {
      firebaseUtils = new FirebaseUtilities();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      return;
    }
  }

  @BeforeEach
  public void setup() {
    // Set up Spark server endpoints
    Spark.get("get", new GetPinsHandler(firebaseUtils));
    Spark.awaitInitialization(); // Wait until the server is listening
    Spark.init();
  }

  /** Clean up Spark server after each test. */
  @AfterEach
  public void tearDown() throws ExecutionException, InterruptedException {
    // Remove Spark server endpoints
    firebaseUtils.clearUser(uid);
    Spark.unmap("get");
    Spark.awaitStop(); // Wait until the server is stopped
  }

  /**
   * Helper method to start a connection to a specific API endpoint.
   *
   * @param apiCall The API endpoint to connect to.
   * @return The connection to the API endpoint.
   * @throws IOException If the connection fails.
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Helper method to print error details if an error occurs in the response.
   *
   * @param body The JSON response body.
   * @return True if an error occurred, false otherwise.
   */
  private boolean showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("response_type") && "failure".equals(body.get("response_type"))) {
      System.out.println(body.toString());
      return true;
    }
    return false;
  }

  private Map<String, Object> testHelper(String apiCall, boolean pass) throws IOException {
    HttpURLConnection loadConnection = tryRequest(apiCall);
    // Ensure a successful response
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    if (responseBody == null || showDetailsIfError(responseBody)) {
      if (pass) fail();
    }
    if (pass) assertEquals("success", responseBody.get("response_type"));
    loadConnection.disconnect();
    return responseBody;
  }

  @Test
  public void testClearPins() {
    try {
      Object vals = testHelper("get?uid=test_id", true).get("pins");
      assertTrue(vals instanceof List);
      List pins = (List) vals;
      assertTrue(pins.isEmpty());
      Map<String, Object> pin = new HashMap<>();
      pin.put("lat", "123.0");
      pin.put("long", "1.0");
      Map<String, Object> pin2 = new HashMap<>();
      pin2.put("lat", "20.0");
      pin2.put("long", "10.0");
      firebaseUtils.addDocument(uid, "pins", "pin0", pin);
      vals = testHelper("get?uid=test_id", true).get("pins");
      assertTrue(vals instanceof List);
      pins = (List) vals;
      assertEquals(pins.size(), 1);
      firebaseUtils.clearUser(uid);
      vals = testHelper("get?uid=test_id", true).get("pins");
      assertTrue(vals instanceof List);
      pins = (List) vals;
      assertEquals(pins.size(), 0);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return;
    }
  }
}
