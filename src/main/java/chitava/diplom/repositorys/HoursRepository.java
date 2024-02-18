package chitava.diplom.repositorys;

import chitava.diplom.models.WorkedHours;
import org.hibernate.query.results.ResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HoursRepository extends JpaRepository<WorkedHours, Long> {

    @Query(value = "SELECT * FROM hours;", nativeQuery = true)
    Collection getAllTimes();


}
