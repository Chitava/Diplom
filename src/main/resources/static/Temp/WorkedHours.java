import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class WorkedHours {
    Long id;
    private Map<Worker, Collection<LocalTime>> workedHours;

    public WorkedHours() {
        this.workedHours = new HashMap<>();

    }

    public void addTime(Worker worker, Collection<LocalTime> time) {
        this.workedHours.put(worker, time);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<Worker, Collection<LocalTime>> getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(Map<Worker, Collection<LocalTime>> workedHours) {
        this.workedHours = workedHours;
    }
}
