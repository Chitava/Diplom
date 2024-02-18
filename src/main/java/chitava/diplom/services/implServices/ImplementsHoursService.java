package chitava.diplom.services.implServices;


import chitava.diplom.models.WorkedHours;
import chitava.diplom.models.Worker;
import chitava.diplom.repositorys.HoursRepository;
import chitava.diplom.services.HoursService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.catalina.mapper.Mapper;
import org.hibernate.query.results.ResultSetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;


/**
 * Класс обработки данных рабочего времени сотрудников
 */
@Service
@AllArgsConstructor
@NoArgsConstructor
public class ImplementsHoursService implements HoursService {

    @Autowired
    HoursRepository repository;

    @Autowired
    private ObjectMapper mapper;


    public List<WorkedHours> getAll() throws SQLException {

        Collection res = repository.getAllTimes();
        for (Object o: res) {
            System.out.println(ToStringBuilder.reflectionToString(country));

        }
        return null;
    }



}
