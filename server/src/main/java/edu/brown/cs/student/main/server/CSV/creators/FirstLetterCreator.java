package edu.brown.cs.student.main.server.CSV.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.util.List;

/**
 * The `FirstLetter` class implements the `CreatorFromRow` interface. It takes the first letter of
 * each entry and concatenates them into one String. This class is intended to be used as a
 * parameter for the CSV parser class constructor, which takes an instance of `CreatorFromRow` to
 * convert CSV rows into objects of a specified type.
 */
public class FirstLetterCreator implements CreatorFromRow<String> {

  /**
   * Search for rows in the CSV data based on a specified header and value.
   *
   * @param row A list of Strings representing the entries of a CSV row
   * @return The list of Strings unchanged
   */
  @Override
  public String create(List<String> row) throws FactoryFailureException {
    StringBuilder s = new StringBuilder();
    for (String entry : row) {
      if (entry.length() > 0) {
        s.append(entry.charAt(0));
      }
    }
    return s.toString();
  }
}
