package handlers;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.AddPinHandler;
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
import org.junit.jupiter.api.*;
import spark.Spark;

public class AddPinHandlerTester {

  private static final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, String.class);
  private static JsonAdapter<Map<String, String>> adapter;
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
    Spark.get("add", new AddPinHandler(firebaseUtils));
    Spark.awaitInitialization(); // Wait until the server is listening
    Spark.init();
  }

  /** Clean up Spark server after each test. */
  @AfterEach
  public void tearDown() throws ExecutionException, InterruptedException {
    // Remove Spark server endpoints
    firebaseUtils.clearUser(uid);
    Spark.unmap("add");
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
  private boolean showDetailsIfError(Map<String, String> body) {
    if (body.containsKey("response_type") && "failure".equals(body.get("response_type"))) {
      System.out.println(body.toString());
      return true;
    }
    return false;
  }

  private void testHelper(String apiCall, boolean pass) throws IOException {
    HttpURLConnection loadConnection = tryRequest(apiCall);
    // Ensure a successful response
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, String> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    if (responseBody == null || showDetailsIfError(responseBody)) {
      if (pass) fail();
    }
    if (pass) assertEquals("success", responseBody.get("response_type"));
    loadConnection.disconnect();
  }

  @Test
  public void testAddPin() {
    try {
      List<Map<String, Object>> vals = firebaseUtils.getCollection(uid, "pins");
      assertTrue(vals.isEmpty());
      Map<String, Object> pin = new HashMap<>();
      pin.put("lat", "123.0");
      pin.put("long", "1.0");
      Map<String, Object> pin2 = new HashMap<>();
      pin2.put("lat", "20.0");
      pin2.put("long", "10.0");
      testHelper("add?lat=123.0&long=1.0&uid=test_id", true);
      testHelper("add?lat=20.0&long=10.0&uid=test_id", true);
      vals = firebaseUtils.getCollection(uid, "pins");
      assertTrue(
          (pin.equals(vals.get(0)) && pin2.equals(vals.get(1)))
              || (pin.equals(vals.get(1)) && pin2.equals(vals.get(0))));
      testHelper("add?lat=124.0a&long=1.0&uid=test_id", false);
      vals = firebaseUtils.getCollection(uid, "pins");
      assertEquals(vals.size(), 2);
      assertTrue(
          (pin.equals(vals.get(0)) && pin2.equals(vals.get(1)))
              || (pin.equals(vals.get(1)) && pin2.equals(vals.get(0))));
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return;
    }
  }
}
