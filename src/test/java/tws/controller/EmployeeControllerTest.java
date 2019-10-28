package tws.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import tws.entity.Employee;

import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void shouldNotInsertWhenCurrentInsert() throws Exception {
        Employee employee = new Employee() {{
            setName("testname");
            setAge("22");
        }};

        String postData = mapper.writeValueAsString(employee);

        //when
        IntStream.range(1, 10).parallel().forEach((index)
                        -> {
                        given().body(postData).contentType(ContentType.JSON)
                                .port(port)
                                .post("/employees");
                }
        );

        int count = JdbcTestUtils.countRowsInTable(jdbcTemplate, "employee");
        Assert.assertEquals(1, count);
    }
}
