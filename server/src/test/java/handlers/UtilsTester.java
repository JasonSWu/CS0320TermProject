package handlers;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.GeoMapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.parser.JSONParser;
import edu.brown.cs.student.main.server.parser.ParserInterface;
import handlers.TestClasses.NoFields;
import handlers.TestClasses.TypeMix;
import handlers.TestClasses.TypeMixPrivate;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class UtilsTester {
  @Test
  public void testHasFields() {
    ParserInterface<GeoMapCollection> smallGeo;
    try {
      smallGeo = new JSONParser("data/smallgeojson.geojson");
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
      return;
    }
    GeoMapCollection g = smallGeo.getData();
    String a = "test";
    Double b = 123.0;
    Double bb = 124.0;
    Integer c = 320;
    TypeMix tOne = new TypeMix(a, b, c, g);
    TypeMix tTwo = new TypeMix(a, b, c);
    NoFields noFields = new NoFields();
    List<String> excluded = new ArrayList<>();
    Map<String, Object> m = new HashMap<>();
    try {
      assertTrue(Utils.hasFields(tOne, m, excluded));
      m.put("a", a);
      m.put("b", b);
      m.put("c", c);
      assertTrue(Utils.hasFields(tOne, m, excluded));
      m.put("g", g);
      assertTrue(Utils.hasFields(tOne, m, excluded));
      assertTrue(Utils.hasFields(tTwo, m, excluded));
      m.put("b", bb);
      assertFalse(Utils.hasFields(tOne, m, excluded));
      excluded.add("b");
      assertTrue(Utils.hasFields(tOne, m, excluded));
      assertTrue(Utils.hasFields(noFields, m, excluded));
    } catch (IllegalAccessException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testHasFieldsPrivate() {
    ParserInterface<GeoMapCollection> smallGeo;
    try {
      smallGeo = new JSONParser("data/smallgeojson.geojson");
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
      return;
    }
    GeoMapCollection g = smallGeo.getData();
    String a = "test";
    Double b = 123.0;
    Double bb = 124.0;
    TypeMixPrivate t = new TypeMixPrivate(a, b);
    List<String> excluded = new ArrayList<>();
    Map<String, Object> m = new HashMap<>();
    m.put("b", bb);
    m.put("a", a);
    try {
      assertTrue(Utils.hasFields(t, m, excluded));
    } catch (IllegalAccessException e) {
      fail(e.getMessage());
      return;
    }
  }
}
