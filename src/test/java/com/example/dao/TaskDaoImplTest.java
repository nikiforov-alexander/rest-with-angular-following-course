package com.example.dao;

import com.example.exception.NotFoundException;
import com.example.model.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskDaoImplTest {

    private final String TEST_HIBERNATE_CONFIGURATION_FILE =
            "hibernate-test.cfg.xml";

    private TaskDao taskDao;

    @Before
    public void setUp() throws Exception {
        taskDao = new TaskDaoImpl(TEST_HIBERNATE_CONFIGURATION_FILE);
    }

    @After
    public void tearDown() throws Exception {
        taskDao.closeDatabase();
    }

    // helpful private methods
    private void addTestTasksToDatabase(int numberOfTestTasks) {
        for (int i = 1; i <= numberOfTestTasks; i++) {
            Task task = new Task();
            task.setName("task " + i);
            taskDao.saveOrUpdate(task);
        }
    }

    @Test
    public void taskCanBeSaved() throws Exception {
        // Given test task to be saved
        Task task = new Task();

        // When we save task
        taskDao.saveOrUpdate(task);

        // Then task could be found in hibernate
        assertThat(taskDao.findOne(1L)).isEqualTo(task);
    }

    @Test
    public void findAllShouldReturnAllTasks() throws Exception {
        // Given database with 5 tasks
        addTestTasksToDatabase(5);

        // When we execute findAll
        List<Task> tasks = taskDao.findAll();

        // Then list with 5 tasks should be returned
        assertThat(tasks).hasSize(5);
    }

    @Test
    public void taskCanBeDeleted() throws Exception {
        // Given that we have one Task in dao
        Task task = new Task();
        taskDao.saveOrUpdate(task);
        assertThat(taskDao.findOne(1L)).isNotNull();

        // When we delete task
        taskDao.delete(task);

        // Then task should be deleted
        assertThat(taskDao.findOne(1L)).isNull();
    }

    @Test
    public void taskCanBeDeletedById() throws Exception {
        // Given that we have one Task in dao
        Task task = new Task();
        taskDao.saveOrUpdate(task);
        assertThat(taskDao.findOne(1L)).isNotNull();

        // When we delete task by id
        taskDao.delete(1L);

        // Then task should be deleted
        assertThat(taskDao.findOne(1L)).isNull();
    }

    @Test
    public void taskCanBeUpdated() throws Exception {
        // Given that we add one Task to dao
        Task task = new Task();
        taskDao.saveOrUpdate(task);

        // When we find task, change the name of the saved task
        // and update task
        Task savedTask = taskDao.findOne(1L);
        savedTask.setName("new name");
        taskDao.saveOrUpdate(savedTask);

        // Then name of the savedTask should be "new name"
        // but id should be still 1L
        Task updatedTask = taskDao.findOne(1L);
        assertThat(updatedTask).hasFieldOrPropertyWithValue(
                "name", "new name"
        );
        assertThat(updatedTask).hasFieldOrPropertyWithValue(
                "id", 1L
        );
    }

    @Test
    public void findMaxIdWorksCorrectly() throws Exception {
        // Given that we have 5 added Tasks in dao
        addTestTasksToDatabase(5);

        // When we call getMaxId()
        // Then maxId should be 5
        assertThat(taskDao.getMaxId()).isEqualTo(5L);
    }

    @Test
    public void countTasksWorksCorrectly() throws Exception {
        // Given that we have 5 added Tasks in dao
        addTestTasksToDatabase(5);

        // When we call count() method
        // Then 5 should be returned
        assertThat(taskDao.count()).isEqualTo(5L);
    }

    @Test
    public void existsReturnsTrueIfTaskExists() throws Exception {
        // Given that we have 1 task added in dao
        addTestTasksToDatabase(1);

        // When we call exists()
        // Then true should be returned
        assertThat(taskDao.exists(1L)).isTrue();
    }

    @Test
    public void existsReturnsFalseIfTaskDoesNotExist() throws Exception {
        // Given that we have no tasks added in dao

        // When we call exists()
        // Then false should be returned
        assertThat(taskDao.exists(1L)).isFalse();
    }

    @Test(expected = NotFoundException.class)
    public void deleteNonExistingTaskThrowsException() throws Exception {
        // Given empty dao
        // When we delete Task with id 123L
        // Then NotFoundException should be thrown
        taskDao.delete(123L);
    }
}