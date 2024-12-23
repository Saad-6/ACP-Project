package acp.acp_project.DbContext;

import acp.acp_project.Entities.Action;
import acp.acp_project.Entities.HotFolder;
import acp.acp_project.Entities.Task;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(HotFolder.class);
            configuration.addAnnotatedClass(Task.class);
            configuration.addAnnotatedClass(Action.class);
            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
