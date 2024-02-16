package chitava.diplom.repositorys;

import chitava.diplom.models.WorkedHours;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * Интерфейс для работы с хранилищем сотрудников в базе данных
 */

@Repository
@AllArgsConstructor
@NoArgsConstructor
public class WorkedHourRepository{
    @Autowired
    private JdbcTemplate jt;

    public void createTable(String name) {
        jt.execute("create table (name) (id int, name varchar(20));", String.class, name);


    }
}


