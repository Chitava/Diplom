package chitava.diplom.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "hours")
public class WorkedHours {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
    @OneToOne(mappedBy = "hours")
    private Worker worker;

    private List<LocalTime> times = new ArrayList<>();

    public LocalTime getTime(int id) {
        return times.get(id);
    }

    public void setTimes(LocalTime times) {
        this.times.add(times);
    }
}
