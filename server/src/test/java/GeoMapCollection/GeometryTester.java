package GeoMapCollection;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.GeoMapCollection.GeoMap.Geometry;
import edu.brown.cs.student.main.server.GeoMapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.parser.JSONParser;
import edu.brown.cs.student.main.server.parser.ParserInterface;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;

public class GeometryTester {
  @Test
  public void testInBounds() {
    ParserInterface<GeoMapCollection> smallGeo;
    try {
      smallGeo = new JSONParser("data/singlejson.geojson");
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
      return;
    }
    Geometry g = smallGeo.getData().features.get(0).geometry;
    // [[-86.921386,33.460245],[-86.911119,33.465301],[-86.907054,33.460662],[-86.919853,33.456051],[-86.921386,33.460245]]
    // Testing within bounds
    assertTrue(g.inBounds(33.0, -86.0, 1.0, 2.0));
    assertTrue(g.inBounds(33.0, -86.0, 2.0, 3.0));
    // Testing null inputs
    assertTrue(g.inBounds(33.0, null, 1.0, 2.0));
    assertTrue(g.inBounds(null, -86.0, 1.0, 2.0));
    assertTrue(g.inBounds(33.0, -86.0, null, 2.0));
    assertTrue(g.inBounds(33.0, -86.0, 1.0, null));

    // Testing box just too small
    assertFalse(g.inBounds(33.0, -86.0, 1.0, 1.8));

    // More null testing, previous test becomes true once Longitude is no longer considered
    assertTrue(g.inBounds(33.0, null, 1.0, 1.8));
    assertFalse(g.inBounds(null, -86.0, 1.0, 1.8));
  }
}
