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
 * Интерфейс для работы с хранилищем данных о посещении и зарплаты в базе данных
 */

@Repository
@AllArgsConstructor
@NoArgsConstructor
public class WorkedHourRepository{
    @Autowired
    private JdbcTemplate jt;

    public String createTable(String name) {
        String query = "create table '"+ name +"' (name VARCHAR(100), a DATETIME, b DATETIME, c DATETIME, d DATETIME," +
                " e DATETIME, f DATETIME, g DATETIME, h DATETIME, i DATETIME, j DATETIME, k DATETIME, l DATETIME, " +
                "m DATETIME, n DATETIME, o DATETIME, p DATETIME, q DATETIME, r DATETIME, s DATETIME, t DATETIME, " +
                "u DATETIME, v DATETIME, w DATETIME, x DATETIME, y DATETIME, z DATETIME, aa DATETIME, ab DATETIME," +
                " ac DATETIME , ad DATETIME, ae DATETIME);";
        try {
            jt.execute(query);
        }catch (Exception e){
            return e.getMessage();
        }return null;
    }

    public void addTime (String tableName, WorkedHours data){

    }
}


