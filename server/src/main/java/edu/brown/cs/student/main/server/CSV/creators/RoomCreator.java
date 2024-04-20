package edu.brown.cs.student.main.server.CSV.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.server.CSV.rowTypes.Room;
import java.util.ArrayList;
import java.util.List;

public class RoomCreator implements CreatorFromRow<Room> {

  public Room create(List<String> row) throws FactoryFailureException {
    throw new FactoryFailureException("", new ArrayList<>());
  }
}
