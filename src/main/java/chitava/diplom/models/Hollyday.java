package chitava.diplom.models;

import lombok.Data;

@Data
public class Hollyday {
    String date;
    int type_id;
    String type_text;
    String note;
    String week_day;
    Double working_hours;
}
