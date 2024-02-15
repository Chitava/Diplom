package chitava.diplom.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class WorkedHours {
    Long id;
    private Map<String, Collection<Double>> workedHours;

    public void addTime(String name, Collection<Double> time) {
        this.workedHours.put(name, time);
    }
}
