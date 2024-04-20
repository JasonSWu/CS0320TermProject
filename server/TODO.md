1. Implement Room creator.

2. Clean up and unify the logic of all the interfaces into a smaller set
   of interfaces.

3. Migrate Utils.hasFields functionality to a class implementing
   either Comparator<Room> or ComparatorInterface<Room>.

4. Data protection with getData's. Partially done, some still need to
   be done. Copyable interface enforces the ability to duplicate.

5. Ensure that logic of CSVSearcher and CSVParser are general enough.
   Most of the work has to be done for CSVSearcher. Things like tracking
   the success of previous searches and parse success and what to do for each
   method in case any of these are unsuccessful.

    - Usage of storage could be improved. Do we need to throw away the last
      search result as soon as we start a new search. Will think about it later

7. Oh my fucking god im so cooked wtf