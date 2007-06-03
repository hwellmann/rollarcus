package org.apache.roller.weblogger.business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.config.RollerConfig;

/**
 * Encapsulates Roller database configuration via JDBC properties or JNDI.
 *
 * <p>Reads configuration properties from RollerConfig:</p>
 * <pre>
 * # Specify database configuration type of 'jndi' or 'jdbc'
 * database.configurationType=jndi
 * 
 * # For database configuration type 'jndi',this will be used
 * database.jndi.name=jdbc/rollerdb
 * 
 * # For database configuration type of 'jdbc', you MUST override these
 * database.jdbc.driverClass=
 * database.jdbc.connectionURL=
 * database.jdbc.username=
 * database.jdbc.password=
 * </pre>
 */
@com.google.inject.Singleton
public abstract class DatabaseProvider  {
    private static Log log = LogFactory.getLog(DatabaseProvider.class);
    public enum ConfigurationType {JNDI_NAME, JDBC_PROPERTIES;}
    
    private static DatabaseProvider databaseProvider = null;
    
    protected ConfigurationType type = ConfigurationType.JNDI_NAME; 
    
    protected String jndiName = null; 
    protected DataSource dataSource = null;
    
    protected String jdbcDriverClass = null;
    protected String jdbcConnectionURL = null;
    protected String jdbcPassword = null;
    protected String jdbcUsername = null;
    protected Properties props = null;
    
    // Singleton
    protected DatabaseProvider() {}
    
    
    // Needed by HibernateConnectionProvider, it's not instantiated by Guice
    public static DatabaseProvider getDatabaseProvider() {
        return databaseProvider;
    }
    
    
    @com.google.inject.Inject
    private void setDatabaseProvider(DatabaseProvider dbprovider) {
        databaseProvider = dbprovider;
    }
    
    
    protected void init(
        ConfigurationType type,
        String jndiName, 
        String jdbcDriverClass,
        String jdbcConnectionURL,
        String jdbcUsername,
        String jdbcPassword) throws WebloggerException {        
        
        // init now so we fail early
        if (getType() == ConfigurationType.JDBC_PROPERTIES) {
            log.info("Using 'jdbc' properties based configuration");
            try {
                Class.forName(getJdbcDriverClass());
            } catch (ClassNotFoundException ex) {
                throw new WebloggerException(
                   "Cannot load specified JDBC driver class [" +getJdbcDriverClass()+ "]", ex);
            }
            if (getJdbcUsername() != null || getJdbcPassword() != null) {
                props = new Properties();
                if (getJdbcUsername() != null) props.put("user", getJdbcUsername());
                if (getJdbcPassword() != null) props.put("password", getJdbcPassword());
            }
        } else {
            log.info("Using 'jndi' based configuration");
            String name = "java:comp/env/" + getJndiName();
            try {
                InitialContext ic = new InitialContext();
                dataSource = (DataSource)ic.lookup(name);
            } catch (NamingException ex) {
                throw new WebloggerException(
                    "ERROR looking up data-source with JNDI name: " + name, ex);
            }            
        }
        try { 
            Connection testcon = getConnection();
            testcon.close();
        } catch (Throwable t) {
            throw new WebloggerException("ERROR unable to obtain connection", t);
        }
    }
    
    
    /**
     * Get database connection from data-source or driver manager, depending 
     * on which is configured.
     */
    public Connection getConnection() throws SQLException {
        if (getType() == ConfigurationType.JDBC_PROPERTIES) {
            return DriverManager.getConnection(getJdbcConnectionURL(), props);
        } else {
            return dataSource.getConnection();
        }
    } 

    public ConfigurationType getType() {
        return type;
    }

    public String getJndiName() {
        return jndiName;
    }

    public String getJdbcDriverClass() {
        return jdbcDriverClass;
    }

    public String getJdbcConnectionURL() {
        return jdbcConnectionURL;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }
}
