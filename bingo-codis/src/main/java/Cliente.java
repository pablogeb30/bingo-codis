import java.net.*;
import java.util.*;

public class Cliente {

    // Constantes
    private static final String IP = "225.0.0.100";
    private static final int PUERTO = 6789;

    private ArrayList<Integer> carton;

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
        System.out.print("Carton (" + carton.size() + "): [ ");
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
        if (carton.isEmpty()) {
            System.out.print(" ]");
        }
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
            System.out.println();
            byte[] buffer = new byte[2];
            while (true) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                if (!cliente.carton.isEmpty() && new String(paquete.getData()).equals("GG")) {
                    System.out.println("\n¡FIN DEL JUEGO!\nHAS PERDIDO LA PARTIDA\n");
                    break;
                }
                int num = Integer.parseInt(new String(paquete.getData()));
                System.out.println("\nHa salido la bola: " + num);
                if (cliente.carton.contains(num)) {
                    cliente.carton.remove(cliente.carton.indexOf(num));
                    cliente.imprimirCarton();
                    System.out.println();
                }
                if (cliente.carton.isEmpty()) {
                    byte[] mensaje = "GG".getBytes();
                    DatagramPacket respuesta = new DatagramPacket(mensaje, mensaje.length, group);
                    socket.send(respuesta);
                    socket.leaveGroup(group, NetworkInterface.getByName("wlan0"));
                    socket.close();
                    System.out.println("\n¡BINGO!\nHAS GANADO LA PARTIDA\n");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
