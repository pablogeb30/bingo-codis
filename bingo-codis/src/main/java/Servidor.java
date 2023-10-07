import java.net.*;
import java.util.*;

public class Servidor extends Thread {

    // Constantes
    private static final String IP = "225.0.0.100";
    private static final int PUERTO = 6789;

    private ArrayList<Integer> bombo;

    public Servidor(String name) {
        super(name);
        this.bombo = new ArrayList<Integer>();
    }

    public void generarBombo() {
        for (int i = 1; i <= 90; i++) {
            bombo.add(i);
        }
        Collections.shuffle(bombo);
    }

    @Override
    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(PUERTO);
            InetSocketAddress group = new InetSocketAddress(InetAddress.getByName(IP), PUERTO);
            socket.joinGroup(group, NetworkInterface.getByName("wlan0"));
            byte[] buffer = new byte[2];
            while (true) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
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

    public static void main(String[] args) {
        MulticastSocket socket = null;
        try {
            Servidor servidor = new Servidor("Emisor");
            socket = new MulticastSocket(PUERTO);
            Thread hiloEscucha = new Servidor("Receptor");
            hiloEscucha.start();
            servidor.generarBombo();
            System.out.println();
            for (int i = 0; i < 90 && hiloEscucha.isAlive(); i++) {
                int bola = servidor.bombo.get(i);
                String mensaje = String.format("%02d", bola);
                byte[] buffer = mensaje.getBytes();
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), PUERTO);
                socket.send(paquete);
                System.out.println("Ha salido la bola: " + bola);
                if (i == 88) {
                    System.out.println("Queda " + (90 - i - 1) + " bola\n");
                } else {
                    System.out.println("Quedan " + (90 - i - 1) + " bolas\n");
                }
                Thread.sleep(50);
            }
            hiloEscucha.join();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
