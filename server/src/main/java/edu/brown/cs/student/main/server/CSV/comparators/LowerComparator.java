package edu.brown.cs.student.main.server.CSV.comparators;

import java.util.Comparator;

/**
 * The `LowerComparator` class implements the `Comparator` interface for String comparison,
 * providing a case-blind comparison for Strings. This class is intended to be used as a parameter
 * for the CSVSearcher class constructor, allowing users to customize the comparison logic between
 * Strings.
 */
public class LowerComparator implements Comparator<String> {

  /**
   * Compares two Strings in a case-blind manner
   *
   * @param s1 The first String to be compared.
   * @param s2 The second String to be compared.
   * @return A negative integer, zero, or a positive integer as the first String is
   *     lexicographically less than, equal to, or greater than the second String.
   */
  @Override
  public int compare(String s1, String s2) {
    return s1.toLowerCase().compareTo(s2.toLowerCase());
  }
}
