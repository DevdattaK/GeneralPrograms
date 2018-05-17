package EvenSchedulingThroughPriorityQueue;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class Person implements EventOwner{
    private final int id;
    private static int idGenerator = 1;
    private String firstName;
    private String lastName;
    private ZonedDateTime DOB;

    public Person() {
        id = idGenerator++;
        if(firstName == null){
            firstName = "FName";
        }
        if(lastName == null){
            lastName = "LName";
        }

        if(DOB == null){
            DOB = ZonedDateTime.now().minus(30, ChronoUnit.YEARS);
        }
    }
}
