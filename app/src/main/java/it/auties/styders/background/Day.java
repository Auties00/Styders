package it.auties.styders.background;

import java.util.ArrayList;
import java.util.List;

public enum Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static Day fromInt(int value) {
        switch (value) {
            case 1:
            default:
                return SUNDAY;
            case 2:
                return MONDAY;
            case 3:
                return TUESDAY;
            case 4:
                return WEDNESDAY;
            case 5:
                return THURSDAY;
            case 6:
                return FRIDAY;
            case 7:
                return SATURDAY;
        }
    }

    public static List<String> valuesAsString(){
        List<String> days = new ArrayList<>();
        for(Day day : values()){
            days.add(day.name());
        }

        return days;
    }
}
