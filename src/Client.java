<<<<<<< HEAD
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    // Variables for
    // the socket,
    // input output reader
    // boolean for checking if program is supposed to shutdown
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try {
            // Connects to the server IP and PORT
            // makes input ouput handler
            client = new Socket("188.150.82.78", 25565);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // runs the input handler in a thread
            // allowing the inhandler to run as the same time as the code below
            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            // runs at the same time as input handler
            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }
        } catch (IOException e) {
            // shut down the client if error is caught
            shutdown();
        }
    }

    // shutdown client
    public void shutdown() {
        done = true;
        try {
            // Closes input output reader
            // If not closed, will not be able to connect again due to an error
            in.close();
            out.close();
            // Closes client if it is not closed
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    // handels user client input
    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {
                // Makes a inout reader from the client
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    // makes a variable for the users inputs
                    String message = inReader.readLine();
                    // Checks the users input
                    // If the message is /quit, then the client will close
                    if (message.equals("/quit")) {
                        out.println(message);
                        inReader.close();
                        shutdown();
                        // If message does not match then send message to server
                    } else {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                // If an error is caught, then it will shutdown the client
                shutdown();
            }
        }

    }

    public static void main(String[] args) {
        // run the clients run method.
        Client client = new Client();
        client.run();
    }

}
=======
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable{


    //Variables 
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try {
            //Connects to the server IP and PORT
            // makes input ouput handler
            client = new Socket("188.150.82.78", 25565);
             out = new PrintWriter(client.getOutputStream(), true);
             in = new BufferedReader(new InputStreamReader(client.getInputStream()));

             //runs the input handler
             InputHandler inHandler = new InputHandler();
             Thread t = new Thread(inHandler);
             t.start();

             //runs at the same time as input handler
             String inMessage;
             while((inMessage = in.readLine()) != null){
                System.out.println(inMessage);
             }
        } catch (IOException e) {
            shutdown();
        }
    }

// shutdown client 
    public void shutdown(){
        done = true;
        try{
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close(); 
            }
        }catch(IOException e){
            // Ignore
        }
    }

    //handels user client input
    class InputHandler implements Runnable{

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while(!done){
                    String message = inReader.readLine();
                    if(message.equals("/quit")){
                        out.println(message);
                        inReader.close();
                        shutdown();
                    }else {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }


    }


    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
    
}
>>>>>>> ecebb097bd66a9ffecbfff83127cb485b17c42cb
