package com.example;

import com.example.dao.TaskDao;
import com.example.dao.TaskDaoImpl;
import com.example.model.Task;
import com.google.gson.Gson;

import static com.example.WebConstants.CONTENT_TYPE;
import static com.example.WebConstants.INDEX_PAGE_ALL_TASKS;
import static spark.Spark.*;

public class Main {

    private static TaskDao taskDao;
    private static Gson gson = new Gson();

    static TaskDao getTaskDao() {
        return taskDao;
    }

    static Gson getGson() {
        return gson;
    }

    /**
     * Adds {@literal numberOfTasks} to {@link #taskDao}
     * for now only with name "Task 1" to "Task numberOfTasks"
     * @param numberOfTasks : number of tasks to save
     */
    static void addSomeTestTasksToDao(int numberOfTasks) {
        for (int i = 1; i < 5; i++) {
            Task task = new Task();
            task.setName("Task " + i);
            taskDao.saveOrUpdate(task);
        }
    }

    public static void main(String[] args) {

        // this way we should expose our angular related
        // files
        staticFileLocation("/public");

        // initialization part

        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("java Api <port> <hibernate.cfg.xml>");
                System.exit(1);
            }
            // it is not supposed to run other than testing
            // so we won't check for NumberFormatException
            port(Integer.parseInt(args[0]));
            taskDao = new TaskDaoImpl(args[1]);
        } else {
            // we add test tasks only for production for now
            // to leave testing database empty
            taskDao = new TaskDaoImpl("hibernate.cfg.xml");
            addSomeTestTasksToDao(5);
        }

        // actual request methods

        // index page: GET all tasks
        get(INDEX_PAGE_ALL_TASKS,
                CONTENT_TYPE,
                (request, response) -> taskDao.findAll(),
                gson::toJson
        );
    }
}
