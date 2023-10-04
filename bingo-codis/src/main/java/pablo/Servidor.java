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
    }

    public int sacarBola(Random random) {
        int bola = random.nextInt(90) + 1;
        int num = bombo.get(bola);
        bombo.remove(bola);
        return num;
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
        MulticastSocket s = null;
        try {
            InetAddress group = InetAddress.getByName(IP);
            s = new MulticastSocket(PUERTO);
            s.joinGroup(group);

            Servidor servidor = new Servidor();
            Random random = new Random();
            servidor.generarBombo();
            servidor.imprimirBombo();

            for (int i = 0; i < 90; i++) {
                int bola = servidor.sacarBola(random);
                String mensaje = String.format("%02d", bola);
                byte[] buffer = mensaje.getBytes();
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, group, PUERTO);
                s.send(paquete);
                System.out.println("Ha salido la bola: " + bola + "\n");
                Thread.sleep(1000);
            }

            s.leaveGroup(group);
            s.close();
            System.out.println("Fin del juego.\n");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
