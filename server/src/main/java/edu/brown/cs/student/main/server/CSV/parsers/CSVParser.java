package edu.brown.cs.student.main.server.CSV.parsers;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.server.CSV.creators.CreatorFromRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * CSVParser class is designed to parse CSV files and convert each row into objects of a specified
 * type using the CreatorFromRow interface.
 *
 * @param <T> The type of object to be created from each CSV row.
 */
public class CSVParser<T> {

  // BufferedReader to read the CSV file
  private BufferedReader br;

  // Interface for creating objects from CSV rows
  private CreatorFromRow<T> creator;

  // List to store the parsed data
  private List<T> data;

  // Indicator for whether parsing was successful
  private boolean success;

  // Class parser
  private static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * Constructor for CSVParser class.
   *
   * @param file A Reader object representing the CSV file to be parsed.
   * @param creator An instance of CreatorFromRow interface responsible for creating objects from
   *     CSV rows.
   */
  public CSVParser(Reader file, CreatorFromRow<T> creator) {
    this.br = new BufferedReader(file);
    this.creator = creator;
    this.data = new ArrayList<T>();
    this.success = false;
    try {
      // Parse the CSV file upon instantiation
      this.parse();
    } catch (IOException e) {
      System.err.println("Error while reading file: " + e.toString());
      return;
    }
    this.success = true;
  }

  /**
   * Check if the CSV file was successfully parsed.
   *
   * @return True if the file was successfully parsed, false otherwise.
   */
  public boolean fileSuccessfullyParsed() {
    return this.success;
  }

  /**
   * Parse the CSV file, creating objects for each row and storing them in the 'data' field.
   *
   * @throws IOException If there is an error parsing the CSV file.
   */
  private void parse() throws IOException {
    String row;
    int row_idx = 0;
    while ((row = this.br.readLine()) != null) {
      // Process each row during parsing
      parseRow(row, row_idx);
      row_idx++;
    }
  }

  /**
   * Process an individual CSV row by splitting it into fields, creating an object, and adding it to
   * the 'data' list.
   *
   * @param row A String containing the row to be parsed.
   * @param row_idx An int representing the index of the row to parse.
   */
  private void parseRow(String row, int row_idx) {
    try {
      // Split the row into fields, create an object, and add it to the 'data' list

      // List<String> splitRow = Arrays.asList(regexSplitCSVRow.split(row));
      List<String> splitRow = tokenizeRow(row);
      T elt = this.creator.create(splitRow);
      this.data.add(elt);
    } catch (FactoryFailureException e) {
      System.err.println("Error converting Row " + row_idx + " " + e.toString());
    }
  }

  /**
   * Split a CSV row into a list of entries, handling cases of fields enclosed in quotes or
   * containing commas.
   *
   * @param row A String containing the row to be parsed.
   * @return A list of String entries extracted from the CSV row.
   */
  private List<String> tokenizeRow(String row) {
    List<String> parsedRow = new ArrayList<>();
    boolean betweenQuotes = false;
    char[] chars = row.toCharArray();
    StringBuilder entry = new StringBuilder();
    for (int i = 0; i < chars.length; i++) {
      if (!betweenQuotes && chars[i] == '\"') {
        betweenQuotes = true;
      } else if (chars[i] == ',') {
        if (betweenQuotes) {
          entry.append(chars[i]);
        } else {
          parsedRow.add(entry.toString());
          entry = new StringBuilder();
        }
      } else if (i < chars.length - 1 && chars[i] == '\"' && chars[i + 1] == '\"') {
        entry.append('\"');
        i++;
      } else if (chars[i] == '\"') {
        betweenQuotes = false;
      } else {
        entry.append(chars[i]);
      }
    }
    parsedRow.add(entry.toString());
    return parsedRow;
  }

  /**
   * Get the list of objects created from parsing the CSV file.
   *
   * @return The list of objects created from parsing the CSV file.
   */
  public List<T> getData() {
    return this.data;
  }
}
