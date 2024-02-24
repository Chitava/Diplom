package chitava.diplom.services.implServices;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ConnectToDB {


    private Connection connection;

    @Value("${URL_DB}")
    private  String URL;
    @Value("${USER_DB}")
    private  String USER;
    @Value("${PASS_DB}")
    private  String PASS;

    /**
     * Метод получения соединения с БД
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConnection(){
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            return connection;
        } catch (SQLException e) {
            System.out.println("Connection Failed : " + e.getMessage());
        }return null;
    }

}
