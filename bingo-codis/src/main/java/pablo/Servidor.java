package pablo;

import java.util.ArrayList;

public class Servidor {

    private ArrayList<Integer> bombo;

    public Servidor() {
        this.bombo = new ArrayList<Integer>();
    }

    public void generarBombo() {
        for (int i = 1; i <= 90; i++) {
            bombo.add(i);
        }
    }

    public int sacarBola() {
        int bola = (int) (Math.random() * bombo.size());
        int num = bombo.get(bola);
        bombo.remove(bola);
        return num;
    }

    public void imprimirBombo() {
        System.out.println("Bombo:");
        for (int i = 0; i < bombo.size(); i++) {
            if (bombo.get(i) < 10) {
                System.out.print(" ");
            }
            System.out.print(bombo.get(i) + " ");
            if ((i + 1) % 10 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

}
