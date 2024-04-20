package edu.brown.cs.student.main.server.CSV.rowTypes;

import edu.brown.cs.student.main.server.CSV.rowTypes.enums.DormEnumInterface;
import edu.brown.cs.student.main.server.CSV.rowTypes.enums.Gender;
import edu.brown.cs.student.main.server.CSV.rowTypes.enums.RoomType;

public class Room {
  private DormEnumInterface building;

  private int floor;

  private int suiteNumber;

  private int roomNumber;

  private int suiteSize;

  private RoomType roomType;

  private Gender roomGender;

  public Room(
      DormEnumInterface building,
      int floor,
      int suiteNumber,
      int roomNumber,
      RoomType roomType,
      int suiteSize,
      Gender roomGender) {
    this.building = building;
    this.floor = floor;
    this.suiteNumber = suiteNumber;
    this.roomNumber = roomNumber;
    this.suiteSize = suiteSize;
    this.roomGender = roomGender;
  }

  public Room(
      DormEnumInterface building, int floor, int suiteNumber, int roomNumber, Gender roomGender) {
    this.building = building;
    this.floor = floor;
    this.suiteNumber = suiteNumber;
    this.roomNumber = roomNumber;
    this.suiteSize = 0;
    this.roomGender = roomGender;
  }
}
