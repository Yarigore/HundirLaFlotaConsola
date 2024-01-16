import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class TableroComportamiento {


    public void crearTablero(String[][] tablero){
        for (String[] strings : tablero) {
            Arrays.fill(strings, "▒");
        }
    }

    public void verTablero(String[][] tablero, String jugador){
        System.out.println(jugador);
        for (int i = 0; i < tablero.length; i++){
            System.out.print(i);
            for (int z = 0; z < tablero[i].length; z++){
                System.out.print(tablero[i][z] + " ");
            }
            System.out.println();
        }
        for (int c = 0; c < tablero[0].length; c++) {
            System.out.print(" " + c);
        }
        System.out.println();
    }

    public void verDondeAtaco(String[][] tablero, String resultado, String y, String x){
        if(resultado.equals("agua")){
            tablero[Integer.parseInt(y)][Integer.parseInt(x)] = "ø";
        }else {
            tablero[Integer.parseInt(y)][Integer.parseInt(x)] = "█";
        }
    }
    public String verDondeAtacan(String[][] tablero, String y, String x){
        System.out.println("Esta siendo atacado por el enemigo");
        System.out.println("Ataque recibido en: " + "Y:" + y + " X: " + x);

        if(tablero[Integer.parseInt(y)][Integer.parseInt(x)].equals("▒")){
            System.out.println("El enemigo a tocado agua");
            return "agua";
        }else{
            System.out.println("El enemigo te ha dado en un barco");
            return "barco";
        }
    }
    //hola

    public void comprobarVictoria(String[][]tablero, int cantidaBarcos, String barco, DataOutputStream dataOutputStream, Socket socket) throws IOException {
        int cantidadBarcoEncontrado = 0;

        for (int i = 0; i < tablero.length; i++){
            for(int z = 0; z < tablero[i].length; z++){

                if(tablero[i][z].equals(barco)){
                    cantidadBarcoEncontrado++;
                }

                if(cantidadBarcoEncontrado == cantidaBarcos){
                    System.out.println("Has ganado");
                    dataOutputStream.writeUTF("Has perdido");
                    socket.close();
                }else{
                    dataOutputStream.writeUTF("Sigue la partida");
                }
            }
        }
    }
}