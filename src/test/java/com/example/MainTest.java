package com.example;

import com.example.dao.TaskDao;
import com.example.model.Task;
import com.example.test_helpers.ApiClient;
import com.example.test_helpers.ApiResponse;
import com.google.gson.Gson;
import org.junit.*;
import spark.Spark;

import static org.assertj.core.api.Assertions.assertThat;
import static com.example.WebConstants.*;

public class MainTest {

    // members used in tests

    private static final String PORT = "4568";
    private ApiClient apiClient;
    private Gson gson;
    private TaskDao taskDao;

    // Test preparation methods

    @BeforeClass
    public static void startServer() {
        // run our App with other than in real App port and JDBC url
        // testing database
        String[] args = {PORT, "hibernate-test.cfg.xml"};
        Main.main(args);

        // wait before Spark server is up
        Spark.awaitInitialization();
    }

    @Before
    public void setUp() throws Exception {
        apiClient = new ApiClient("http://localhost:" + PORT);
        taskDao = Main.getTaskDao();
        gson = Main.getGson();
    }

    // Stop Spark Server after all tests
    // and close database. Because it is aka "integration test"
    // we have to close database at the end, not using
    // @After tearDown method.
    @AfterClass
    public static void stopServer() {
        Main.getTaskDao().closeDatabase();
        Spark.stop();
    }

    // private helper methods used in tests
    private void addTestTasksToDatabase(int numberOfTasks) {
        for (int i = 0; i < numberOfTasks; i++) {
            Task task = new Task();
            task.setName("task " + i);
            taskDao.saveOrUpdate(task);
        }
    }

    // actual tests

    @Test
    public void getRequestToListAllTasksWorks() throws Exception {
        // Given 5 added test tasks to database
        addTestTasksToDatabase(5);

        // When GET request to INDEX_PAGE_ALL_TASKS is made
        ApiResponse apiResponse =
                apiClient.request("GET",
                        INDEX_PAGE_ALL_TASKS);

        // Then status should be 200
        assertThat(apiResponse).hasFieldOrPropertyWithValue(
                "status", 200
        );

        // Then 5 tasks should be returned
        Task[] tasks = gson.fromJson(apiResponse.getBody(), Task[].class);
        assertThat(tasks.length).isEqualTo(5);
    }
}