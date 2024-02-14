package chitava.diplom.models;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
public class WorkedHours {
    @MapsId
    @JoinColumn(name = "id")
    private Worker worker;
    private Collection workedDaysHours;
}
