
import java.io.IOException;
import java.net.InetAddress;


public class Main {
    
    public static void main(String[] args) throws IOException {
        int port = 26000;
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        Servidor server;
        Cliente client;
        Cliente taxi;

        if (args.length >= 1) { 
            
            switch (args[0]) {
                  
                case "server" :
                    server = new Servidor(port);
                    server.start();
                    break;
                          
                case "client" :
                    if (args.length >= 2) {
                        for (int i=1;i<=Integer.parseInt(args[1]);i++){
                            client = new Cliente(port, ip, "client");
                            client.start();
                        }
                    } else {
                        client = new Cliente(port, ip, "client");
                        client.start();
                    }
                    break;
                        
                case "taxi" :
                    if (args.length >= 2) {
                        for (int i=1;i<=Integer.parseInt(args[1]);i++){
                            taxi = new Cliente(port, ip, "taxi");
                            taxi.start();
                        }
                    } else {
                        taxi = new Cliente(port, ip, "taxi");
                        taxi.start();
                    }
                    break;
                          
                default:
                    server = new Servidor(port);
                    client = new Cliente(port, ip, "client");
                    taxi = new Cliente(port, ip, "taxi");
                    break;
            }
        }
    }
}
