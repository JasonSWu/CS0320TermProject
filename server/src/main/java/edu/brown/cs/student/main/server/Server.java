package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.GeoMapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.handlers.AddPinHandler;
import edu.brown.cs.student.main.server.handlers.ClearPinsHandler;
import edu.brown.cs.student.main.server.handlers.GeoJSONHandler;
import edu.brown.cs.student.main.server.handlers.GetPinsHandler;
import edu.brown.cs.student.main.server.parser.JSONParser;
import edu.brown.cs.student.main.server.parser.ParserInterface;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import spark.Filter;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  public static void setUpServer() {
    int port = 3232;
    Spark.port(port);

    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });

    StorageInterface firebaseUtils;
    ParserInterface<GeoMapCollection> geoJSON;
    try {
      firebaseUtils = new FirebaseUtilities();
      geoJSON = new JSONParser("data/fullDownload.geojson");

      Spark.get("add-pin", new AddPinHandler(firebaseUtils));
      Spark.get("get-pins", new GetPinsHandler(firebaseUtils));
      Spark.get("clear-pins", new ClearPinsHandler(firebaseUtils));
      Spark.get("geo-json", new GeoJSONHandler(geoJSON));

      Spark.notFound(
          (request, response) -> {
            response.status(404); // Not Found
            System.out.println("ERROR");
            return "404 Not Found - The requested endpoint does not exist.";
          });
      Spark.init();
      Spark.awaitInitialization();

      System.out.println("Server started at http://localhost:" + port);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.err.println("Error: Could not find GeoJSON file. Exiting");
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println(
          "Error: Could not initialize Firebase. Likely due to firebase_config.json not being found. Exiting.");
      System.exit(1);
    }
  }

  /**
   * Runs Server.
   *
   * @param args none
   */
  public static void main(String[] args) {
    setUpServer();
  }
}
