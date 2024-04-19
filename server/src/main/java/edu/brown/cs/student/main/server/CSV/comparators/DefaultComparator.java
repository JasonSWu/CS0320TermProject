package edu.brown.cs.student.main.server.CSV.comparators;

import java.util.Comparator;

/**
 * The `DefaultComparator` class implements the `Comparator` interface for String comparison,
 * providing a default comparison for Strings. This class is intended to be used as a parameter for
 * the CSVSearcher class constructor, allowing users to customize the comparison logic between
 * Strings. This default implementation uses Java's built-in String comparison function.
 */
public class DefaultComparator implements Comparator<String> {

  /**
   * Compares two Strings
   *
   * @param s1 The first String to be compared.
   * @param s2 The second String to be compared.
   * @return A negative integer, zero, or a positive integer if the first String is
   *     lexicographically less than, equal to, or greater than the second String.
   */
  @Override
  public int compare(String s1, String s2) {
    return s1.compareTo(s2);
  }
}
