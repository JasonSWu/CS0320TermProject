package edu.brown.cs.student.main.server.parser;

import edu.brown.cs.student.main.server.GeoMapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.adapters.GeoMapAdapter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JSONParser implements ParserInterface<GeoMapCollection> {
  private GeoMapCollection data;

  private boolean successfulParse;

  // one coordinate in box
  // nothing in box
  // expand box a little to get one thing in
  // filter by each property
  // filter different files with normal searches

  public JSONParser(String filePath) throws FileNotFoundException {
    this.successfulParse = this.parse(filePath);
  }

  public boolean parse(String filePath) {
    System.out.println("Working Directory = " + System.getProperty("user.dir"));
    successfulParse = false;
    try {
      // ***************** READING THE FILE *****************
      FileReader jsonReader = new FileReader(filePath);
      BufferedReader br = new BufferedReader(jsonReader);
      String fileString = "";
      String line = br.readLine();
      while (line != null) {
        fileString = fileString + line;
        line = br.readLine();
      }
      jsonReader.close();

      // ****************** CREATING THE ADAPTER ***********
      this.data = new GeoMapAdapter().fromJson(fileString);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return false;
    }

    return true;
  }

  public boolean isSuccessfulParse() {
    return this.successfulParse;
  }

  public GeoMapCollection getData() {
    return this.data;
  }
}
