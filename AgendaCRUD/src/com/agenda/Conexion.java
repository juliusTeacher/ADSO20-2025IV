package com.agenda;
import java.sql.*;

public class Conexion {
    
    Connection con = null;
    
    public Connection establerConexion(){
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:Empresa.db");
            System.out.println("Base de datos conectada!!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error "+e.toString());
        }
        return con;
    }
    
    public void cierreConexion(){
        try {
            con.close();
            //System.out.println("Se cerró correctamente!!");
        } catch (SQLException e) {
            System.out.println("No se cerró correctamente!! "+e.toString());
        }
    }
    
}
