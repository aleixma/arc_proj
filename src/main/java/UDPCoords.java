
import java.util.concurrent.TimeUnit;


public class UDPCoords extends Thread {
    Cliente c;
    
    UDPCoords(Cliente c){
        this.c = c;
    }
    
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(5000);
                c.sendCoords();
                // Esperar entre 60-70 segundos para volver a enviar las coordenadas
                Thread.sleep(60+(c.Rand(10)*1000));
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
