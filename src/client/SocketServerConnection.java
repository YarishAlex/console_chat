package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Реализация подключения к чат-серверу, работающему на сокетах.
 * Подробное описание каждого из методов контракта можно найти в интерфейса.
 */
public class SocketServerConnection implements IServerConnection {
    private Socket socket;

    private DataInputStream distr;
    private DataOutputStream dostr;

    @Override
    public void connect(String address, int port, String login) throws IOException {
        InetAddress ipAddress = InetAddress.getByName(address);
        this.socket = new Socket(ipAddress, port);
        // Создаем потоки ввода и вывода.
        this.distr = new DataInputStream(socket.getInputStream());
        this.dostr = new DataOutputStream(socket.getOutputStream());
        // Отправляем на сервер логин.
        dostr.writeUTF(login);
    }

    @Override
    public void send(String message) {
        try {
            // Пишем в поток вывода.
            dostr.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Не удалось отправить сообщение");
        }
    }

    @Override
    public String recieve() {
        try {
            // Читаем из потока ввода.
            return distr.readUTF();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        // Отправляем на сервер сообщение о завершении сеанса.
        String message = "exit";
        dostr.writeUTF(message);
        // Закрываем сокет.
        socket.close();
    }
}
