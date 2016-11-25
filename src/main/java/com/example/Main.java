package com.example;

import com.example.dao.TaskDao;
import com.example.dao.TaskDaoImpl;
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

    public static void main(String[] args) {

        // initialization part

        String hibernateConfigurationFile = "hibernate.cfg.xml";

        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("java Api <port> <hibernate.cfg.xml>");
                System.exit(1);
            }
            // it is not supposed to run other than testing
            // so we won't check for NumberFormatException
            port(Integer.parseInt(args[0]));
            hibernateConfigurationFile = args[1];
        }

        taskDao = new TaskDaoImpl(hibernateConfigurationFile);

        // actual request methods

        // index page: GET all tasks
        get(INDEX_PAGE_ALL_TASKS,
                CONTENT_TYPE,
                (request, response) -> taskDao.findAll(),
                gson::toJson
        );
    }
}
