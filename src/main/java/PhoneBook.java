import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PhoneBook {

    private Connection connection;

    public PhoneBook() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:contacts.db");
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE contacts");
            statement.executeUpdate("CREATE TABLE contacts (id INTEGER PRIMARY KEY, " +
                    "first_name TEXT, last_name TEXT, phone1 TEXT, phone2 TEXT, phone3 TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Добавление контакта
    public void addContact(String firstName, String lastName, String phone1, String phone2, String phone3) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO contacts (first_name, last_name, phone1, phone2, phone3) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, phone1);
            statement.setString(4, phone2);
            statement.setString(5, phone3);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Удаление контакта по ID
    public void deleteContact(int contactId) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM contacts WHERE id = ?")) {
            statement.setInt(1, contactId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Редактирование контакта по ID
    public void editContact(int contactId, String firstName, String lastName, String phone1, String phone2, String phone3) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE contacts SET first_name = ?, last_name = ?, phone1 = ?, phone2 = ?, phone3 = ? WHERE id = ?")) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, phone1);
            statement.setString(4, phone2);
            statement.setString(5, phone3);
            statement.setInt(6, contactId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Поиск контакта по фамилии
    public List<Contact> searchByLastName(String lastName) {
        List<Contact> contacts = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM contacts WHERE last_name = ?")) {
            statement.setString(1, lastName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                contacts.add(new Contact(
                        resultSet.getString("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("phone1"),
                        resultSet.getString("phone2"),
                        resultSet.getString("phone3")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public List<Contact> searchById(Integer id) {
        List<Contact> contacts = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM contacts WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                contacts.add(new Contact(
                        resultSet.getString("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("phone1"),
                        resultSet.getString("phone2"),
                        resultSet.getString("phone3")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    // Получение всех контактов
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM contacts");
            while (resultSet.next()) {
                contacts.add(new Contact(
                        resultSet.getString("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("phone1"),
                        resultSet.getString("phone2"),
                        resultSet.getString("phone3")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public List<Contact> SortContacts(List<Contact> contacts) {
        Collections.sort(contacts, Comparator.comparing(Contact::getLastName)
                .thenComparing(Contact::getFirstName)
                .thenComparing(Contact::getPhone1)
                .thenComparing(Contact::getPhone2)
                .thenComparing(Contact::getPhone3));
        return contacts;
    }

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        // Пример использования: добавление контакта
        phoneBook.addContact("Иван", "Иванов", "123-456-789", "456-789-012", "789-012-345");
        phoneBook.addContact("Петр", "Петров", "123-456-789", "456-789-012", "789-012-345");
        phoneBook.addContact("Александр", "Александров", "987-654-321", "999-888-777", "789-012-345");
        phoneBook.addContact("Кирилл", "Дарьевич", "555-555-555", "456-789-012", "789-012-345");
        phoneBook.addContact("Дмитрий", "Евгеньевич", "248-585-900", "456-789-012", "789-012-345");

        // Пример использования: получение и вывод всех контактов
        System.out.println("Вывод всех контактов: ");
        List<Contact> allContacts_old = phoneBook.getAllContacts();
        for (Contact contact : allContacts_old) {
            System.out.println(contact);
        }

        // Пример использования: удаление контакта
        phoneBook.deleteContact(1);

        // Пример использования: редактирование контакта
        phoneBook.editContact(2, "Новое имя", "Новая фамилия", "111-222-333", "222-333-444", "333-444-555");

        System.out.println("Поиск по фамилии: ");
        // Пример использования: поиск по фамилии
        List<Contact> foundContacts_by_lastname = phoneBook.searchByLastName("Евгеньевич");
        for (Contact contact : foundContacts_by_lastname) {
            System.out.println(contact);
        }

        System.out.println("Поиск по id: ");
        List<Contact> foundContacts_by_id = phoneBook.searchById(3);
        for (Contact contact : foundContacts_by_id) {
            System.out.println(contact);
        }

        System.out.println("Вывод всех контактов после удаления: ");
        List<Contact> allContacts = phoneBook.getAllContacts();
        for (Contact contact : allContacts) {
            System.out.println(contact);
        }

        System.out.println("Вывод сортированных контактов после удаления: ");
        List<Contact> allContacts_sorted = phoneBook.SortContacts(phoneBook.getAllContacts());
        for (Contact contact : allContacts_sorted) {
            System.out.println(contact);
        }
    }
}