package com.example.dao;

import com.example.model.Task;

import java.util.List;

public interface TaskDao {

    Task findOne(Long id);
    List<Task> findAll();

    void saveOrUpdate(Task task);
    void delete(Task task);
    void delete(Long id);

    Long getMaxId();

    void closeDatabase();
}
