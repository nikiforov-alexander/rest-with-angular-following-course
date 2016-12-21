package com.example.dao;

import com.example.exception.NotFoundException;
import com.example.model.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.util.List;

public class TaskDaoImpl implements TaskDao {

    private final SessionFactory sessionFactory;

    /**
     * build Hibernate's sessionFactory with
     * provided in constructor {@literal hibernateConfigurationFile}
     * see <a href="http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#bootstrap-native-metadata">
     *     Hibernate User's Guide 5.2
     *     </a>
     * @param hibernateConfigurationFile : {@literal String} that
     *                                   simply provides configuration
     *                                   file name. It will be used in
     *                                   for testing purposes. Default
     *                                   is "hibernate.cfg.xml"
     * @return {@literal SessionFactory} with which we can
     * open sessions and make transactions
     */
    private SessionFactory
    buildSessionFactory(String hibernateConfigurationFile) {
        final ServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder().configure(
                        hibernateConfigurationFile
                ).build();
        return new MetadataSources(serviceRegistry)
                .buildMetadata()
                .buildSessionFactory();
    }

    // constructors

    public TaskDaoImpl(String hibernateConfigurationFile) {
        this.sessionFactory =
                buildSessionFactory(hibernateConfigurationFile);
    }

    // @Override methods

    @Override
    public Task findOne(Long id) {
        Session session = sessionFactory.openSession();
        Task task = session.get(Task.class, id);
        session.close();
        return task;
    }

    @Override
    public List<Task> findAll() {
        Session session = sessionFactory.openSession();

        List<Task> tasks = session.createQuery(
                "SELECT t from Task t",
                Task.class
        ).list();

        session.close();
        return tasks;
    }

    @Override
    public void saveOrUpdate(Task task) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(task);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(Task task) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(task);
        session.getTransaction().commit();
        session.close();
    }

    /**
     * Deletes task by {@code id},
     * checking before whether {@code Task}
     * exists using {@link #exists(Long)} method
     * @param id : id of the {@code Task} to be
     *           deleted
     * @throws NotFoundException : is thrown when
     * {@link #exists(Long)} method returns {@code false}
     */
    @Override
    public void delete(Long id) throws NotFoundException {
        if (!exists(id)) {
            throw new NotFoundException("Task with this id does not exist");
        }
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createQuery(
                "DELETE FROM Task WHERE id = :id"
        ).setParameter("id", id)
        .executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    /**
     * Checks if {@code Task} exists in DAO
     * @param id : id of the task that is checked for
     *           existence
     * @return {@code true} if {@code Task} exists, and
     * false otherwise
     */
    @Override
    public boolean exists(Long id) {
        Session session = sessionFactory.openSession();
        Long numberOfTasksWithThisId = (Long) session.createQuery(
                "SELECT COUNT(id) FROM Task WHERE id = :id"
        ).setParameter("id", id)
        .getSingleResult();
        session.close();
        return numberOfTasksWithThisId != 0;
    }

    /**
     * finds max {@code id} of all possible
     * {@code Task}-s. Used in testing when we
     * add and then remove something, to keep track
     * of the highest added {@code Task}
     * @return : max {@code id} : the id of latest added task
     */
    @Override
    public Long getMaxId() {
        Session session = sessionFactory.openSession();
        Long maxId = (Long) session.createQuery(
                "SELECT MAX(id) FROM Task"
        ).getSingleResult();
        session.close();
        return maxId;
    }

    /**
     * calculates number of tasks in DAO. The SQL
     * function {@code COUNT} is used with {@literal id}
     * column
     * @return : {@code Long count} : number of tasks in DAO
     */
    @Override
    public Long count() {
        Session session = sessionFactory.openSession();
        Long count = (Long) session.createQuery(
                "SELECT COUNT(id) FROM Task"
        ).getSingleResult();
        session.close();
        return count;
    }

    /**
     * Finds the latest added {@code Task} using
     * @see #getMaxId()
     * @see #findOne(Long)
     * @return latest added Task or {@code null} if
     * DAO is empty
     */
    @Override
    public Task findLastAddedTask() {
        if (exists(getMaxId())) {
            return findOne(getMaxId());
        } else {
            return null;
        }
    }

    @Override
    public void closeDatabase() {
        sessionFactory.close();
    }
}
