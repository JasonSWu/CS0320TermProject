package edu.brown.cs.student.main.server.CSV.searcher;

import edu.brown.cs.student.main.server.CSV.creators.CreatorFromRow;
import edu.brown.cs.student.main.server.CSV.parsers.CSVParser;
import edu.brown.cs.student.main.server.handlers.Searchable;
import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.CSV.searcher.InvalidSearchActionException;

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
public class CSVSearcher<T extends Searchable<T>> {

    // List to store the CSV data
    private List<T> data;

    // Indicator for whether the CSV file has headers
    private boolean hasHeaders;

    // List to store headers if present in the CSV file
    private List<String> headers;

    // Comparator for String comparison during searches
    private Comparator<T> comparator;

    // Directory containing the CSV files
    private Path dataDirectory;

    // Indicator for success of CSV file parsing
    private boolean successfulParse;

    // Indicator for success of CSV file searching
    private boolean successfulSearch;

    // String storing the results of the last search
    private String lastError;

    // Creator to pass into the parser
    private CreatorFromRow<T> creator;

    // Storage interface used to store search results
    private Storage<T> storage;

    /**
     * Constructor for CSVSearcher class.
     *
     * @param filename      The name of the CSV file to be searched.
     * @param hasHeaders    Flag indicating whether the CSV file has headers.
     * @param comparator    Comparator for string comparison during searches.
     * @param dataDirectory The directory containing the CSV file.
     */
    public CSVSearcher(
            String filename,
            boolean hasHeaders,
            Comparator<T> comparator,
            CreatorFromRow<T> creator,
            Storage<T> storage,
            Path dataDirectory) {
        this.successfulParse = false;
        this.hasHeaders = hasHeaders;
        this.comparator = comparator;
        this.dataDirectory = dataDirectory;
        this.creator = creator;
        this.storage = storage;
        this.lastError = "";

        // Validate the directory to prevent security risks
        if (!validDirectory(filename)) {
            System.err.println("Invalid file path.");
            this.lastError = "Invalid file path.";
            return;
        }

        // Construct the file path and attempt to read the CSV file
        String filePath;
        try {
            filePath = this.dataDirectory.resolve(filename).toString();
        } catch (InvalidPathException e) {
            System.err.println("Invalid file path.");
            this.lastError = "Invalid file path.";
            return;
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (IOException e) {
            System.err.println("File cannot be read or does not exist.");
            this.lastError = "File could not be read or does not exist.";
            return;
        }

        // Parse the CSV file and indicate success after
        CSVParser<T> parser = new CSVParser<T>(br, creator);
        this.successfulParse = parser.fileSuccessfullyParsed();
        if (!this.successfulParse) {
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
        return this.successfulParse;
    }

    /**
     * Extract headers from the CSV data, removing them from the main data list.
     */
    private void extractHeaders() {
        if (this.data.isEmpty()) {
            System.err.println("Empty CSV cannot have headers");
            return;
        }
        // this.headers = this.data.remove(0);
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
            if (this.headers.get(header_idx).compareTo(header) == 0) {
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
     * @param values The value to search for.
     * @return True if the search was successful, false otherwise.
     */
    public boolean search(String header, Map<String, Object> values) {
        if (header == null || values == null || values.isEmpty()) {
            System.err.println("Invalid search input");
            this.lastError = "Invalid search input";
            this.successfulSearch = false;
            return false;
        }
        if (!this.successfulParse) {
            System.err.println("File was not successfully parsed.");
            this.lastError = "File was not successfully parsed.";
            this.successfulSearch = false;
            return false;
        }
        if (!this.hasHeaders) {
            System.err.println("Cannot do header-based search with CSV that has no headers.");
            this.lastError = "Cannot do header-based search with CSV that has no headers.";
            this.successfulSearch = false;
            return false;
        }

        this.storage.clear();
        for (T row : this.data) {
            if (!row.checkFormat(this.headers)) continue;
            if (Utils.hasFields(row, values, null)) {
                this.storage.updateData(row);
            }
        }

        this.successfulSearch = true;
        return true;
    }

    /**
     * Search for rows in the CSV data based on a value across all columns.
     *
     * @param values The value to search for.
     * @return True if the search was successful, false otherwise.
     */
    public boolean search(Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            System.err.println("Invalid search input.");
            this.lastError = "Invalid search input.";
            this.successfulSearch = false;
            return false;
        }
        if (!this.successfulParse) {
            System.err.println("File was not successfully parsed.");
            this.lastError = "Invalid search input.";
            this.successfulSearch = false;
            return false;
        }

        this.storage.clear();
        for (T row : this.data) {
            if (!row.checkFormat()) continue;
            if (Utils.hasFields(row, values, null)) {
                storage.updateData(row);
                break;
            }
        }

        this.successfulSearch = true;
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
    public List<T> getLastSearch() throws InvalidSearchActionException {
        if (!successfulSearch) {
            throw new InvalidSearchActionException(this.lastError);
        }
        return this.storage.getData();
    }
}
