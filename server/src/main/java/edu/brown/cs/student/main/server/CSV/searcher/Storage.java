package edu.brown.cs.student.main.server.CSV.searcher;

import java.util.List;

public interface Storage<T> {
  public void updateData(T data);

  public void updateData(List<T> data);

  public List<T> getData();

  public void clear();
}
