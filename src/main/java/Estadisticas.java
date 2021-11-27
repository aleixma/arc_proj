
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nengo
 */
public class Estadisticas {
    
    private ArrayList<Long> init_clientes;
    private ArrayList<Long> init_taxis;
    
    private ArrayList<Long> full_clientes;
    private ArrayList<Long> full_taxis;
    
    Estadisticas() {    //inicializamos los arrays de datos
        this.init_clientes=new ArrayList<>();
        this.init_taxis=new ArrayList<>();
        this.full_clientes=new ArrayList<>();
        this.full_taxis=new ArrayList<>();
        
    }
    
    synchronized public void addClienteInit(Long l){
        init_clientes.add(l);
    }
    
    synchronized public void addTaxiInit(Long l){
            init_taxis.add(l);
    }
        
    synchronized public void addClienteFull(Long l){
            full_clientes.add(l);
    }
        
    synchronized public void addTaxiFull(Long l){
            full_taxis.add(l);
    }
        
    synchronized public String getMediaInitClientes(){
            long aux =0;
            for (Long l : init_clientes) {
                aux =+ l;
            }
            aux = aux/(long)init_clientes.size();
            return Long.toString(aux);
    }
        
    synchronized public String getMediaInitTaxis(){
            long aux =0;
            for (Long l : init_taxis) {
                aux =+ l;
            }
            aux = aux/(long)init_taxis.size();
            return Long.toString(aux);
    }
        
    synchronized public String getMediaFullClientes(){
            long aux =0;
            for (Long l : full_clientes) {
                aux =+ l;
            }
            aux = aux/(long)full_clientes.size();
            return Long.toString(aux);
    }
        
    synchronized public String getMediaFullTaxis(){
            long aux =0;
            for (Long l : full_taxis) {
                aux =+ l;
            }
            aux = aux/(long)full_taxis.size();
            return Long.toString(aux);
    }
    
    synchronized public void printEj(){
        int randomNumc = ThreadLocalRandom.current().nextInt(0, init_clientes.size() + 1);
        int randomNumc2 = ThreadLocalRandom.current().nextInt(0, init_clientes.size() + 1);
        int randomNumc3 = ThreadLocalRandom.current().nextInt(0, init_clientes.size() + 1);
        int randomNum = ThreadLocalRandom.current().nextInt(0, init_taxis.size() + 1);
        int randomNum2 = ThreadLocalRandom.current().nextInt(0, init_taxis.size() + 1);
        int randomNum3 = ThreadLocalRandom.current().nextInt(0, init_taxis.size() + 1);
        System.out.println("Tiempos: "+init_clientes.get(randomNumc)+" "+init_clientes.get(randomNumc2)+" "+init_clientes.get(randomNumc3));
        System.out.println("Tiempos: "+init_taxis.get(randomNum)+" "+init_taxis.get(randomNum2)+" "+init_taxis.get(randomNum3));
        System.out.println("Tiempos: "+full_clientes.get(randomNumc)+" "+full_clientes.get(randomNumc2)+" "+full_clientes.get(randomNumc3));
        System.out.println("Tiempos: "+full_taxis.get(randomNum)+" "+full_taxis.get(randomNum2)+" "+full_taxis.get(randomNum3));
    }
    
}
