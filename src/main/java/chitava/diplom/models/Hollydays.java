package chitava.diplom.models;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
public class Hollydays {
    Collection<Hollyday> days;
    public static Collection<String> yearHolidays = new ArrayList<>();
    public static ArrayList<LocalDate> yearHolidaysDates = new ArrayList<>();
    public static Map<Integer, Boolean> monthTime = new HashMap<>();
}
