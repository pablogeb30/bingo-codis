package pablo;

import java.util.ArrayList;

public class Cliente {

    private ArrayList<Integer> carton;

    public Cliente() {
        this.carton = new ArrayList<Integer>();
    }

    public void generarCarton() {
        int cont = 0;
        while (cont < 15) {
            int num = (int) (Math.random() * 90 + 1);
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
                System.out.print(" ");
            }
            System.out.print(carton.get(i) + " ");
            if ((i + 1) % 5 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

}