package chitava.diplom.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@Component
public class WorkedHours {

    private String workerName;

    private List<LocalTime> workedHours;

    public WorkedHours() {
        this.workedHours = new ArrayList<>();
    }

    public void addTime(LocalTime time) {
        this.workedHours.add(time);
    }
    public LocalTime getTime(int i){
        return workedHours.get(i);
    }
}
