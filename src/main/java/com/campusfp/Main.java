package com.campusfp;

import java.io.Console;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // 1. Cargar configuración desde db.properties
        Properties props = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("No se encontró el archivo db.properties");
                return;
            }
            props.load(input);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 2. Obtener datos de conexión
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        // 3. Probar conexión
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            System.out.println("Conexión establecida con éxito a la base de datos.");

            // Mostrar metadatos
            DatabaseMetaData meta = con.getMetaData();
            System.out.println("🔹 Driver: " + meta.getDriverName());
            System.out.println("🔹 Versión del driver: " + meta.getDriverVersion());
            System.out.println("🔹 Base de datos: " + meta.getDatabaseProductName());
            System.out.println("🔹 Versión BD: " + meta.getDatabaseProductVersion());
            System.out.println("🔹 Usuario conectado: " + meta.getUserName());
            System.out.println("🔹 URL de conexión: " + meta.getURL());


            // Scanner para entradas de usuario
            Scanner sc = new Scanner(System.in);

            Statement st = con.createStatement();
/*
        //3.1 Crear base de datos y tabla empleados si no existe
            st.executeUpdate("DROP DATABASE IF EXISTS empresa");
            st.executeUpdate("CREATE DATABASE empresa");
            st.executeUpdate("USE empresa");


            String dropTable= """
               DROP TABLE IF EXISTS empleados;
            """;

            st.executeUpdate(dropTable);
*/
        // Crear tabla empleados
        String createTable = """
                CREATE TABLE IF NOT EXISTS empleados (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    salario DECIMAL(10,2)
                )
            """;

        st.executeUpdate(createTable);
            String insertar = """
                            INSERT INTO empleados (nombre, salario) VALUES
                            ('Juan Pérez', 2500.00),
                            ('María Gómez', 3000.50),
                            ('Luis Rodríguez', 2800.75)
                            """;
            Statement stmt = con.createStatement();
            // Verificar si hay registros
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM empleados");
            rs.next();
            int total = rs.getInt(1);

            if (total == 0) {
                stmt.executeUpdate(insertar);
            }


            // Crear procedimiento almacenado (sin usar DELIMITER)
            String procedimiento_obtener = """
                CREATE PROCEDURE IF NOT EXISTS obtener_empleado(IN empleado_id INT)
                BEGIN
                    SELECT * FROM empleados WHERE empleados.id = empleado_id;
                END
                """;

        st.executeUpdate(procedimiento_obtener);

            String procedimiento_aumentar = """
            CREATE PROCEDURE IF NOT EXISTS aumentar_salario(IN empleado_id INT, IN incremento DECIMAL(10,2))
            BEGIN
                UPDATE empleados
                SET salario = salario + incremento
                WHERE empleados.id = empleado_id;
            END
            """;

        st.executeUpdate(procedimiento_aumentar);


        //SELECT para verificar los datos insertados
        String consulta_SELECT = "SELECT * FROM empleados";
        System.out.println("\n=== EMPLEADOS ===");
        try(ResultSet rs1 = st.executeQuery(consulta_SELECT)){
            while (rs1.next()) {
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs1.getInt("id"), rs1.getString("nombre"), rs1.getDouble("salario"));
            }
        }
        // Lamar al procedure aumentar_salario

            System.out.println("=== AUMENTAR SALARIO DE EMPLEADO ===");
            System.out.print("Ingrese el ID del empleado: ");
            int empleadoId = sc.nextInt();
            System.out.print("Ingrese el incremento salarial: ");
            double incremento = sc.nextDouble();
            String consulta_procedimiento = "{CALL aumentar_salario(?, ?)}";
            try (CallableStatement cst = con.prepareCall(consulta_procedimiento)){
                cst.setInt(1, empleadoId);
                cst.setDouble(2, incremento);
                cst.executeUpdate();
                System.out.println("Salario aumentado correctamente.");
                // Verificar el nuevo salario
                consulta_SELECT = "SELECT * FROM empleados WHERE id = ?";
                try(PreparedStatement pst = con.prepareStatement(consulta_SELECT)){
                    pst.setInt(1, empleadoId);
                    try(ResultSet rs2 = pst.executeQuery()){
                        if(rs2.next()){
                            System.out.printf("Nuevo salario del empleado ID %d: %.2f €%n",
                                    rs2.getInt("id"),
                                    rs2.getDouble("salario"));
                        }else{
                            System.out.println("Empleado no encontrado.");
                        }
                    }
                }

            }catch (SQLException e){
                System.err.println("Error en la consulta: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }
}