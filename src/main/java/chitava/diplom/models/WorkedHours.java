package chitava.diplom.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkedHours {

    private Worker worker;
    private boolean dontWork;
    private List<LocalDateTime> times = new ArrayList<>();

    public LocalDateTime getTime(int id) {
        return times.get(id);
    }

    public void addTime(LocalDateTime time) {
        this.times.add(time);
    }

    public List<LocalDateTime> getTimes(int startDate, int endDate) {
        List<LocalDateTime> result = new ArrayList<>();
        if  (endDate > times.size())  {
            endDate = times.size();
        }
        if (times.size() > 0) {
            for (int i = 0; i < startDate - 1; i++) {
                result.add(LocalDateTime.of(0, 1, 1, 0, 0));
            }
            for (int i = startDate - 1; i < endDate; i++) {
                result.add(times.get(i));
            }
            for (int i = endDate; i < times.size(); i++) {
                result.add(LocalDateTime.of(0, 1, 1, 0, 0));
            }
        }
        return result;
    }

    public void setTimes(List<LocalDateTime> times) {
        this.times = times;
    }


}
