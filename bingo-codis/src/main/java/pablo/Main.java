package pablo;

public class Main {

    public static void main(String[] args) {
        Cliente pablo = new Cliente();
        System.out.println();
        pablo.generarCarton();
        pablo.imprimirCarton();
        Servidor servidor = new Servidor();
        servidor.generarBombo();
        servidor.imprimirBombo();
        System.out.println("Ha salido la bola: " + servidor.sacarBola() + "\n");
        servidor.imprimirBombo();
        System.out.println();
    }

}