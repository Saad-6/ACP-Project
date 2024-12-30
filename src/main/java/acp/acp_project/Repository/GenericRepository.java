package acp.acp_project.Repository;

import acp.acp_project.Entities.Action;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import acp.acp_project.DbContext.HibernateUtil;

import java.util.List;

public class GenericRepository<T> implements IGenericRepository<T> {
    private final Class<T> entityType;

    public GenericRepository(Class<T> entityType) {
        this.entityType = entityType;
    }

    @Override
    public T create(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<T> getAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.createQuery("from " + entityType.getName(), entityType).list();
        } finally {
            session.close();
        }
    }

    @Override
    public T getById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.get(entityType, id);
        } finally {
            session.close();
        }
    }

    public T getActionById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            T entity = session.get(entityType, id);
            if (entity instanceof Action) {
                Hibernate.initialize(((Action) entity).getActionParameters());
            }
            return entity;
        } finally {
            session.close();
        }
    }

    @Override
    public T update(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            T entity = session.get(entityType, id);
            if (entity != null) {
                session.delete(entity);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void initializeDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // You can add some initial data here if needed
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<T> getByHotFolder(int hotFolderId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.createQuery("from " + entityType.getName() + " where hotFolder.id = :hotFolderId", entityType)
                    .setParameter("hotFolderId", hotFolderId)
                    .list();
        } finally {
            session.close();
        }
    }

    public List<T> getByTask(int taskId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.createQuery("from " + entityType.getName() + " where task.id = :taskId", entityType)
                    .setParameter("taskId", taskId)
                    .list();
        } finally {
            session.close();
        }
    }
}