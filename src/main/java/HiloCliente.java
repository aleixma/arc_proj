
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class HiloCliente extends Thread{
    private String mode;
    final Socket cs;
    final PrintWriter out;
    final BufferedReader in;
    final Servidor server;
    private int id;
    private String coords;
    
    private long startTime;
    private long initTime;
    private long fullTime;

    HiloCliente(Socket cs, BufferedReader in, PrintWriter out,int id, Servidor server) {
        this.id=id;
        this.server=server;
        this.cs=cs;
        this.in=in;
        this.out=out;
        
        this.startTime = 0;
        this.initTime = 0;
        this.fullTime = 0;
    }
     
    @Override
    public void run() 
    {
        String received;
        while (true) {
            try {
                received = in.readLine();
                if (this.startTime==0){
                    this.startTime = System.currentTimeMillis();
                }
                  
                // Comandos sin más datos
                switch (received) {          
                    case "client" :
                        this.mode = "client";
                        server.clientList.add(this);
                        out.println(id);
                        System.out.println("Nuevo cliente conectado, id: "+this.id);
                        break;
                    case "taxi" :
                        this.mode = "taxi";
                        server.taxiList.add(this);
                        out.println(id);
                        System.out.println("Nuevo taxi conectado, id: "+this.id);
                        break;
                    case "initAck":
                        if (this.mode.equals("taxi")){
                            if (this.initTime==0){
                                this.initTime = System.currentTimeMillis()- this.startTime;
                                server.estadisticas.addTaxiInit(this.initTime);
                                System.out.println("Tiepo medio de inicialización del taxi: " +server.estadisticas.getMediaInitTaxis());
                            }
                        } if (this.mode.equals("client")){
                            if (this.initTime==0){
                                this.initTime = System.currentTimeMillis() - this.startTime;
                                server.estadisticas.addClienteInit(this.initTime);
                                System.out.println("Tiepo medio de inicialización del cliente: " + server.estadisticas.getMediaInitClientes());
                            }
                        }
                        break;
                    case "salir":
                        //server.estadisticas.printEj();
                        if (this.mode.equals("taxi")){
                            server.taxiList.remove(this);
                        } if (this.mode.equals("client")){
                            server.clientList.remove(this);
                        }
                        this.cs.close();
                        System.out.println("Conexión cerrada con "+this.id);
                        break;
                    default:
                        break;
                }
                
                // Comandos con más datos separados por espacios
                String[] r = received.split(" ");
                switch (r[0]) {
                    // El cliente solicita un taxi
                    case "solicita":
                        int recvID = Integer.parseInt(r[1]);
                        this.coords = r[2];
                        System.out.println("El cliente "+recvID+" pide un taxi desde la posición: "+this.coords);
                        server.notifyTaxi(recvID,this);
                        if (this.fullTime==0){
                            this.fullTime = System.currentTimeMillis()- this.startTime;
                            server.estadisticas.addClienteFull(this.fullTime);
                            System.out.println("Tiepo medio de solicitud del cliente: " +server.estadisticas.getMediaFullClientes());
                        }
                        break;
                    // El taxi acepta una petición
                    case "acepta":
                        int selected = 0;
                        do {
                            selected = server.selectClient(this);
                        } while (selected == 0);
                        if (this.fullTime==0){
                            this.fullTime = System.currentTimeMillis()- this.startTime;
                            server.estadisticas.addTaxiFull(this.fullTime);
                            System.out.println("Tiepo medio de solicitud del taxi: " +server.estadisticas.getMediaFullTaxis());
                        }
                        break;
                    // El cliente intenta reconectar
                    case "rtaxi":
                        int oldIDt = Integer.parseInt(r[1]);
                        this.id = oldIDt;
                        this.mode = "taxi";
                        server.taxiList.add(this);
                        //server.closedTaxiList.remove(this);
                        break;
                    case "rclient":
                        int oldIDc = Integer.parseInt(r[1]);
                        this.id = oldIDc;
                        this.mode = "client";
                        server.clientList.add(this);
                        //server.closedClientList.remove(this);
                        break;
                    default:
                        break;
                }
                
//                if (received.contains("solicita")) {
//                    String[] r = received.split(" "); // ["solicita","id","coords"]
//                    int recvID = Integer.parseInt(r[1]);
//                    this.coords = r[2];
//                    System.out.println("El cliente "+recvID+" pide un taxi desde la posición: "+this.coords);
//                    server.notifyTaxi(recvID,this);
//                    break;
//                } else if (received.contains("acepta")){
//                    int selected = 0;
//                    do {
//                        selected = server.selectClient(this);
//                    } while (selected == 0);
//                    break;
//                } 
                
                
            } catch (Exception e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
                // Si el servidor se desconecta, se añade el objeto a la lista de desconectados para luego buscarlo al reconectar
                if (this.mode.equals("taxi")){
                    server.closedTaxiList.add(this);
                    server.taxiList.remove(this);
                } else if (this.mode.equals("client")){
                    server.closedClientList.add(this);
                    server.clientList.remove(this);
                }
            }
        }
    }
    
    public void setMode(String s){
        this.mode=s;
    }
    
    public String getMode(){
        return this.mode;
    }
    
    public int getClientID(){
        return this.id;
    }
    
    public void setCoords(String c){
        this.coords=c;
    }
    
    public String getCoords(){
        return this.coords;
    }
    
}
    

