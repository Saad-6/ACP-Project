package acp.acp_project.DbContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbContext {
    String url = "jdbc:sqlserver://DESKTOP-F925KJB:1433;Database=HotelDb;encrypt=true;trustServerCertificate=true;";

    String username = "user";
    String password = "testuser";

   public DbContext(){
       try{
           Connection connection = DriverManager.getConnection(url,username,password);
           System.out.println("Connected to SQL Server successfully!");

       }
       catch (SQLException e){
           System.err.println("Connection failed!");
           e.printStackTrace();
       }
   }
}
