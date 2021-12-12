import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class Servidor extends Thread {
    private final String notificacionCliente = "solicitud";
    
    private ServerSocket ss;
    private Socket cs;
    private PrintWriter out;
    private BufferedReader in;
    
    private DatagramSocket ds;
    private byte[] buf = new byte[256];
    
    private int id = 0;
    List<HiloCliente> taxiList = new ArrayList<>();
    List<HiloCliente> clientList = new ArrayList<>();
    List<HiloCliente> closedTaxiList = new ArrayList<>();
    List<HiloCliente> closedClientList = new ArrayList<>();
    
    boolean notifyClient = false;
    ArrayList<Integer> clientsNotified = new ArrayList<Integer>();
    
    Estadisticas estadisticas;
    
    
    Servidor(int port) {
        
        try {
            ds = new DatagramSocket(port);
            ss = new ServerSocket(port);
            
            System.out.println("Servidor escuchando en puerto " + port);
            
            estadisticas = new Estadisticas();
            
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void run() {
        try {
            // Hilo para recibir coordenadas por UDP
            Thread coordT = new Thread(){ public void run(){
                    while (true) {
                        try {
                            getCoords();
                        } catch (IOException ex) {
                            System.out.println("Server exception: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }
            };
            coordT.start();
            
            // Bucle principal para aceptar sockets y crear nuevos hilos con ellos
            while (true) {
                cs = ss.accept();
                in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                out = new PrintWriter(cs.getOutputStream(), true);
                this.id++;
                HiloCliente ct = new HiloCliente(cs,in,out,id,this);
                ct.start();
                
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getCoords() throws IOException{
        DatagramPacket packet 
              = new DatagramPacket(buf, buf.length);
        ds.receive(packet);
            
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        String received 
          = new String(packet.getData(), 0, packet.getLength());
        // Guarda la id y las coordenadas de la string que se recibe
        String[] r = received.split(" ");
        int recvID = Integer.parseInt(r[0]);
        String recvCoords= r[1];
        for (int i = 0; i < taxiList.size(); i++) {
            HiloCliente ctr =taxiList.get(i);
            int a = ctr.getClientID();
            if (a == recvID) {
                ctr.setCoords(recvCoords);
                break;
            }
        }
    }
    
    
    public void notifyTaxi(int cid,HiloCliente client){
        for (int i = 0; i < taxiList.size(); i++) {
            taxiList.get(i).out.println(notificacionCliente+" "+client.getClientID()+" "+client.getCoords());
        }
    }
    
    synchronized public int selectClient(HiloCliente taxi){
        if (notifyClient == true) {
            return 0;
        }
        notifyClient = true;
        for (int i = 0; i < clientList.size(); i++) {
            HiloCliente ctr = clientList.get(i);
            int a = ctr.getClientID();
            if (!clientsNotified.contains(a)) {
                System.out.println("El taxi "+taxi.getClientID()+" acepta la petición de "+a);
                ctr.out.println("El taxi "+taxi.getClientID()+" va de camino desde la posición: "+taxi.getCoords());
                clientsNotified.add(a);
                notifyClient = false;
                return 1; //success
            }
        }
        notifyClient = false;
        return 0;
    }
    
    synchronized public int notifyClient(int cid,HiloCliente taxi){
        if (notifyClient == true) {
            if (clientsNotified.contains(cid)) {
                return 0;
            }
        }
        notifyClient = true;
        for (int i = 0; i < clientList.size(); i++) {
            HiloCliente ctr =clientList.get(i);
            int a = ctr.getClientID();
            if (a == cid) {
                System.out.println("El taxi "+taxi.getClientID()+" acepta la petición de "+a);
                ctr.out.println("El taxi "+taxi.getClientID()+" va de camino desde la posición: "+taxi.getCoords());
                break;
            }
        }
        clientsNotified.add(cid);
        notifyClient = false;
        return 1; //success
    }
    
    /*
    public HiloCliente closestTaxi(ArrayList<HiloCliente> taxi, cliente){
        int i;//contador
        HiloCliente cercano = null;//variable para devolver el thread con el taxi mas cecano.
        double x=0.0,y=0.0,z=0.0;
        Coordinates cc = cliente.gerCoords();
        coordenadas ct;
        double min_actual=999999.99,actual=0.0;
   
        for(i=0;i<taxi.size();i++){
            ct = taxi.get(i).getCoords();
            x = cc.getx() - ct.getx;
            y = cc.gety() - ct.gety;
            z = cc.getz() - ct.getz;
            actual = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(x, 2));
            if(actual >= min_actual){
                cercano = taxi.get(i);
            }
        }
        return taxi.get(cercano);//se devuelve el de menor valor
    }
*/
    
    public void close() throws IOException {
        in.close();
        out.close();
        cs.close();
        ss.close();
    }
    
}