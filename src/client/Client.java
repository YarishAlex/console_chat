package client;

import java.util.Scanner;

/**
 * Основной класс клиента чата.
 * Содержит точку входа и каркас, который не зависит от конкретной реализации IServerConnection.
 */
public class Client {
    /**
     * Адрес чат-сервера.
     */
    private static final String ADDRESS = "localhost";

    /**
     * Порт, на котором чат-сервер ожидает клиентов.
     */
    private static final int PORT = 777;

    /**
     * Поток-обработчик входящих от сервера сообщений.
     */
    private Thread inputMessageHandler;

    /**
     * Запуск клиента. "Авторизация", создание обработка входящих сообщений, обработка пользовательского ввода.
     */
    private void run() {
        // Запрашиваем у пользователя логин.
        System.out.println("Введите логин: ");
        Scanner scan = new Scanner(System.in);
        String login = scan.nextLine();

        // Создаем подключение к серверу.
        // Используется конструкция try-with-resources, которая предоставляет наиболее безопасный способ работы с
        // так называемыми "закрываемыми" объектами, реализующими интерфейс AutoClothable.
        // Созданный экземпляр существует только внутри вложенного блока кода. При завершении выполения блока кода
        // вызывается метод clothe() данного экземпляра.
        try (IServerConnection connection = new SocketServerConnection()) {

            // Подключение и авторизация, если ее можно так назвать.
            connection.connect(ADDRESS, PORT, login);

            // Инициализируем обработчик входящих сообщений.
            inputMessageHandler = new Thread(() -> {
                // В бесконечном цикле получаем сообщения.
                while (true) {
                    // Не расходуется процессорное время, так как метод recieve() блокирует.
                    String message = connection.recieve();
                    // Если сообщение непустое, выводим его.
                    if (message != null && !message.equals(""))
                        System.out.println(message);
                }
            });
            // Запускаем поток обработки входящих сообщений.
            inputMessageHandler.start();

            System.out.println("Вы можете общаться. Для выхода введите \"exit\"");
            // В бесконечном цикле ожидаем пользовательский ввод и отправляем его на сервер.
            while (true) {
                // Процессорное время не расходуется, так как сканер блокирует выполнение до пользовательского ввода.
                String message = scan.nextLine();
                // Если сообщение сигнализирует о завершении сеанса, заканчиваем.
                if (message != null && message.equals("exit")) {
                    // Останавливаем поток обработки входящих сообщений.
                    inputMessageHandler.interrupt();
                    // Выходим из цикла.
                    break;
                }
                // Если сообщение непустое и не завершающее, отправляем его на сервер.
                if (message != null && !message.equals(""))
                    connection.send(message);
            }
        } catch (Exception e) {
            // Выводим лог, падение клиента неизбежно.
            e.printStackTrace();
        }

    }

    /**
     * Точка входа для приложения чат-клиента.
     * @param args - аргументы командной строки.
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
