
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class ReceptorCliente extends Thread {
    Cliente c;
    
    ReceptorCliente(Cliente c){
        this.c = c;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                c.receive();
            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
