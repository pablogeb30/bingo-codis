import java.net.*;
import java.util.*;

public class Cliente {

    // IP y puerto utilizados para la comunicacion multicast (constantes)
    private static final String IP = "225.0.0.100";
    private static final int PUERTO = 6789;

    // ArrayList que contiene los numeros del carton
    private ArrayList<Integer> carton;

    // Constructor de la clase Cliente
    public Cliente() {
        this.carton = new ArrayList<Integer>();
    }

    // Funcion que genera un carton de bingo aleatorio de 15 numeros
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

    // Funcion que imprime el carton de bingo
    public void imprimirCarton() {
        System.out.print("Carton(" + carton.size() + "): [ ");
        for (int i = 0; i < carton.size(); i++) {
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

    // Funcion principal del programa, encargada de gestionar los distintos mensajes
    public static void main(String[] args) {
        // Creamos el socket multicast
        MulticastSocket socket = null;

        // Debemos gestionar las expcepciones que puedan surgir
        try {
            // Nos unimos al grupo multicast
            InetSocketAddress group = new InetSocketAddress(InetAddress.getByName(IP), PUERTO);
            socket = new MulticastSocket(PUERTO);
            socket.joinGroup(group, NetworkInterface.getByName("wlan0"));

            // Creamos una instancia de la clase cliente generamos su carton y lo imprimimos
            Cliente cliente = new Cliente();
            cliente.generarCarton();
            System.out.println();
            cliente.imprimirCarton();
            System.out.println();

            while (true) {
                // Creamos el datagrama para recibir el numero del servidor
                byte[] buffer = new byte[2];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                // Cuando se recibe "GG", este cliente ha perdido y termina su ejecucion
                if (!cliente.carton.isEmpty() && new String(paquete.getData()).equals("GG")) {
                    System.out.println("\n¡FIN DEL JUEGO!\nHAS PERDIDO LA PARTIDA\n");
                    break;
                }

                // Cuando se recibe un numero, se imprime su valor
                int num = Integer.parseInt(new String(paquete.getData()));
                System.out.println("\nHa salido la bola: " + num);

                // Si esta en el carton se elimina y se imprime el carton actualizado
                if (cliente.carton.contains(num)) {
                    cliente.carton.remove(cliente.carton.indexOf(num));
                    cliente.imprimirCarton();
                    System.out.println();
                }

                // Cuando el carton se queda vacio, el cliente manda un mensaje al resto
                if (cliente.carton.isEmpty()) {
                    // Se envia el mensaje "GG" al resto de clientes
                    byte[] mensaje = "GG".getBytes();
                    DatagramPacket respuesta = new DatagramPacket(mensaje, mensaje.length, group);
                    socket.send(respuesta);

                    // El cliente que gana es el encargado de dejar el grupo y cerrar el socket
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
