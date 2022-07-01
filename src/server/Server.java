package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class Server {
    static ArrayList<User> users = new ArrayList<>();
    static String db_url = "jdbc:mysql://localhost/android28";
    static String db_login = "root";
    static String db_pass = "";
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9178);
            System.out.println("Сервер запущен!");
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            while (true){
                // Создаём сокет для подключившегося клиента
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                User user = new User(socket);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            boolean isAuth = false;
                            while (!isAuth){
                                user.getOut().writeUTF("Телефон: ");
                                String phone = user.getIs().readUTF();
                                user.getOut().writeUTF("Пароль: ");
                                String pass = user.getIs().readUTF();
                                Connection connection = DriverManager.getConnection(db_url, db_login, db_pass);
                                Statement statement = connection.createStatement();
                                ResultSet resultSet = statement.executeQuery("SELECT * FROM `users` WHERE `phone`='"+phone+"' AND `pass`='"+pass+"'");
                                if(resultSet.next()) isAuth = true;
                                else user.getOut().writeUTF("Неправильный логин или пароль, попробуйте ещё раз");
                            }
                            users.add(user);
                            //System.out.println(resultSet.getString("name"));
                            user.getOut().writeUTF("Введите имя: ");
                            user.setName(user.getIs().readUTF());
                            while (true){
                                String request = user.getIs().readUTF();
                                System.out.println(request);
                                broadCastMessage(request, user.getUuid());
                            }
                        } catch (Exception e) {
                            users.remove(user);
                            broadCastMessage(user.getName()+" отключился");
                        }

                    }
                });
                thread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void broadCastMessage(String message, UUID uuid){

        for (User user:users){
            try {
                if(!user.getUuid().toString().equals(uuid.toString())) {
                    user.getOut().writeUTF(user.getName() + ": " + message);
                }else{
                    System.out.println(user.getName() + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void broadCastMessage(String message){
        System.out.println(message);
        for (User user:users){
            try {
                user.getOut().writeUTF("Server: " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
