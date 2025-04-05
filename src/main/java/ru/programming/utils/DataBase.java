package ru.programming.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true"; //ссылка на подклюючение к локальной БД. my_db?createDatabaseIfNotExist=true здесь my_db - название БД, ?createDatabaseIfNotExist=true - флаг, для создания БД если она еще не существует, специфичный флаг для MySQL
    private static final String username = "root"; //здесь после равно просто вставь свой логин из MySQL (пользователь должен иметь рутправа для создания таблиц)
    private static final String password = "kukulo1"; //тоже самое что и с логином, только тут пароль

    public static Connection getConnection() throws SQLException {
        //DriverManager это класс, предоставляющий возможность соединения к БД
        //импортируется из библиотеки "com.mysql:mysql-connector-j:9.2.0"
        //посмотреть все библиотеки можете в файле build.gradle.kts в блоке dependencies{}
        return DriverManager.getConnection(url, username, password);
    }
}
