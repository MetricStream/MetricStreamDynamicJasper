package ar.com.fdvs.dj.test.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

public class TestSchema {

    private static final Log log = LogFactory.getLog(TestSchema.class);
    private static boolean initialized = false;
    
    public static void buildConfiguration() {
        try {
            // Get the actual module build directory from classpath resources
            java.net.URL hsqlResourceUrl = TestSchema.class.getResource("/hsql");
            String buildDir;
            if (hsqlResourceUrl != null) {
                // Resource found in classpath - use its parent directory
                java.io.File hsqlDir = new java.io.File(hsqlResourceUrl.getPath());
                buildDir = hsqlDir.getAbsolutePath();
            } else {
                // Fallback: construct path from user.dir
                String userDir = System.getProperty("user.dir");
                buildDir = userDir + java.io.File.separator + "build" + java.io.File.separator + "resources" + java.io.File.separator + "test" + java.io.File.separator + "hsql";
            }
            
            java.io.File dbDir = new java.io.File(buildDir);
            if (!dbDir.exists()) {
                dbDir.mkdirs();
                log.info("Created database directory: " + dbDir.getAbsolutePath());
            }
            
            log.info("Database directory: " + dbDir.getAbsolutePath());
            
            // Initialize database before building configuration (only once)
            if (!initialized) {
                // Delete old database files before first initialization to ensure clean state
                deleteOldDatabaseFiles(buildDir);
                initializeTestDatabase(buildDir);
                initialized = true;
                log.info("Database initialized successfully");
            } else {
                log.info("Database already initialized, skipping initialization");
            }
            
            URL configFile = TestSchema.class.getResource("/hibernate/customer.hbm.xml");
            log.info("Hibernate config file: " + configFile.toString());

            Configuration config = new Configuration().
            setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect").
            setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver").
            setProperty("hibernate.connection.url", "jdbc:hsqldb:file:" + dbDir.getAbsolutePath() + java.io.File.separator + "test_dj_db;sql.syntax_pgs=true").
            setProperty("hibernate.connection.username", "sa").
            setProperty("hibernate.connection.password", "").
            setProperty("hibernate.connection.pool_size", "5").
            setProperty("hibernate.connection.autocommit", "true").
            setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider").
            setProperty("hibernate.show_sql", "false").
            addFile(configFile.getFile());

            HibernateUtil.setSessionFactory(config.buildSessionFactory());
        } catch (Exception e) {
            log.error("Error building Hibernate configuration", e);
            throw new RuntimeException(e);
        }
    }

    private static void initializeTestDatabase(String dbPath) throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        String dbUrl = "jdbc:hsqldb:file:" + dbPath + java.io.File.separator + "test_dj_db;sql.syntax_pgs=true;create=true;shutdown=true";
        Connection conn = DriverManager.getConnection(dbUrl, "sa", "");
        conn.setAutoCommit(false);
        
        try (java.io.InputStream is = TestSchema.class.getResourceAsStream("/hsql/init.sql")) {
            if (is != null) {
                log.info("Found init.sql, starting database initialization");
                
                // Read entire file as string
                byte[] bytes = is.readAllBytes();
                String scriptContent = new String(bytes, "UTF-8");
                log.info("Script file size: " + bytes.length + " bytes");
                
                // Split by semicolon to handle multi-line statements
                String[] statements = scriptContent.split(";");
                log.info("Total statements in script: " + statements.length);
                
                int executedCount = 0;
                int skippedCount = 0;
                
                for (int stmtNum = 0; stmtNum < statements.length; stmtNum++) {
                    String sql = statements[stmtNum].trim();
                    
                    if (sql.isEmpty() || sql.startsWith("--")) {
                        continue;
                    }
                    
                    if (!sql.isEmpty()) {
                        try (java.sql.Statement stmt = conn.createStatement()) {
                            log.info("Executing SQL (statement " + (stmtNum + 1) + "): " + sql.substring(0, Math.min(80, sql.length())));
                            stmt.execute(sql);
                            log.debug("Successfully executed SQL (statement " + (stmtNum + 1) + ")");
                            executedCount++;
                        } catch (Exception e) {
                            log.error("Error executing SQL (statement " + (stmtNum + 1) + "): " + sql, e);
                            throw new RuntimeException("Failed to execute SQL at statement " + (stmtNum + 1) + ": " + sql, e);
                        }
                    }
                }
                log.info("Database initialization completed. Executed: " + executedCount + " statements, Skipped: " + skippedCount + " statements");
                conn.commit();
                log.info("Database changes committed successfully");
            } else {
                log.error("init.sql not found in classpath!");
                throw new RuntimeException("init.sql not found in classpath!");
            }
        } finally {
            try {
                conn.close();
                log.info("Database connection closed");
            } catch (Exception e) {
                log.warn("Error closing database connection", e);
            }
        }
    }

    private static void deleteOldDatabaseFiles(String dbPath) {
        try {
            java.io.File dbDir = new java.io.File(dbPath);
            if (dbDir.exists()) {
                log.info("Cleaning up old database files in: " + dbDir.getAbsolutePath());
                String[] files = dbDir.list((dir, name) -> name.startsWith("test_dj_db"));
                if (files != null && files.length > 0) {
                    for (String file : files) {
                        java.io.File f = new java.io.File(dbDir, file);
                        boolean deleted = f.delete();
                        if (deleted) {
                            log.info("Deleted old database file: " + f.getAbsolutePath());
                        } else {
                            log.warn("Failed to delete database file: " + f.getAbsolutePath() + " (file may be in use)");
                        }
                    }
                } else {
                    log.info("No old database files found to delete");
                }
            } else {
                log.info("Database directory does not exist yet: " + dbPath);
            }
        } catch (Exception e) {
            log.error("Error deleting old database files", e);
        }
    }

}
