package chitava.diplom.services;

import chitava.diplom.models.WorkedHours;
import chitava.diplom.repositorys.HoursRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для работы с классом обработки рабочего времени в базе данных
 */
@Service
public interface HoursService {
    List<WorkedHours> getAll() throws SQLException;

}
