-- Eliminar y crear la base de datos
DROP DATABASE IF EXISTS empresa;
CREATE DATABASE empresa;
USE empresa;

-- Crear tabla empleados
CREATE TABLE IF NOT EXISTS empleados (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         nombre VARCHAR(100) NOT NULL,
    salario DECIMAL(10,2)
    );

-- Insertar registros
INSERT INTO empleados (nombre, salario) VALUES
                                            ('Juan Pérez', 2500.00),
                                            ('María Gómez', 3000.50),
                                            ('Luis Rodríguez', 2800.75);

-- Eliminar procedimientos
DROP PROCEDURE IF EXISTS obtener_empleado;
DROP PROCEDURE IF EXISTS aumentar_salario;

-- Crear procedimiento para obtener empleado por ID
DELIMITER //
CREATE PROCEDURE obtener_empleado(IN empleado_id INT)
BEGIN
SELECT * FROM empleados WHERE empleados.id = empleado_id;
END
//
DELIMITER ;

-- Crear procedimiento para aumentar salario
DELIMITER //
CREATE PROCEDURE aumentar_salario(IN empleado_id INT, IN incremento DECIMAL(10,2))
BEGIN
UPDATE empleados
SET salario = salario + incremento
WHERE empleados.id = empleado_id;
END
//
DELIMITER ;
