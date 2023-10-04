package pablo;

import java.net.*;
import java.util.*;

public class Cliente {

    // Constantes
    private static final String IP = "225.0.0.100";
    private static final int PUERTO = 6789;

    private ArrayList<Integer> carton;
    private boolean hayBingo = false;

    public Cliente() {
        this.carton = new ArrayList<Integer>();
    }

    public void generarCarton() {
        int cont = 0;
        Random random = new Random();
        while (cont < 15) {
            int num = random.nextInt(90) + 1;
            if (!carton.contains(num)) {
                carton.add(num);
                cont++;
            }
        }
    }

    public void imprimirCarton() {
        System.out.println("Carton:");
        for (int i = 0; i < carton.size(); i++) {
            if (carton.get(i) < 10) {
                System.out.print("0");
            }
            System.out.print(carton.get(i) + " ");
            if ((i + 1) % 5 == 0) {
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

            Cliente cliente = new Cliente();
            cliente.generarCarton();
            cliente.imprimirCarton();

            while (cliente.hayBingo == false) {
                byte[] buffer = new byte[2];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                s.receive(paquete);
                int bola = Integer.parseInt(new String(paquete.getData()));
                System.out.println("Ha salido la bola: " + bola + "\n");

                if (cliente.carton.contains(bola)) {
                    cliente.carton.remove(cliente.carton.indexOf(bola));
                }

                if (cliente.carton.isEmpty()) {
                    cliente.hayBingo = true;
                }

            }

            System.out.println("BINGO!");

            s.leaveGroup(group);
            s.close();
            System.out.println("Fin del juego.\n");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}