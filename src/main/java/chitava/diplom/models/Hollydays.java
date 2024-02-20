package chitava.diplom.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class Hollydays {
    Collection<Hollyday> days;
    public static Collection<String> yearHolidays = new ArrayList<>();
}
