package edu.brown.cs.student.main.server.parser;

public interface ParserInterface<T> {

  public boolean isSuccessfulParse();

  public T getData();

  public boolean parse(String filePath);
}
