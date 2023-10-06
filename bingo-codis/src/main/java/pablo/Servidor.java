package pablo;

import java.net.*;
import java.util.*;

public class Servidor extends Thread {

    // Constantes
    private static final String IP = "225.0.0.100";
    private static final int PUERTO = 6789;

    private ArrayList<Integer> bombo;

    public Servidor() {
        super();
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

    @Override
    public void run() {
        MulticastSocket socket = null;
        try {
            InetSocketAddress group = new InetSocketAddress(InetAddress.getByName(IP), PUERTO);
            socket = new MulticastSocket(PUERTO);
            socket.joinGroup(group, NetworkInterface.getByName("wlan0"));

            byte[] buffer = new byte[5];
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);
            System.out.println("¡FIN DEL JUEGO!\n");

            socket.leaveGroup(group, NetworkInterface.getByName("wlan0"));
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        MulticastSocket socket = null;
        try {
            Servidor servidor = new Servidor();
            socket = new MulticastSocket(PUERTO);

            Thread hiloEscucha = new Thread();
            hiloEscucha.start();

            servidor.generarBombo();
            System.out.println();
            servidor.imprimirBombo();

            for (int i = 0; i < 90 && hiloEscucha.isAlive(); i++) {
                int bola = servidor.bombo.get(i);
                String mensaje = String.format("%02d", bola);

                byte[] buffer = mensaje.getBytes();
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), PUERTO);
                socket.send(paquete);

                System.out.println("Ha salido la bola: " + bola);
                System.out.println("Quedan " + (90 - i - 1) + " bolas\n");
                Thread.sleep(300);
            }

            hiloEscucha.join();
            socket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
