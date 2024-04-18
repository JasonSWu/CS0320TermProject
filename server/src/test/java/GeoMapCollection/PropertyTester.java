package GeoMapCollection;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.GeoMapCollection.GeoMap.Property;
import edu.brown.cs.student.main.server.GeoMapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.parser.JSONParser;
import edu.brown.cs.student.main.server.parser.ParserInterface;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class PropertyTester {
  @Test
  public void testFilterCheck() {
    ParserInterface<GeoMapCollection> smallGeo;
    try {
      smallGeo = new JSONParser("data/singlejson.geojson");
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
      return;
    }
    Property p = smallGeo.getData().features.get(0).properties;
    Map<String, Object> m = new HashMap<>();
    assertTrue(p.filterCheck(m));
    m.put("name", "Golden Gate Bridge");
    m.put("neighborhood_id", Double.parseDouble("123"));
    assertTrue(p.filterCheck(m));
    m.put("city", "Houston");
    assertFalse(p.filterCheck(m));
  }
}
