package ar.com.fdvs.dj.test.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class HibernateUtil {
	   private static SessionFactory factory;
	   private static final Log log = LogFactory.getLog(HibernateUtil.class);

	    public static synchronized Session getSession() {
	        if (factory == null) {
	            // Ensure database is initialized and SessionFactory is built
	            // This delegates to TestSchema which handles both database init and SessionFactory creation
	            TestSchema.buildConfiguration();
	        }
	        return factory.openSession();
	    }

	    public static void setSessionFactory(SessionFactory factory) {
	        HibernateUtil.factory = factory;
	    }

    public static SessionFactory getFactory() {
        return factory;
    }
}
