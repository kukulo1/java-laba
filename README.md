Расширенная шпаргалка по Java + MySQL + ООП (на основе задач 1–8)

---

## 0. Основы MySQL, необходимые для понимания

### Теория:

MySQL — это система управления базами данных (СУБД), основанная на языке SQL. Она используется для хранения информации в виде таблиц. Таблицы состоят из строк и столбцов.

Пояснения к базовым терминам:

- **База данных (database)** — это контейнер, содержащий связанные таблицы.
- **Таблица (table)** — структура для хранения строк, каждая строка — это запись.
- **Столбец (column)** — атрибут записи (например: имя, возраст).
- **Тип данных** — определяет, что можно хранить в ячейке (целое число, текст, дата и т.д.).
- **PRIMARY KEY** — уникальный идентификатор строки в таблице.
- **AUTO\_INCREMENT** — автоматическое увеличение значения при добавлении новых строк.

### Базовый пример:

```sql
CREATE TABLE students (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  age INT,
  salary DOUBLE
);
```

### Объяснение:

Этот запрос создаёт таблицу с четырьмя столбцами. Столбец `id` будет уникален для каждой строки, и его значение будет автоматически увеличиваться при вставке новых записей.

---

## 1. Подключение к базе данных

### Теория:

Чтобы Java-программа могла работать с базой данных, используется интерфейс JDBC (Java Database Connectivity). Для подключения нужен URL, логин и пароль. Все SQL-операции требуют подключения через `Connection`.

### Базовый пример:

```java
Connection conn = DriverManager.getConnection(url, username, password);
```

### Реальный пример из кода:

```java
private static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, username, password);
}
```

### Дополнение:

Метод `getConnection()` — это обёртка, позволяющая переиспользовать соединение в любом месте программы. Все SQL-операции (чтение, запись, обновление) требуют объекта `Connection`.

---

## 2. Создание таблицы из Java

### Теория:

SQL-запросы можно отправлять из Java, используя объект `Statement` или `PreparedStatement`. Для создания таблиц используется команда `CREATE TABLE`.

### Базовый пример:

```java
String query = "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100))";
Statement statement = conn.createStatement();
statement.executeUpdate(query);
```

### Реальный пример из кода:

```java
executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " ("
        + "id INT AUTO_INCREMENT PRIMARY KEY, "
        + "name VARCHAR(255), "
        + "age INT, "
        + "salary DOUBLE)");
```

### Объяснение:

Метод `executeStatement()` позволяет выполнять любые SQL-запросы с минимальным повторением кода. Строка запроса собирается динамически с помощью конкатенации.

---

## 3. Подготовленные SQL-запросы (PreparedStatement)

### Теория:

`PreparedStatement` — это безопасный и удобный способ отправки запросов с переменными. Вместо прямой подстановки значений используются «плейсхолдеры» — `?`, куда потом передаются конкретные значения.

### Базовый пример:

```java
String sql = "INSERT INTO students (name, age) VALUES (?, ?)";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, "Ivan");
stmt.setInt(2, 20);
stmt.executeUpdate();
```

### Реальный пример из кода:

```java
try (PreparedStatement statement = getConnection().prepareStatement(query)) {
    for (int i = 0; i < params.length; i++) {
        statement.setObject(i + 1, params[i]);
    }
    statement.executeUpdate();
} catch (SQLException e) {
    e.printStackTrace();
}
```

### Дополнение:

`Object... params` — это varargs (переменное количество аргументов), позволяющее писать универсальный код.

---

## 4. Чтение данных (`ResultSet`)

### Теория:

Запросы `SELECT` возвращают результат в виде объекта `ResultSet`. Для чтения данных построчно используется метод `next()`.

### Базовый пример:

```java
ResultSet rs = stmt.executeQuery("SELECT name, age FROM students");
while (rs.next()) {
    System.out.println(rs.getString("name") + " - " + rs.getInt("age"));
}
```

### Реальный пример из кода:

```java
try (Statement statement = getConnection().createStatement();
     ResultSet resultSet = statement.executeQuery(query)) {

    ResultSetMetaData metaData = resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();

    while (resultSet.next()) {
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(resultSet.getString(i) + "\t");
        }
        System.out.println();
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

### Дополнение:

`ResultSetMetaData` используется для получения информации о столбцах таблицы. Это особенно полезно, если структура таблицы заранее неизвестна.

---

## 5. Проверка пользовательского ввода

### Теория:

Проверка ввода необходима, чтобы избежать сбоев при работе с консолью. Особенно важно валидировать числа.

### Базовый пример:

```java
Scanner scanner = new Scanner(System.in);
while (!scanner.hasNextInt()) {
    System.out.println("Введите целое число:");
    scanner.next();
}
int age = scanner.nextInt();
```

### Реальный пример из кода:

```java
int age = -1;
while (!scanner.hasNextInt()) {
    System.out.print("Введите возраст (целое число): ");
    scanner.next();
}
age = scanner.nextInt();
if (age < 0 || age > 100) {
    System.out.println("Возраст должен быть в пределах от 0 до 100.");
    return;
}
```

### Дополнение:

Помимо проверки на тип, добавлены логические ограничения на значение, что улучшает стабильность программы.

---

## 6. Экспорт данных в CSV

### Теория:

CSV (Comma-Separated Values) — простой текстовый формат, в котором данные разделены точкой с запятой или запятой. Подходит для импорта/экспорта в Excel.

### Базовый пример:

```java
FileWriter fileWriter = new FileWriter("students.csv");
fileWriter.write("id;name;age\n");
fileWriter.write("1;Ivan;20\n");
fileWriter.close();
```

### Реальный пример из кода:

```java
ResultSetMetaData metaData = resultSet.getMetaData();
int columnCount = metaData.getColumnCount();

for (int i = 1; i <= columnCount; i++) {
    fileWriter.append('"').append(metaData.getColumnName(i)).append('"');
    if (i < columnCount) fileWriter.append(";");
}
fileWriter.append("\n");
```

### Дополнение:

Символ `"` экранирует значения, чтобы они корректно открывались в Excel. `ResultSetMetaData` позволяет автоматизировать вывод заголовков.

---

## 7. Основы ООП в задачах

### Теория:

Объектно-ориентированное программирование (ООП) — это подход, при котором данные объединяются с операциями, которые над ними выполняются, в классах и объектах.

- **Класс** — шаблон для объектов.
- **Объект** — экземпляр класса.
- **Наследование** — один класс может расширять другой.
- **Инкапсуляция** — скрытие деталей реализации (например, через `private` и `public`).

### Базовый пример:

```java
class Student {
    String name;
    int age;

    public void printInfo() {
        System.out.println(name + ", " + age);
    }
}
```

### Реальный пример:

```java
class Worker extends Student {
    double salary;

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}
```

### Дополнение:

Геттеры и сеттеры позволяют получить или изменить значение поля, сохраняя контроль над доступом к нему.

---

## 8. Обработка ошибок: try-catch и try-with-resources

### Теория:

В Java ошибки (исключения) можно перехватывать с помощью конструкции `try-catch`. Это защищает программу от аварийного завершения.

- `try` — код, где может возникнуть ошибка.
- `catch` — обработка ошибки.
- `finally` — выполняется всегда (необязательно).

`try-with-resources` — расширение конструкции `try`, которое автоматически закрывает ресурсы (например, `Connection`, `Scanner`, `FileWriter`).

### Пример с try-catch:

```java
try {
    Connection conn = DriverManager.getConnection(...);
} catch (SQLException e) {
    e.printStackTrace();
}
```

### Пример с try-with-resources:

```java
try (PreparedStatement stmt = conn.prepareStatement(query)) {
    stmt.setString(1, "Ivan");
    stmt.executeUpdate();
}
```

### Объяснение:

`try-with-resources` гарантирует, что ресурсы будут закрыты автоматически. Это избавляет от необходимости вручную вызывать `close()`.

---

## Итоговая таблица

| Раздел           | Что важно                            |
| ---------------- | ------------------------------------ |
| MySQL            | Таблицы, типы, SQL-запросы           |
| Подключение      | `DriverManager`, URL, try/catch      |
| SQL-запросы      | `PreparedStatement`, `setObject`     |
| Чтение           | `ResultSet`, `ResultSetMetaData`     |
| Ввод             | `Scanner`, `hasNextInt`, проверки    |
| CSV              | `FileWriter`, экспорт, экранирование |
| ООП              | Классы, наследование, геттеры        |
| Обработка ошибок | try-catch, try-with-resources        |

Эти конструкции охватывают все 8 задач и подходят как для студенческих проектов, так и для реальных приложений на Java с базой данных.

Если необходимо, можно дополнить шпаргалку примерами из конкретных заданий.

