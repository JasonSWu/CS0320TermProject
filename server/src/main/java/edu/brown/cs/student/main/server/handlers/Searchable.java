package edu.brown.cs.student.main.server.handlers;

import java.util.List;

import edu.brown.cs.student.main.server.CSV.parsers.Copyable;

public interface Searchable<T> extends Inspectable, Copyable<T> {
    public boolean checkFormat(List<String> headers);

    public boolean checkFormat();
}
