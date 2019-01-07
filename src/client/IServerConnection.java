package client;

import java.io.IOException;

/**
 * Интерфейс, задающий сценарий взаимодействия с сервером.
 *
 * В общем случае может быть несколько различных реализаций в зависимости от протокола взаимодействия.
 * Например, TCP/IP, HTTP, Telnet и т.д.
 *
 * Расширяется интерфейс AutoCloseable для того, чтобы реализация его метода close() могла
 * быть автоматически вызвана в конструкции try with resources.
 */
public interface IServerConnection extends AutoCloseable {
    /**
     * Подключиться к серверу под заданным логином.
     * @param address - web-адрес или ip сервера.
     * @param port - порт, на котором сервер ожидает клиентов.
     * @param login - логин, под которым пользователь хочет подключиться к чату.
     */
    void connect(String address, int port, String login) throws IOException;

    /**
     * Отправить сообщение на чат-сервер.
     * @param message - отправляемое сообщение.
     */
    void send(String message);

    /**
     * Получение сообщения с сервера.
     * @return входящее сообщение.
     */
    String recieve();
}
