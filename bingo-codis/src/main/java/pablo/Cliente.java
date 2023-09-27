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
        imprimirCarton();
    }

    public void imprimirCarton() {
        System.out.println("Carton:");
        for (int i = 0; i < 15; i++) {
            System.out.print(carton.get(i) + " ");
        }
        System.out.println();
    }

}