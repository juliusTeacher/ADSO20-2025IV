package com.mycompany.agendacrud;

import java.awt.HeadlessException;
import java.sql.*;

public class Conexion {
    
    Connection con = null;
        
    String bd = "Empresa.db";
    String cadena = "jdbc:sqlite:"+System.getProperty("user.dir")+"/"+bd;
    
    
    public Connection establecerConexion(){     
  
        try {
            
            con = DriverManager.getConnection(cadena);
            System.out.println("Base de datos conectada!!");
            
        } catch (SQLException e) {
            
            System.out.println("Error de conexión: "+e.toString());
            
        }
        
        return con;
    }
    
    public void cerrarConexion(){
        try {
            if(con != null){
                con.close();
                System.out.println("Se cerró la conexión correctamente");
            }
        } catch (HeadlessException | SQLException e) {
            System.out.println("No se cerró la conexión correctamente "+e.toString());
        }
    }
    
}
