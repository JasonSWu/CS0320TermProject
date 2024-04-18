package handlers.TestClasses;

import edu.brown.cs.student.main.server.GeoMapCollection.GeoMapCollection;

public class TypeMix {
  public String a;
  public Double b;
  public Integer c;
  public GeoMapCollection g;

  public TypeMix(String a, Double b, Integer c, GeoMapCollection g) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.g = g;
  }

  public TypeMix(String a, Double b, Integer c) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.g = null;
  }
}
