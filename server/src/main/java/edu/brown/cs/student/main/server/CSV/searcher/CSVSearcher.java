package edu.brown.cs.student.main.server.CSV.searcher;

import edu.brown.cs.student.main.parser.CSVParser;
import edu.brown.cs.student.main.server.CSV.creators.DefaultCreator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;

/**
 * CSVSearcher is a class designed to parse then search CSV files. It provides flexibility for
 * retrieving data from CSV files based on headers and entry values.
 */
public class CSVSearcher {

  // List to store the CSV data
  private List<List<String>> data;

  // Indicator for whether the CSV file has headers
  private boolean hasHeaders;

  // List to store headers if present in the CSV file
  private List<String> headers;

  // Comparator for String comparison during searches
  private Comparator<String> comparator;

  // Directory containing the CSV files
  private Path dataDirectory;

  // Indicator for success of CSV file parsing
  private boolean success;

  // String storing the results of the last search
  private String lastSearchResult;

  /**
   * Constructor for CSVSearcher class.
   *
   * @param filename The name of the CSV file to be searched.
   * @param hasHeaders Flag indicating whether the CSV file has headers.
   * @param comparator Comparator for string comparison during searches.
   * @param dataDirectory The directory containing the CSV file.
   */
  public CSVSearcher(
      String filename, boolean hasHeaders, Comparator<String> comparator, Path dataDirectory) {
    this.success = false;
    this.hasHeaders = hasHeaders;
    this.comparator = comparator;
    this.dataDirectory = dataDirectory;

    // Validate the directory to prevent security risks
    if (!validDirectory(filename)) {
      System.err.println("Invalid file path.");
      this.lastSearchResult = "Invalid file path.";
      return;
    }

    // Construct the file path and attempt to read the CSV file
    String filePath;
    try {
      filePath = this.dataDirectory.resolve(filename).toString();
    } catch (InvalidPathException e) {
      System.err.println("Invalid file path.");
      this.lastSearchResult = "Invalid file path.";
      return;
    }
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(filePath));
    } catch (IOException e) {
      System.err.println("File cannot be read or does not exist.");
      this.lastSearchResult = "File could not be read or does not exist.";
      return;
    }

    // Parse the CSV file and indicate success after
    DefaultCreator creator = new DefaultCreator();
    CSVParser<List<String>> parser = new CSVParser<>(br, creator);
    this.success = parser.fileSuccessfullyParsed();
    if (!this.success) {
      return;
    }
    this.data = parser.getData();

    // Extract headers from the data if the CSV file has headers
    if (hasHeaders) {
      extractHeaders();
    }
  }

  /**
   * Check if the CSV file was successfully parsed.
   *
   * @return True if the file was successfully parsed, false otherwise.
   */
  public boolean fileSuccessfullyParsed() {
    return this.success;
  }

  /** Extract headers from the CSV data, removing them from the main data list. */
  private void extractHeaders() {
    if (this.data.isEmpty()) {
      System.err.println("Empty CSV cannot have headers");
      return;
    }
    this.headers = this.data.remove(0);
  }

  /**
   * Find the index of a specified header in the list of headers.
   *
   * @param header The header to search for.
   * @return The index of the header, or -1 if not found.
   */
  public int findHeaderIndex(String header) {
    int header_idx = 0;
    while (header_idx < this.headers.size()) {
      if (comparator.compare(this.headers.get(header_idx), header) == 0) {
        break;
      }
      header_idx++;
    }
    return header_idx;
  }

  /**
   * Search for rows in the CSV data based on a specified header and value.
   *
   * @param header The header to search within.
   * @param value The value to search for.
   * @return True if the search was successful, false otherwise.
   */
  public boolean search(String header, String value) {
    if (header == null || value == null || value.isEmpty()) {
      System.err.println("Invalid search input");
      return false;
    }
    if (!this.success) {
      System.err.println("File was not successfully parsed.");
      return false;
    }
    if (!this.hasHeaders) {
      System.err.println("Cannot do header-based search with CSV that has no headers.");
      return false;
    }

    String noResults = "No entry for \"" + value + "\" found.";
    int headerIdx = findHeaderIndex(header);
    StringBuilder searchResults = new StringBuilder();
    boolean rowFound = false;
    int rowIdx = -1;
    for (List<String> row : this.data) {
      rowIdx++;
      if (!checkRowFormat(row, rowIdx)) continue;
      if (row.size() > headerIdx && comparator.compare(row.get(headerIdx), value) == 0) {
        rowFound = true;
        searchResults.append(row);
        searchResults.append("\n");
        System.out.println(row);
      }
    }

    if (!rowFound) {
      System.out.println(noResults);
      searchResults.append(noResults);
    }

    this.lastSearchResult = searchResults.toString().strip();
    return true;
  }

  /**
   * Search for rows in the CSV data based on a specified column index and value.
   *
   * @param column The index of the column to search within.
   * @param value The value to search for.
   * @return True if the search was successful, false otherwise.
   */
  public boolean search(int column, String value) {
    if (column < 0 || value == null || value.isEmpty()) {
      System.err.println("Invalid search input");
      return false;
    }
    if (!this.success) {
      System.err.println("File was not successfully parsed.");
      return false;
    }

    String noResults = "No entry for \"" + value + "\" found.";
    StringBuilder searchResults = new StringBuilder();
    boolean rowFound = false;
    int row_idx = -1;
    for (List<String> row : this.data) {
      row_idx++;
      if (!checkRowFormat(row, row_idx)) continue;
      if (column < row.size() && this.comparator.compare(value, row.get(column)) == 0) {
        rowFound = true;
        searchResults.append(row);
        searchResults.append("\n");
        System.out.println(row);
      }
    }

    if (!rowFound) {
      System.out.println(noResults);
      searchResults.append(noResults);
    }

    this.lastSearchResult = searchResults.toString().strip();
    return true;
  }

  /**
   * Search for rows in the CSV data based on a value across all columns.
   *
   * @param value The value to search for.
   * @return True if the search was successful, false otherwise.
   */
  public boolean search(String value) {
    if (value == null || value.isEmpty()) {
      System.err.println("Invalid search input.");
      return false;
    }
    if (!this.success) {
      System.err.println("File was not successfully parsed.");
      return false;
    }

    String noResults = "No entry for \"" + value + "\" found.";
    StringBuilder searchResults = new StringBuilder();
    boolean rowFound = false;
    int row_idx = -1;
    for (List<String> row : this.data) {
      row_idx++;
      if (!checkRowFormat(row, row_idx)) continue;
      for (String entry : row) {
        if (this.comparator.compare(value, entry) == 0) {
          rowFound = true;
          searchResults.append(row);
          searchResults.append("\n");
          System.out.println(row);
          break;
        }
      }
    }

    if (!rowFound) {
      System.out.println(noResults);
      searchResults.append(noResults);
    }

    this.lastSearchResult = searchResults.toString().strip();
    return true;
  }

  /**
   * Check the format of a CSV row, ensuring there are no empty entries and, if headers are present,
   * the number of entries matches the number of headers.
   *
   * @param row The CSV row to check.
   * @param row_idx The index of the current row being checked.
   * @return True if the row format is valid, false otherwise.
   */
  private boolean checkRowFormat(List<String> row, int row_idx) {
    for (String entry : row) {
      if (entry.isEmpty()) {
        System.err.println("Empty entry in Row " + row_idx + ".");
        return false;
      }
    }
    if (this.hasHeaders && row.size() != this.headers.size()) {
      System.err.println("Number of entries in Row " + row_idx + " inconsistent with headers.");
      return false;
    }
    return true;
  }

  /**
   * Validate the provided filename to prevent directory traversal security risks.
   *
   * @param filename The filename to validate.
   * @return True if the filename is valid, false otherwise.
   */
  private static boolean validDirectory(String filename) {
    return !filename.contains("..");
  }

  /**
   * Get the string representation of the last search results.
   *
   * @return The string representation of the last search results.
   */
  public String getLastSearchResult() {
    return this.lastSearchResult;
  }
}
