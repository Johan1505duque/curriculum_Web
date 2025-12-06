package com.curriculum;
import com.curriculum.database.connectionFactory;
import java.sql.Connection;

public class app {
    public static void main(String[] args){
        try(Connection conn=connectionFactory.getConnection()){
            System.out.println("🔥 Conexión exitosa a PostgreSQL!");
        }catch (Exception exception){
            System.out.println("❌ Error al conectar: " + exception.getMessage());
        }

    }
}
