package chitava.diplom.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "hours")
public class WorkedHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @OneToOne
    @JoinColumn(name = "id")
    private Worker worker;

    private List<LocalDateTime> times = new ArrayList<>();

    public LocalDateTime getTime(int id) {
        return times.get(id);
    }

    public void setTimes(LocalDateTime times) {
        this.times.add(times);
    }

    public List<LocalDateTime> getTimes(){
        return this.times;
    }

    public void setTimes(List<LocalDateTime> times) {
        this.times = times;
    }
}
