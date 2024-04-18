package edu.brown.cs.student.main.server.adapters;

import java.io.IOException;

/**
 * An interface representing the contract for serializing and deserializing objects of type T
 * to/from JSON format. Implementing classes must provide implementations for both JSON
 * serialization and deserialization methods.
 *
 * @param <T> The type of object to serialize and deserialize.
 */
public interface AdapterInterface<T> {

  /**
   * Converts an object of type T to its JSON representation.
   *
   * @param obj The object to serialize to JSON.
   * @return The JSON representation of the input object.
   */
  public String toJson(T obj);

  /**
   * Converts a JSON string to an object of type T.
   *
   * @param json The JSON string to deserialize.
   * @return The deserialized object of type T.
   * @throws IOException If an I/O error occurs during deserialization.
   */
  public T fromJson(String json) throws IOException;
}
