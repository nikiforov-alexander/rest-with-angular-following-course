package com.example.dao;

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

    @Override
    public void delete(Long id) {
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

    @Override
    public Long count() {
        Session session = sessionFactory.openSession();
        Long count = (Long) session.createQuery(
                "SELECT COUNT(id) FROM Task"
        ).getSingleResult();
        session.close();
        return count;
    }

    @Override
    public void closeDatabase() {
        sessionFactory.close();
    }
}
