package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * Главный класс чат-сервера.
 */
public class Server {
    /**
     * Порт, по которому сервер ведет прослушку и ждет клиентов.
     */
    private static final int PORT = 777;

    /**
     * Множество всех клиентных потоков.
     */
    private HashSet<ClientThread> clientThreadSet;

    /**
     * Инициализация экземпляра класса.
     */
    private Server() {
        clientThreadSet = new HashSet<>();
    }

    /**
     * Запуск прослушки порта. Создание потока обработки сообщений от клиента.
     */
    private void run() {
        try {
            // Запускается прослушка заданного порта, ожидаются подключения со стороны клиентов.
            ServerSocket socketListener = new ServerSocket(PORT);

            // В бесконечном цикле получаем экземпляры объектов сокетов клиентов.
            while (true) {
                Socket client = null;
                // Пока получем непустой экземпляр, пытаемся его создать.
                while (client == null) {
                    // Попытка получения экземпляра сокета.
                    // Не расходует процессорное время, т.к. блокируется в ожидании входящего подключения.
                    client = socketListener.accept();
                }
                // Создаем экземпляр потока обработки входящих сообщений от клиентов, записываем в коллекцию.
                clientThreadSet.add(new ClientThread(this, client));
            }
        } catch (Exception e) {
            // Выводим лог, падение все равно неизбежно.
            e.printStackTrace();
        }
    }

    /**
     * Отправка сообщения всем клиентам, подключенным к серверу.
     * Отправка не производится на самого отправителя.
     * @param sender - отправитель сообщения.
     * @param message - сообщение.
     */
    public void broadcastMessage(ClientThread sender, String message) {
        // Перебираем всех клиентов.
        for (ClientThread client : clientThreadSet) {
            try {
                // Отправляем, если не является источником сообщения.
                if (client != sender)
                    client.sendMessage(message);
            } catch (IOException e) {
                // Могут возникнуть временные проблемы с подключением, логируем данный факт.
                System.out.println("Не удалось отправить сообщение пользователю с логином " + client.login);
            }
        }
    }

    /**
     * Точка входа.
     * @param args - аргументы командной строки.
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
