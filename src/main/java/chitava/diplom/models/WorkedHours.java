package chitava.diplom.models;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
public class WorkedHours {
    private Worker worker;
    private Collection<LocalDateTime> workedDaysHours;


    public void addTime(LocalDateTime time) {
        this.workedDaysHours.add(time);
    }
}
