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
        System.out.print("Carton: [ ");
        for (int i = 0; i < carton.size(); i++) {
            if (carton.get(i) < 10) {
                System.out.print("0");
            }
            if (i != carton.size() - 1) {
                System.out.print(carton.get(i) + ", ");
            } else {
                System.out.print(carton.get(i) + " ]");
            }
        }
        System.out.println("\n");
    }

    public static void main(String[] args) {
        MulticastSocket socket = null;
        try {
            InetSocketAddress group = new InetSocketAddress(InetAddress.getByName(IP), PUERTO);
            socket = new MulticastSocket(PUERTO);
            socket.joinGroup(group, NetworkInterface.getByName("wlan0"));

            Cliente cliente = new Cliente();
            cliente.generarCarton();
            System.out.println();
            cliente.imprimirCarton();

            while (cliente.hayBingo == false) {
                byte[] buffer = new byte[2];

                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                int bola = Integer.parseInt(new String(paquete.getData()));

                if (cliente.carton.contains(bola)) {
                    cliente.carton.remove(cliente.carton.indexOf(bola));
                    System.out.println("El numero " + bola + " esta en el carton");
                    if (cliente.carton.size() != 0) {
                        cliente.imprimirCarton();
                    }
                }

                if (cliente.carton.isEmpty()) {
                    cliente.hayBingo = true;
                    System.out.println("¡BINGO!\n");
                    socket.leaveGroup(group, NetworkInterface.getByName("wlan0"));
                    socket.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}