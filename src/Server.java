import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    // variables
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    // makes empty arraylist
    // sets done to false
    public Server() {
        connections = new ArrayList<>();
        done = false;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(25565);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }

        } catch (Exception e) {
            shutdown();
        }
    }

    // sends message to all connections to the server by a for loop to every
    // connection
    public void broadcast(String message) {
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    // shut down server
    public void shutdown() {

        try {
            done = true;
            pool.shutdown();
            if (!server.isClosed()) {
                server.close();
            }

            // end the connection for all connected clients
            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (Exception e) {
            // ignore
        }

    }

    class ConnectionHandler implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {

            try {
                // makes input output handler
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                // Get nickname from the user
                out.println("Please enter a nickname");
                nickname = in.readLine(); // gets the nickname from clients input
                // informs the server that X person has joined and also the rest of the client
                // that X has joined
                System.out.println(nickname + " Connected");
                broadcast(nickname + " joined the chat!");

                // Makes a string variable for messages from the clients
                String message;

                // Check the message sent by the client
                while ((message = in.readLine()) != null) {

                    // change nickname
                    if (message.startsWith("/nick ")) {

                        // Checks if users has input a nickname after /nick
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            // tells the server, user and all the rest of the clients that the username has
                            // been changed
                            broadcast(nickname + " renamed themselves to " + messageSplit[1]);
                            System.out.println(nickname + " renamed themselves to " + messageSplit[1]);
                            out.print("Successfully changed nickname to " + nickname);

                        }
                        // if the user didn't give a nickname then informs the user of not providing a
                        // nickname
                        else {

                            out.println("no nickname provided");
                        }

                        // Client quits the chat
                    } else if (message.startsWith("/quit")) {
                        broadcast(nickname + " has left the chat");
                        shutdown();
                    }
                    // Client sends a message to all connections
                    else {
                        broadcast(nickname + ": " + message);
                    }

                }

                // cloeses the client if an error happens
            } catch (IOException e) {
                shutdown();
            }

        }

        // send message, being used my the broadcast method
        public void sendMessage(String message) {

            out.println(message);
        }

        // shut down client
        public void shutdown() {
            try {
                // Closes input output reader to avoid future error
                in.close();
                out.close();
                // checks if the clients is closed, if not then it just closes all the clients
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }

    }

    public static void main(String[] args) {
        // runs the servers run method
        Server server = new Server();
        server.run();
    }

}