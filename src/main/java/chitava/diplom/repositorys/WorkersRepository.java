package chitava.diplom.repositorys;
import chitava.diplom.models.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Интерфейс для работы с хранилищем сотрудников в базе данных
 */
@Repository
public interface WorkersRepository extends JpaRepository<Worker, Long> {

    Optional<Worker> findById(Long id);

    Worker findByName(String name);


}


