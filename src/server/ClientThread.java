package server;

import java.io.*;
import java.net.Socket;

/**
 * Класс, реализующий обработку входящих сообщений от клиента в отдельном потоке.
 * Наследуется от предопределенного класса потока.
 */
public class ClientThread extends Thread {
    /**
     * Клиентский сокет, по нему поступают сообщения на сервер.
     */
    private Socket socket;

    /**
     * Ссылка на экземпляр класса сервера. Необходима для массовой рассылки на всех клиентов.
     */
    private Server server;

    /**
     * Входной поток данных от клиента.
     */
    private DataInputStream distr;

    /**
     * Выходной поток данных клиенту.
     */
    private DataOutputStream dostr;

    /**
     * Логин клиента, задается в первом сообщении после подключения.
     */
    public String login;

    /**
     * Инициализация экземпляра класса. Запуск потока.
     * @param socket - клиентский сокет.
     */
    public ClientThread(Server server, Socket socket) throws IOException {
        super();
        try {
            this.socket = socket;
            this.server = server;
            // Подготавливаем потоки ввода и вывода.
            // Вводов и выводов будет много, поэтому создавать потоки каждый раз негуманно.
            this.distr = new DataInputStream(socket.getInputStream());
            this.dostr = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            // Если не удается получить потоки ввода и вывода, получаем ошибку, логируем.
            System.out.println("Не удалось создать потоки для взаимодействия с клиентом.");
            // Пробрасываем ошибку вверх по стеку вызова.
            throw e;
        }
        // Стартуем поток после инициализации.
        this.start();
    }

    /**
     * Метод, выполняемый в потоке.
     */
    @Override
    public void run(){
        try {
            // Читаем логин из потока.
            login = distr.readUTF();
            // Логируем факт подключения.
            System.out.println(login + " connected.");
            // Делаем рассылку на всех клиентов о том, что в их полку прибыло.
            server.broadcastMessage(this, login + " присоединяется к беседе.");

            // В бесконечном цикле получаем сообщения от клиента.
            String message;
            while (true) {
                    // Читаем сообщение из потока ввода.
                    String msg = distr.readUTF();
                    // Если сообщение указывает на то, что пользователь вышел, делаем рассылку, выходим из цикла.
                    if (msg.equals("exit")){
                        server.broadcastMessage(this, login + " покидает беседу.");
                        break;
                    }
                    // Если сообщение должно попасть в чат, помечаем его именем отправителя.
                    message = login + ": \t";
                    message += msg;
                    // И делаем его рассылку на всех клиентов.
                    server.broadcastMessage(this, message);
            }
        } catch (IOException e) {
            // Логируем ошубку, падение все равно неизбежно.
            e.printStackTrace();
        }
    }

    /**
     * Отправить сообщение данному клиенту.
     * @param message - отправляемое сообщение.
     * @throws IOException в случае провала.
     */
    public void sendMessage(String message) throws IOException {
        // Если пустое сообщение, то не отправляем.
        if (message == null || message.equals("")) { return; }
        // Записываем в поток вывода.
        dostr.writeUTF(message);
    }
}
