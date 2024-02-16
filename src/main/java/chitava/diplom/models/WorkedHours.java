package chitava.diplom.models;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;

@Data

public class WorkedHours {
    @Id
    Long id;
    String workerName;

    private Collection<LocalTime> workedHours;

    public WorkedHours() {
        this.workedHours = new ArrayList<>();
    }

    public void addTime(LocalTime time) {
        this.workedHours.add(time);
    }
}
