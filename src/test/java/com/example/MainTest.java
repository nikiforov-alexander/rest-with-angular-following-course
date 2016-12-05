package com.example;

import com.example.dao.TaskDao;
import com.example.exception.ApiError;
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
    private static Gson gson;
    private static TaskDao taskDao;

    // Test preparation methods

    @BeforeClass
    public static void startServer() {
        // run our App with other than in real App port and JDBC url
        // testing database
        String[] args = {PORT, "hibernate-test.cfg.xml"};
        Main.main(args);
        taskDao = Main.getTaskDao();
        gson = Main.getGson();
        // add 5 test tasks in the beginning
        for (int i = 1; i <= 5; i++) {
            Task task = new Task();
            task.setName("task " + i);
            taskDao.saveOrUpdate(task);
        }

        // wait before Spark server is up
        Spark.awaitInitialization();
    }

    @Before
    public void setUp() throws Exception {
        apiClient = new ApiClient("http://localhost:" + PORT);
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

    // actual tests

    @Test
    public void getRequestToListAllTasksWorks() throws Exception {
        // Given taskDao with some tasks

        // When GET request to INDEX_PAGE_ALL_TASKS is made
        ApiResponse apiResponse =
                apiClient.request("GET",
                        INDEX_PAGE_ALL_TASKS);

        // Then status should be 200
        assertThat(apiResponse).hasFieldOrPropertyWithValue(
                "status", 200
        );

        // Then size of tasks from JSON should be
        // equal to numberOfTasks in DAO
        Task[] tasks = gson.fromJson(apiResponse.getBody(), Task[].class);
        assertThat((long) tasks.length)
                .isEqualTo(
                taskDao.count()
        );
    }

    @Test
    public void deletingTaskWorks() throws Exception {
        // Given 1 new test task is added to database to be
        // deleted, and numberOfTasks calculated before addition
        // of task to be deleted
        Long numberOfTasksBeforeAddition = taskDao.count();
        taskDao.saveOrUpdate(new Task("test task to delete"));
        Long idOfNewlyAddedTask = taskDao.getMaxId();

        // When DELETE request to INDEX_PAGE_ALL_TASKS
        // + "/idOfNewlyAddedTask" is made
        ApiResponse apiResponse =
                apiClient.request("DELETE",
                        INDEX_PAGE_ALL_TASKS
                                + "/" + idOfNewlyAddedTask);

        // Then status should be 200
        assertThat(apiResponse).hasFieldOrPropertyWithValue(
                "status", 200
        );
        // Then body should be null
        assertThat(apiResponse).hasFieldOrPropertyWithValue(
                "body", "null"
        );

        // Then number of tasks should be the same as before
        assertThat(taskDao.count())
                .isEqualTo(numberOfTasksBeforeAddition);

        // Then task with idOfNewlyAddedTask
        // should no longer exist
        assertThat(
               taskDao.findOne(idOfNewlyAddedTask)
        ).isNull();
    }

    @Test
    public void deletingNonExistingTaskThrowsException() throws Exception {
        // Given taskDao without task 123
        assertThat(taskDao.exists(123L)).isFalse();

        // When DELETE request to INDEX_PAGE_ALL_TASKS
        // + "/123" is made
        ApiResponse apiResponse =
                apiClient.request("DELETE",
                        INDEX_PAGE_ALL_TASKS
                                + "/" + 123);

        // Then status should be 404
        assertThat(apiResponse).hasFieldOrPropertyWithValue(
                "status", 404
        );
    }
}