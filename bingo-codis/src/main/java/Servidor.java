import java.net.*;
import java.util.*;

public class Servidor extends Thread {

    // IP y puerto utilizados para la comunicacion multicast (constantes)
    private static final String IP = "225.0.0.100";
    private static final int PUERTO = 6789;

    // ArrayList que contiene los numeros del bombo
    private ArrayList<Integer> bombo;

    // Constructor de la clase Servidor
    public Servidor(String name) {
        super(name);
        this.bombo = new ArrayList<Integer>();
    }

    // Funcion que genera un bombo de bingo aleatorio de 90 numeros
    public void generarBombo() {
        for (int i = 1; i <= 90; i++) {
            bombo.add(i);
        }
        Collections.shuffle(bombo);
    }

    // Sobreescribimos el metodo run() de la clase Thread
    // Este metodo implementa el comportamiento del hilo que crearemos
    @Override
    public void run() {
        // Creamos el socket multicast
        MulticastSocket socket = null;

        // Debemos gestionar las expcepciones que puedan surgir
        try {
            // Nos unimos al grupo multicast
            socket = new MulticastSocket(PUERTO);
            InetSocketAddress group = new InetSocketAddress(InetAddress.getByName(IP), PUERTO);
            socket.joinGroup(group, NetworkInterface.getByName("wlan0"));

            while (true) {
                // Esperamos hasta recibir el mensaje "GG" del cliente ganador
                byte[] buffer = new byte[2];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                // Si recibimos el mensaje "GG", salimos del bucle y cerramos el socket
                if (new String(paquete.getData()).equals("GG")) {
                    System.out.println("¡FIN DEL JUEGO!\nLA PARTIDA HA TERMINADO\n");
                    socket.leaveGroup(group, NetworkInterface.getByName("wlan0"));
                    socket.close();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Funcion principal del programa, encargada de gestionar los distintos mensajes
    public static void main(String[] args) {
        // Creamos el socket multicast
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(PUERTO);

            // Creamos una instancia de la clase servidor e iniciamos el hilo de escucha
            Servidor servidor = new Servidor("Emisor");
            Thread hiloEscucha = new Servidor("Receptor");
            hiloEscucha.start();

            // Generamos el bombo y lo imprimimos
            servidor.generarBombo();
            System.out.println();

            // Enviamos los numeros del bombo mientras que el hilo de escucha este activo
            for (int i = 0; i < 90 && hiloEscucha.isAlive(); i++) {
                int bola = servidor.bombo.get(i);
                String mensaje = String.format("%02d", bola);
                byte[] buffer = mensaje.getBytes();
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), PUERTO);
                socket.send(paquete);

                // Mostramos la informacion por pantalla
                System.out.println("Ha salido la bola: " + bola);
                if (i == 88) {
                    System.out.println("Queda " + (90 - i - 1) + " bola\n");
                } else {
                    System.out.println("Quedan " + (90 - i - 1) + " bolas\n");
                }

                // Esperamos 1 segundo entre bolas
                Thread.sleep(1000);
            }

            // Esperamos a que el hilo de escucha termine su ejecucion y cerramos el socket
            hiloEscucha.join();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
