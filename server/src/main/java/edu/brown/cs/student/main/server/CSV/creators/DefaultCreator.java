package edu.brown.cs.student.main.server.CSV.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.util.List;

/**
 * The `DefaultCreator` class implements the `CreatorFromRow` interface, providing a default
 * implementation to convert each CSV row into a list of strings. The main purpose of this class is
 * to serve as the creator when no custom row conversion is needed. It simply returns the input row
 * as is. This class is intended to be used as a parameter for the CSV parser class constructor,
 * which takes an instance of `CreatorFromRow` to convert CSV rows into objects of a specified type.
 */
public class DefaultCreator implements CreatorFromRow<List<String>> {

  /**
   * Search for rows in the CSV data based on a specified header and value.
   *
   * @param row A list of Strings representing the entries of a CSV row
   * @return The list of Strings unchanged
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
