package chitava.diplom.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Data
@AllArgsConstructor
public class MonthAllWorkersHours {
    private ArrayList<WorkedHours> monthAllHours;

    public MonthAllWorkersHours() {
        this.monthAllHours = new ArrayList<>();
    }


    public void addWorkedHours(WorkedHours workedHours) {
        this.monthAllHours.add(workedHours);
    }

    public int lenth(){
        return monthAllHours.size();
    }
}
