import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Cliente extends Thread {
    String mode = "";
    int id = 0;
    String coords;
    
    private InetAddress ip = InetAddress.getByName("127.0.0.1");
    private int port = 0;
    
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    
    private byte[] buf;
    private DatagramSocket ds;
    private Scanner scanner = new Scanner(System.in);
    
    ArrayList<String> clientCoords = new ArrayList<String>();
    
    boolean cNotificado = false;
    
    
    Cliente(int port,InetAddress ip, String mode) throws IOException{
        this.coords = Rand(90)+"."+Rand(90)+","+Rand(90)+"."+Rand(90);
        this.mode = mode;
        this.ip = ip;
        this.port = port;
        startSockets(this.ip,this.port);
    }
    
    public void run() {
        try {
            if (mode.equals("client")) {
                startClient();
            } else if (mode.equals("taxi")) {
                startTaxi();
            } 
        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void startTaxi() throws Exception{
        // Inizializa el taxi
        if (this.id==0){
            sendv("taxi");
            receiveID();
            sendv("initAck");
        }
        // Hilos para el cliente de coordenadas UDP y el receptor de mensajes TCP
        UDPCoords coordT = new UDPCoords(this);
        coordT.start();
        ReceptorCliente tr = new ReceptorCliente(this);
        tr.start();
        
        // Espera un poco y comunica al servidor que esta listo para aceptar una solicitud de cliente
        Thread.sleep(10000+(Rand(20)*1000));
        sendv("acepta");
        Thread.sleep(30000);
        sendv("salir");
    }
    
    public void startClient() throws Exception{
        // Inizializa el cliente
        if (this.id==0){
            sendv("client");
            receiveID();
            sendv("initAck");
        }

        // Espera cierto tiempo y envía una solicitud al servidor, luego se queda esperando respuesta
        Thread.sleep((Rand(10)*1000));
        sendv("solicita "+ id + " " + coords);
        receive();
        Thread.sleep(30000);
        sendv("salir");
    }
    
    

    public void startSockets(InetAddress ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        ds = new DatagramSocket();
    }

    public String send(String msg) throws IOException {
        // Envia y recibe
        out.println(msg);
        String resp = in.readLine();
        System.out.println("Servidor: "+resp);
        return resp;
    }
    
    public void sendv(String msg) throws IOException {
        // Solo envia el mensaje
        out.println(msg);
    }
    
    public String receive() throws IOException {
        // Método para recibir y guardar los datos por TCP
        String received;
        do {
            received = in.readLine();
        } while (received == null);
        System.out.println("Servidor: "+received);
        try {
            String[] r = received.split(" ");
            String recvCommand = r[0];
            if (recvCommand.equals("solicitud")){ // Cliente solicitando un taxi [commandID] [clientID] [coords]
                if (!clientCoords.contains(r[1]+";"+r[2])){
                    clientCoords.add(r[1]+";"+r[2]); }
            }
        } catch (Exception e) {}
        return received;
    }
    
    public void receiveID() throws IOException{
        // Guarda la ID del cliente
        String received = in.readLine();
        System.out.println("Servidor: ID "+received);
        this.id=Integer.parseInt(received);
    }
    
    public void sendCoords() throws IOException{
        // Envia las coordenadas por UDP
        String msg = this.id+" "+this.coords; // [id] [coords]
        buf = msg.getBytes();
        DatagramPacket packet 
          = new DatagramPacket(buf, buf.length, ip, port);
        ds.send(packet);
    }
    
    public int Rand(int s) {
        // Devuelve entero entre 0 y s
        double doubleRand = Math.random() * s;
        int rand = (int)doubleRand;
        return rand;
    }

    public void close() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
