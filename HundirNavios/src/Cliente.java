import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente{

    static TableroComportamiento tableroComportamiento = new TableroComportamiento();

    static String[][] tableroBarcos;
    static String[][] tableroEnemigo;

    //pedir datos por pantalla
    static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    static Scanner scanner = new Scanner(System.in);
    static String direccion = "";
    static int cantidadBarcos = 0;
    static int cantidadBarcosEnemigos = 0;

    public static void main(String args[]) throws IOException {

        String url = "localhost";
        int port = 8080;

        String movimientoX,movimientoY;
        String ataqueX, ataqueY;

        String resultado = "";
        String resultadoPartida = "";

        tableroBarcos = new String[][]{{"","","",""},{"","","",""},{"","","",""},{"","","",""}};
        tableroEnemigo = new String[][]{{"","","",""},{"","","",""},{"","","",""},{"","","",""}};

        tableroComportamiento.crearTablero(tableroEnemigo);
        tableroComportamiento.crearTablero(tableroBarcos);

        tableroComportamiento.verTablero(tableroEnemigo, "Enemigo");
        tableroComportamiento.verTablero(tableroBarcos, "Tu");

        ColocarBarco(2,"█");

        tableroComportamiento.verTablero(tableroEnemigo, "Enemigo");
        tableroComportamiento.verTablero(tableroBarcos, "Tu");

        try{
            Socket socket = new Socket(url, port);

            // Declara variable para Recibir información
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            // Declara variable para Enviar información
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.writeUTF(String.valueOf(cantidadBarcos));
            System.out.println("Partes de nuestros barcos: " + cantidadBarcos);

            cantidadBarcosEnemigos = Integer.parseInt(dataInputStream.readUTF());
            System.out.println("Partes de barcos enemigos: " + cantidadBarcosEnemigos);

            System.out.println();

            while (true){
                //Atacando
                System.out.print("Donde quieres atacar ↕ : ");
                movimientoY = bufferedReader.readLine();
                dataOutputStream.writeUTF(movimientoY);

                System.out.print("Donde quieres atacar ↔ : ");
                movimientoX = bufferedReader.readLine();
                dataOutputStream.writeUTF(movimientoX);

                resultado = dataInputStream.readUTF();
                System.out.println("Has tocado en: " + resultado);
                tableroComportamiento.verDondeAtaco(tableroEnemigo, resultado, movimientoY,movimientoX);
                dataOutputStream.flush();

                tableroComportamiento.verTablero(tableroEnemigo, "Enemigo");
                tableroComportamiento.verTablero(tableroBarcos, "Tu");

                //Atacado
                //Leo el mensaje que me envia
                ataqueY = dataInputStream.readUTF();
                //Leo el mensaje que me envia
                ataqueX = dataInputStream.readUTF();
                //Le envio un mensaje
                dataOutputStream.writeUTF(tableroComportamiento.verDondeAtacan(tableroBarcos, ataqueY, ataqueX));

                tableroComportamiento.verTablero(tableroEnemigo, "Enemigo");
                tableroComportamiento.verTablero(tableroBarcos, "Tu");
            }
        }
        catch(IOException e){
            System.out.println("ERROR: NO SE ENCONTRO EL SERVIDOR");
        }
    }

    private static void ColocarBarco(int lonngitudBarco, String imagenBarco) throws IOException{
        int colocarY, colocarX;

        cantidadBarcos += lonngitudBarco;

        System.out.print("Posicion en vertical donde quieres poner el barco: ");
        colocarY = scanner.nextInt();

        System.out.print("Posicion en horizontal donde quieres poner el barco: ");
        colocarX = scanner.nextInt();
        tableroBarcos[colocarY][colocarX] = imagenBarco;

        direccionBarco(lonngitudBarco, imagenBarco, colocarY, colocarX);
    }

    private static void direccionBarco(int longitudBarco, String imagenBarco, int colocarY, int colocarX) throws IOException {
        boolean correcto = false;

        while (!correcto) {
            System.out.println("1.Arriba 2.Derecha 3.Abajo 4. Izquierda");
            System.out.print("Hacia donde quieres que mire el barco: ");
            direccion = bufferedReader.readLine();

            switch (direccion) {
                case "1" -> {
                    if (colocarY - longitudBarco + 1 >= 0) {
                        System.out.println("has elegido que el barco mire para arriba");
                        for (int i = 0; i < longitudBarco; i++) {
                            tableroBarcos[colocarY - i][colocarX] = imagenBarco;
                        }
                        correcto = true;
                    } else {
                        System.out.println("No puedes salir del mar D:");
                        System.out.println();
                    }
                }
                case "2" -> {
                    if (colocarX + longitudBarco <= tableroBarcos[colocarY].length) {
                        System.out.println("has elegido que el barco mire para la derecha");
                        for (int i = 0; i < longitudBarco; i++) {
                            tableroBarcos[colocarY][colocarX + i] = imagenBarco;
                        }
                        correcto = true;
                    } else {
                        System.out.println("No puedes salir del mar D:");
                        System.out.println();
                    }
                }
                case "3" -> {
                    if (colocarY + longitudBarco <= tableroBarcos.length) {
                        System.out.println("has elegido que el barco mire para abajo");
                        for (int i = 0; i < longitudBarco; i++) {
                            tableroBarcos[colocarY + i][colocarX] = imagenBarco;
                        }
                        correcto = true;
                    } else {
                        System.out.println("No puedes salir del mar D:");
                        System.out.println();
                    }
                }
                case "4" -> {
                    if (colocarX - longitudBarco + 1 >= 0) {
                        System.out.println("has elegido que el barco mire para la izquierda");
                        for (int i = 0; i < longitudBarco; i++) {
                            tableroBarcos[colocarY][colocarX - i] = imagenBarco;
                        }
                        correcto = true;
                    } else {
                        System.out.println("No puedes salir del mar D:");
                        System.out.println();
                    }
                }
                default -> {
                    System.out.println("Esta direccion no existe elija de nuevo");
                }
            }
        }
    }
}