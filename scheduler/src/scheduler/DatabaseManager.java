/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Elliot Knuth
 */
public class DatabaseManager {
        
    static private Connection conn = null;
    static final String DRIVER = "com.mysql.jdbc.Driver";
    static final String URL  = "";
    static final String USER = "";
    static final String PASSWORD = "";
    static private String user = null;
    
    public static void connectToDatabase() throws ClassNotFoundException, SQLException {
        
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL,USER,PASSWORD);
            System.out.println("Connected to database at " + URL);
    }
    public static String getUser() {
        return user;
    }
    
    public static void setUser(String userName) {
        if (user == null) {
            user = userName;
        }
    }
    public static boolean isUserLoggedIn() {
        return user != null;
    }
    
    public synchronized static void closeConnection() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized static Boolean verifyUsernameAndPassword(List<String> valuesForPreparedStatement) throws SQLException{
        ResultSet results = null;
        PreparedStatement statement = conn.prepareStatement("SELECT IF (? = password, true, false) AS result FROM user WHERE userName = ? AND active = 1");
        if (valuesForPreparedStatement != null && valuesForPreparedStatement.size() == 2) {
            statement.setString(2, valuesForPreparedStatement.get(0));
            statement.setString(1, valuesForPreparedStatement.get(1));

            results = statement.executeQuery();
            
        }
        
        if (results != null && results.next()) return results.getBoolean("result");
        else return false;
    }
    
    public synchronized static ResultSet populateCustomerMaps() throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT customerId, customerName, active, "
                                         +"address.addressId, address, address2, postalCode, phone, "
                                         +"city.cityId, city, country.countryId, country "
                                         +"FROM customer "
                                         +"RIGHT OUTER JOIN address ON customer.addressId = address.addressId "
                                         +"RIGHT OUTER JOIN city ON address.cityId = city.cityId "
                                         +"RIGHT OUTER JOIN country ON city.countryId = country.countryId");
        return statement.executeQuery();
    }
    
    public synchronized static ResultSet searchForCustomer(String customerId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT customerId, customerName, active, "
                                         +"address.addressId, address, address2, postalCode, phone, "
                                         +"city.cityId, city, country.countryId, country "
                                         +"FROM customer WHERE customerId = ?"
                                         +"RIGHT OUTER JOIN address ON customer.addressId = address.addressId "
                                         +"RIGHT OUTER JOIN city ON address.cityId = city.cityId "
                                         +"RIGHT OUTER JOIN country ON city.countryId = country.countryId");
        statement.setString(1, customerId);
        return statement.executeQuery();
    }
    
    public synchronized static ResultSet populateAppointmentMaps() throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT appointmentId, customerId, title, description, location, contact, url, start, end FROM appointment");
        return statement.executeQuery();
    }
    
    public synchronized static void add(Map<String, String> valuesFromCallingFunction, String preparedStatement) throws SQLException {
        Map<String, String> valuesForPreparedStatement = new LinkedHashMap<>(valuesFromCallingFunction);
        
        PreparedStatement statement = conn.prepareStatement(preparedStatement);
        if (valuesForPreparedStatement != null && valuesForPreparedStatement.size() > 0) {
            AtomicInteger iterator = new AtomicInteger(0);
            valuesForPreparedStatement.forEach((key, value) -> {
                try {
                    if (key.contains("Id")) {
                        statement.setInt(iterator.incrementAndGet(), Integer.valueOf(value));
                    }
                    else {
                        statement.setString(iterator.incrementAndGet(), value);
                    }
                        
                } catch (SQLException ex) {
                   Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            statement.setString(iterator.incrementAndGet(), user);
            statement.setString(iterator.incrementAndGet(), user);
            statement.executeUpdate();
        }
    }
    
    public synchronized static void update(Map<String, String> valuesFromCallingFunction, String preparedStatement) throws SQLException {
        Map<String, String> valuesForPreparedStatement = new LinkedHashMap<>(valuesFromCallingFunction);
        
        PreparedStatement statement = conn.prepareStatement(preparedStatement);
        if (valuesForPreparedStatement.size() > 0) {
            AtomicInteger iterator = new AtomicInteger(0);
            valuesForPreparedStatement.forEach((key, value) -> {
                try {
                    if (iterator.get() < valuesForPreparedStatement.size() - 1) {
                        if (key.contains("Id")) {
                            statement.setInt(iterator.incrementAndGet(), Integer.valueOf(value));
                        }
                        else {
                            statement.setString(iterator.incrementAndGet(), value);
                        }
                    }
                    else {
                        statement.setInt((iterator.incrementAndGet() + 1), Integer.valueOf(value));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            statement.setString(iterator.get(), user);
            statement.executeUpdate();
        }
    }
}
