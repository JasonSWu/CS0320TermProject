package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.CSV.parsers.Copyable;
import java.util.List;

public interface Searchable<T> extends Inspectable, Copyable<T> {
  public boolean checkFormat(List<String> headers);

  public boolean checkFormat();
}
