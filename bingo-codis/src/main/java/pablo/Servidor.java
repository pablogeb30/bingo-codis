package pablo;

import java.net.*;
import java.util.*;

public class Servidor {

    // Constantes
    private static final String IP = "225.0.0.100";
    private static final int PUERTO = 6789;

    private ArrayList<Integer> bombo;

    public Servidor() {
        this.bombo = new ArrayList<Integer>();
    }

    public void generarBombo() {
        for (int i = 1; i <= 90; i++) {
            bombo.add(i);
        }
        Collections.shuffle(bombo);
    }

    public void imprimirBombo() {
        System.out.println("Bombo:");
        for (int i = 0; i < bombo.size(); i++) {
            if (bombo.get(i) < 10) {
                System.out.print("0");
            }
            System.out.print(bombo.get(i) + " ");
            if ((i + 1) % 10 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        MulticastSocket socket = null;
        try {
            InetSocketAddress group = new InetSocketAddress(InetAddress.getByName(IP), PUERTO);
            socket = new MulticastSocket(PUERTO);
            socket.joinGroup(group, NetworkInterface.getByName("wlan0"));

            Servidor servidor = new Servidor();
            servidor.generarBombo();
            System.out.println();
            servidor.imprimirBombo();

            for (int i = 0; i < 90; i++) {
                int bola = servidor.bombo.get(i);
                String mensaje = String.format("%02d", bola);

                byte[] buffer = mensaje.getBytes();
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), PUERTO);
                socket.send(paquete);

                System.out.println("Ha salido la bola: " + bola);
                System.out.println("Quedan " + (90 - i - 1) + " bolas\n");
                Thread.sleep(100);
            }

            socket.leaveGroup(group, NetworkInterface.getByName("wlan0"));
            socket.close();

            System.out.println("¡FIN DEL JUEGO!\n");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
