package handlers;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.GeoMapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.GeoMapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.adapters.AdapterInterface;
import edu.brown.cs.student.main.server.adapters.GeoMapAdapter;
import edu.brown.cs.student.main.server.handlers.GeoJSONHandler;
import edu.brown.cs.student.main.server.parser.JSONParser;
import edu.brown.cs.student.main.server.parser.ParserInterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** Test class for validating the functionality of the GeoJSONHandler class. */
public class GeoJSONHandlerTester {

  // Constants for JSON parsing
  private static final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, String.class);
  private static JsonAdapter<Map<String, String>> adapter;
  private static AdapterInterface<GeoMapCollection> geoAdapter;

  // Sample GeoMap objects for testing
  private static GeoMap single1, small1, small2, small3, small4, food1, food2;

  /** Initialize global variables and set up Spark server before all tests. */
  @BeforeAll
  public static void initGlobals() throws FileNotFoundException {
    // Set up Spark server
    Spark.port(0);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    geoAdapter = new GeoMapAdapter();

    // Load sample GeoMap data for testing
    List<GeoMap> small, food, single;
    small = (new JSONParser("data/smallgeojson.geojson")).getData().features;
    food = (new JSONParser("data/foodLand.geojson")).getData().features;
    single = (new JSONParser("data/singlejson.geojson")).getData().features;
    single1 = single.get(0);
    small1 = small.get(0);
    small2 = small.get(1);
    small3 = small.get(2);
    small4 = small.get(3);
    food1 = food.get(0);
    food2 = food.get(1);
  }

  /** Set up Spark server before each test. */
  @BeforeEach
  public void setup() {
    // Set up Spark server endpoints
    ParserInterface<GeoMapCollection> smallGeo, foodLand, single;
    try {
      smallGeo = new JSONParser("data/smallgeojson.geojson");
      foodLand = new JSONParser("data/foodLand.geojson");
      single = new JSONParser("data/singlejson.geojson");
    } catch (FileNotFoundException e) {
      System.err.println(e.toString());
      return;
    }
    Spark.get("small", new GeoJSONHandler(smallGeo));
    Spark.get("food", new GeoJSONHandler(foodLand));
    Spark.get("single", new GeoJSONHandler(single));
    Spark.awaitInitialization(); // Wait until the server is listening
    Spark.init();
  }

  /** Clean up Spark server after each test. */
  @AfterEach
  public void tearDown() {
    // Remove Spark server endpoints
    Spark.unmap("small");
    Spark.unmap("food");
    Spark.unmap("single");
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

  /**
   * Helper method to test multiple API calls and their responses.
   *
   * @param apiCalls The array of API calls to make.
   * @param targetResponses The expected responses for each API call.
   * @throws IOException If an error occurs during the test.
   */
  private void testHelper(String[] apiCalls, List<GeoMapCollection> targetResponses)
      throws IOException {
    int n;
    if ((n = apiCalls.length) != targetResponses.size()) {
      fail("Error: testHelper's apiCalls and targetResponses params are of different sizes");
    }
    for (int i = 0; i < n; i++) {
      // Set up the request, make the request
      HttpURLConnection loadConnection = tryRequest(apiCalls[i]);
      // Ensure a successful response
      assertEquals(200, loadConnection.getResponseCode());
      // Get the expected response: a success
      Map<String, String> responseBody =
          adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
      if (responseBody == null || showDetailsIfError(responseBody)) {
        fail();
      }
      assertEquals("success", responseBody.get("response_type"));
      assertTrue(responseBody.containsKey("result"));
      GeoMapCollection data = geoAdapter.fromJson(responseBody.get("result"));
      assertTrue(data != null && data.equals(targetResponses.get(i)));
      loadConnection.disconnect();
    }
  }

  /**
   * Test case for filtering small geo maps.
   *
   * @throws IOException If an error occurs during the test.
   */
  @Test
  public void testSmall() throws IOException {
    String[] apiCalls = {
      "small?state=California&city=San%20Francisco",
      "small?centerLng=12&centerLat=110&boxLng=3&boxLat=22",
      "small?centerLng=12&centerLat=110&boxLng=2&boxLat=20",
      "small?centerLng=12a&centerLat=110&boxLng=3&boxLat=22"
    };
    GeoMap[][] geoMaps = {{small1, small2, small3, small4}, {small4}, {}, {small1, small2, small4}};
    List<GeoMapCollection> geoMapCollections =
        Arrays.stream(geoMaps).map((g) -> new GeoMapCollection("FeatureCollection", g)).toList();
    testHelper(apiCalls, geoMapCollections);
  }

  /**
   * Test case for filtering food geo maps.
   *
   * @throws IOException If an error occurs during the test.
   */
  @Test
  public void testFood() throws IOException {
    String[] apiCalls = {
      "food?neighborhood_id=123",
      "food?centerLng=-86&centerLat=33&boxLng=1&boxLat=1",
      "food?centerLng=-86&centerLat=33&boxLng=2&boxLat=2",
      "food?neighborhood_id=476&centerLng=0&centerLat=0&boxLng=900&boxLat=900"
    };
    GeoMap[][] geoMaps = {{food1}, {}, {food1}, {food2}};
    List<GeoMapCollection> geoMapCollections =
        Arrays.stream(geoMaps).map((g) -> new GeoMapCollection("FeatureCollection", g)).toList();
    testHelper(apiCalls, geoMapCollections);
  }

  /**
   * Test case for filtering single geo maps.
   *
   * @throws IOException If an error occurs during the test.
   */
  @Test
  public void testSingle() throws IOException {
    String[] apiCalls = {
      "single?state=California&city=San%20Francisco&name=Golden%20Gate%20Bridge"
          + "&holc_id=A1&holc_grade=A%2b&neighborhood_id=123&area_description_data=Providence",
      "single?state=California&city=San%20Francisco&name=Golden%20Gate%20Bridge"
          + "&holc_id=A1&holc_grade=B&neighborhood_id=123&area_description_data=Providence"
    };
    GeoMap[][] geoMaps = {{single1}, {}};
    List<GeoMapCollection> geoMapCollections =
        Arrays.stream(geoMaps).map((g) -> new GeoMapCollection("FeatureCollection", g)).toList();
    testHelper(apiCalls, geoMapCollections);
  }
}
