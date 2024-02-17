package chitava.diplom.repositorys;

import chitava.diplom.models.WorkedHours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoursRepository extends JpaRepository<WorkedHours, Long> {
}
