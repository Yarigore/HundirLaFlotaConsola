import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor {

    static String[][] tableroBarcos;
    static String[][] tableroEnemigo;
    static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    static Scanner scanner = new Scanner(System.in);
    static String direccion = "";
    static int cantidadBarcos = 0;
    static int cantidadBarcosEnemigos = 0;
    static boolean volverAtacar;
    static int colocarY, colocarX;
    public static void main(String[] args) throws IOException {

        TableroComportamiento tableroComportamiento = new TableroComportamiento();
        BarcoComportamiento barcoComportamiento = new BarcoComportamiento();

        int port = 8080;
        ServerSocket serverSocket = null;

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

        ColocarBarco(2, "█");

        tableroComportamiento.verTablero(tableroEnemigo, "Enemigo");
        tableroComportamiento.verTablero(tableroBarcos, "Tu");

        ColocarBarco(3, "█");

        tableroComportamiento.verTablero(tableroEnemigo, "Enemigo");
        tableroComportamiento.verTablero(tableroBarcos, "Tu");

        try{
            // Creamos el socket Servidor
            serverSocket = new ServerSocket(port);

            //Espero que un cliente se conecte
            Socket socket = serverSocket.accept();

            // Declara variable para Enviar información
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Declara variable para Recibir información
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            cantidadBarcosEnemigos = Integer.parseInt(dataInputStream.readUTF());
            System.out.println("Partes de barcos enemigos: " + cantidadBarcosEnemigos);

            dataOutputStream.writeUTF(String.valueOf(cantidadBarcos));
            System.out.println("Partes de nuestros barcos: " + cantidadBarcos);

            System.out.println();

            while(true){
                do{
                    //Atacado
                    //Leo los mensajes que me envia
                    ataqueY = dataInputStream.readUTF();
                    ataqueX = dataInputStream.readUTF();

                    //Le envio un mensaje
                    String tocado = tableroComportamiento.verDondeAtacan(tableroBarcos, ataqueY, ataqueX);
                    dataOutputStream.writeUTF(tocado);

                    tableroComportamiento.verTablero(tableroEnemigo, "Enemigo");
                    tableroComportamiento.verTablero(tableroBarcos, "Tu");

                    resultadoPartida = dataInputStream.readUTF();
                    if(resultadoPartida.equals("Has perdido")){
                        System.out.println(resultadoPartida);
                        socket.close();
                        System.exit(-1);
                    }

                    if(tocado.equals("agua")){
                        volverAtacar = false;
                    }else {
                        volverAtacar = true;
                        System.out.println("Te han detectado");
                    }

                }while (volverAtacar);

                do {
                    //Atacando
                    do {
                        System.out.print("Donde quieres atacar ↕ : ");
                        movimientoY = bufferedReader.readLine();
                        if(Integer.parseInt(movimientoY) < 0 || Integer.parseInt(movimientoY) >= tableroBarcos.length){
                            System.out.println("No puedes salir del mar D: \n");
                        }
                    }while (Integer.parseInt(movimientoY) < 0 || Integer.parseInt(movimientoY) >= tableroBarcos.length);
                    dataOutputStream.writeUTF(movimientoY);

                    do{
                        System.out.print("Donde quieres atacar ↔ : ");
                        movimientoX = bufferedReader.readLine();
                        if(Integer.parseInt(movimientoX) < 0 || Integer.parseInt(movimientoX) >= tableroBarcos.length){
                            System.out.println("No puedes salir del mar D: \n");
                        }
                    }while (Integer.parseInt(movimientoX) < 0 || Integer.parseInt(movimientoX) >= tableroBarcos.length);

                    dataOutputStream.writeUTF(movimientoX);

                    resultado = dataInputStream.readUTF();
                    System.out.println("Has tocado en: " + resultado);
                    tableroComportamiento.verDondeAtaco(tableroEnemigo, resultado, movimientoY, movimientoX);

                    tableroComportamiento.verTablero(tableroEnemigo, "Enemigo");
                    tableroComportamiento.verTablero(tableroBarcos, "Tu");

                    tableroComportamiento.comprobarVictoria(tableroEnemigo, cantidadBarcosEnemigos, "█", dataOutputStream, socket);

                    if(resultado.equals("agua")){
                        volverAtacar = false;
                    }else {
                        volverAtacar = true;
                        System.out.println("Vueve a atacar");
                    }
                    dataOutputStream.flush();
                }while (volverAtacar);
            }
        }
        catch(IOException e){
            System.out.println("ERROR EN EL CLIENTE");
        }
    }
    private static void ColocarBarco(int lonngitudBarco, String imagenBarco) throws IOException{
        int colocarY, colocarX;

        cantidadBarcos += lonngitudBarco;

        do {
            do {
                System.out.print("Posicion en vertical donde quieres poner el barco: ");
                colocarY = scanner.nextInt();
                if(colocarY < 0 || colocarY >= tableroBarcos.length){
                    System.out.println("No puedes salir del mar D: \n");
                }
            }while (colocarY < 0 || colocarY >= tableroBarcos.length);

            do{
                System.out.print("Posicion en horizontal donde quieres poner el barco: ");
                colocarX = scanner.nextInt();
                if(colocarX < 0 || colocarX >= tableroBarcos.length){
                    System.out.println("No puedes salir del mar D: \n");
                }
            }while (colocarX < 0 || colocarX >= tableroBarcos.length);

            if(tableroBarcos[colocarY][colocarX].equals(imagenBarco)){
                System.out.println("Ya hay un barco en ese lugar \n");
            }
        }while (tableroBarcos[colocarY][colocarX].equals(imagenBarco));

        tableroBarcos[colocarY][colocarX] = imagenBarco;

        direccionBarco(lonngitudBarco, imagenBarco, colocarY, colocarX);
        System.out.println();
    }
    private static void direccionBarco(int longitudBarco, String imagenBarco, int colocarY, int colocarX) throws IOException {
        boolean correcto = false;
        boolean trapasado = false;

        while (!correcto) {
            trapasado = false;
            System.out.println("1.Arriba 2.Derecha 3.Abajo 4. Izquierda");
            System.out.print("Hacia donde quieres que mire el barco: ");
            direccion = bufferedReader.readLine();

            switch (direccion) {
                case "1" -> {
                    if (colocarY - longitudBarco + 1 >= 0 && !trapasado) {
                        for (int i = 1; i < longitudBarco; i++){
                            if (tableroBarcos[colocarY - i][colocarX].equals(imagenBarco)) {
                                trapasado = true;
                                System.out.println("No puedes atravesar otro buque \n");
                                break;
                            }
                        }
                        if(!trapasado){
                            System.out.println("has elegido que el barco mire para arriba");
                            for (int i = 0; i < longitudBarco; i++) {
                                tableroBarcos[colocarY - i][colocarX] = imagenBarco;
                            }
                            correcto = true;
                        }
                    } else {
                        System.out.println("No puedes salir del mar D: \n");
                    }
                }
                case "2" -> {
                    if (colocarX + longitudBarco <= tableroBarcos[colocarY].length) {
                        for (int i = 1; i < longitudBarco; i++){
                            if (tableroBarcos[colocarY][colocarX + i].equals(imagenBarco)) {
                                trapasado = true;
                                System.out.println("No puedes atravesar otro buque \n");
                                break;
                            }
                        }
                        if(!trapasado){
                            System.out.println("has elegido que el barco mire para la derecha");
                            for (int i = 0; i < longitudBarco; i++) {
                                tableroBarcos[colocarY][colocarX + i] = imagenBarco;
                            }
                            correcto = true;
                        }
                    } else {
                        System.out.println("No puedes salir del mar D: \n");
                    }
                }
                case "3" -> {
                    if (colocarY + longitudBarco <= tableroBarcos.length) {
                        for (int i = 1; i < longitudBarco; i++){
                            if (tableroBarcos[colocarY + i][colocarX].equals(imagenBarco)) {
                                trapasado = true;
                                System.out.println("No puedes atravesar otro buque \n");
                                break;
                            }
                        }
                        if(!trapasado){
                            System.out.println("has elegido que el barco mire para abajo");
                            for (int i = 0; i < longitudBarco; i++) {
                                tableroBarcos[colocarY + i][colocarX] = imagenBarco;
                            }
                            correcto = true;
                        }
                    } else {
                        System.out.println("No puedes salir del mar D: \n");
                    }
                }
                case "4" -> {
                    if (colocarX - longitudBarco + 1 >= 0) {
                        for (int i = 1; i < longitudBarco; i++){
                            if (tableroBarcos[colocarY][colocarX - i].equals(imagenBarco)) {
                                trapasado = true;
                                System.out.println("No puedes atravesar otro buque \n");
                                break;
                            }
                        }
                        if(!trapasado){
                            System.out.println("has elegido que el barco mire para la izquierda");
                            for (int i = 0; i < longitudBarco; i++) {
                                tableroBarcos[colocarY][colocarX - i] = imagenBarco;
                            }
                            correcto = true;
                        }
                    } else {
                        System.out.println("No puedes salir del mar D: \n");
                    }
                }
                default -> {
                    System.out.println("Esta direccion no existe elija de nuevo \n");
                }
            }
        }
    }
}