package org.example;

import com.github.javafaker.Faker;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    // JDBC URL, username, and password of PostgreSQL server
    private static final String URL = "jdbc:postgresql://localhost:5432/java_krot";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456";

    // JDBC variables for opening and managing connection
    private static Connection connection;

    public static void main(String[] args) {
        try {
// Open a connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Виберіть операцію:");
                System.out.println("1. Додати нову тварину");
                System.out.println("2. Показати всіх тварин");
                System.out.println("3. Видалити тварину");
                System.out.println("4. Змінити тварину");
                System.out.println("5. Вийти");
                int choice = scanner.nextInt();
                scanner.nextLine();  // consume newline

                switch (choice) {
                    case 1:
                        insertAnimal(connection);
                        break;
                    case 2:
                        showAllAnimals(connection);
                        break;
                    case 3:
                        deleteAnimal(connection);
                        break;
                    case 4:
                        updateAnimal(connection);
                        break;
                    case 5:
                        connection.close();
                        return;
                    default:
                        System.out.println("Невірний вибір. Спробуйте ще раз.");
                }
            }

            // Generate and insert 100,000 random animals
//            generateAndInsertAnimals(connection, 100000);
//            connection.close();
        } catch (SQLException e) {
            System.out.println("Begin working"+e.getMessage());
        }
    }

    private static void createTableAnimals(Statement command) throws SQLException {
        String sql = "CREATE TABLE animals (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(50) NOT NULL, " +
                "species VARCHAR(50) NOT NULL, " +
                "age INT NOT NULL, " +
                "weight DECIMAL(5, 2) NOT NULL" +
                ")";
        command.executeUpdate(sql);
    }

    private static void insertAnimal(Connection conn) throws SQLException {
        Animal animal = new Animal();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Вкажіть назву ->_");
        animal.setName(scanner.nextLine());

        System.out.print("Вкажіть вид тварини ->_");
        animal.setSpecies(scanner.nextLine());

        System.out.print("Вкажіть вік ->_");
        animal.setAge(scanner.nextInt());

        System.out.print("Вкажіть вагу ->_");
        animal.setWeight(scanner.nextDouble());

        String sql = "INSERT INTO animals (name, species, age, weight) VALUES (?, ?, ?, ?)";

        // Create a prepared statement object
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        // Set the values for the prepared statement
        preparedStatement.setString(1, animal.getName());
        preparedStatement.setString(2, animal.getSpecies());
        preparedStatement.setInt(3, animal.getAge());
        preparedStatement.setBigDecimal(4, java.math.BigDecimal.valueOf(animal.getWeight()));

        // Execute the query
        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Тваринку успішно додано!");
        }
        preparedStatement.close();
    }

    private static void showAllAnimals(Connection connection) throws SQLException {
        String sql = "SELECT * FROM animals";
        Statement command = connection.createStatement();
        ResultSet resultSet = command.executeQuery(sql);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String species = resultSet.getString("species");
            int age = resultSet.getInt("age");
            double weight = resultSet.getDouble("weight");

            System.out.println("ID: " + id + ", Name: " + name + ", Species: " + species + ", Age: " + age + ", Weight: " + weight);
        }

        resultSet.close();
        command.close();
    }

    private static void deleteAnimal(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Вкажіть ID тварини для видалення ->_");
        int id = scanner.nextInt();

        String sql = "DELETE FROM animals WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        int rowsDeleted = preparedStatement.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("Тваринку успішно видалено!");
        } else {
            System.out.println("Тваринку з таким ID не знайдено.");
        }
        preparedStatement.close();
    }

    private static void updateAnimal(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Вкажіть ID тварини для зміни ->_");
        int id = scanner.nextInt();
        scanner.nextLine();  // consume newline

        System.out.print("Вкажіть нову назву ->_");
        String name = scanner.nextLine();

        System.out.print("Вкажіть новий вид тварини ->_");
        String species = scanner.nextLine();

        System.out.print("Вкажіть новий вік ->_");
        int age = scanner.nextInt();

        System.out.print("Вкажіть нову вагу ->_");
        double weight = scanner.nextDouble();

        String sql = "UPDATE animals SET name = ?, species = ?, age = ?, weight = ? WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, species);
        preparedStatement.setInt(3, age);
        preparedStatement.setBigDecimal(4, java.math.BigDecimal.valueOf(weight));
        preparedStatement.setInt(5, id);

        int rowsUpdated = preparedStatement.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Тваринку успішно змінено!");
        } else {
            System.out.println("Тваринку з таким ID не знайдено.");
        }
        preparedStatement.close();
    }

    private static void generateAndInsertAnimals(Connection conn, int count) throws SQLException {
        Faker faker = new Faker();
        Random random = new Random();

        String sql = "INSERT INTO animals (name, species, age, weight) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        for (int i = 0; i < count; i++) {
            String name = faker.animal().name();
            String species = faker.lorem().word();
            int age = random.nextInt(15) + 1; // Вік від 1 до 15 років
            double weight = 10 + (200 - 10) * random.nextDouble(); // Вага від 10 до 200 кг

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, species);
            preparedStatement.setInt(3, age);
            preparedStatement.setDouble(4, weight);
            preparedStatement.addBatch();

            // Виконувати батч кожні 1000 записів для оптимізації
            if (i % 1000 == 0) {
                preparedStatement.executeBatch();
            }
        }

        // Виконати залишок батчу
        preparedStatement.executeBatch();
        preparedStatement.close();

        System.out.println(count + " random animals have been inserted into the database.");
    }
}
