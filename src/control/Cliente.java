package control;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Cliente extends Thread {

	// ParÃ¡metros de la conexiÃ³n de control
	
	//Socket para la conexion de de control con el servidor.
	private Socket sktControl;
	
	//Escritor de mensajes sobre el canal
	private PrintWriter outControl;
	
	//booleano para el cierre de conexion
	private boolean cerrarConexion;
	//cambio de binario a ascii
	private boolean isAscii = true;
	// dirección ip local
	private InetAddress localhost;
	
	// Lector de mensajes del servidor
	private BufferedReader input;
	// Client DTP

	
  //dirección del fichero actual
	private String currentPath;
	//Clase del main
	private Ejecutor e;

	/**
	 * constructor de la clase
	 * @param eje clase main
	 */
	public Cliente(Ejecutor eje) {

		try {
			e = eje;
			localhost = InetAddress.getByName("127.0.0.1");
			sktControl = new Socket(localhost, 4001);
			outControl = new PrintWriter(sktControl.getOutputStream(), true);
			cerrarConexion = false;
			input = new BufferedReader(new InputStreamReader(
					sktControl.getInputStream()));

			currentPath = System.getProperty("user.dir") + "/root";

		} catch (IOException e) {
			System.out.println("Error creando el socket");
		}
	}

	@Override
	public void run() {

		try {

			System.out.println("Conectado");
			System.out.println(input.readLine());
			System.out.println(input.readLine());
			System.out.println(input.readLine());

			System.out.println("Ingrese el nombre de usuario:");
			Scanner in = new Scanner(System.in);
			String user = in.nextLine();
			outControl.println("USER " + user);
			System.out.println(input.readLine());

			// PASS
			System.out.println("Ingrese su contrasena:");
			String pss = in.nextLine();
			outControl.println("PASS " + pss);
			System.out.println(input.readLine());

			// Escritura de comandos
			System.out.println("Que accion desea realizar:");
			System.out.println("1- Transferencia ASCII");
			System.out.println("2- Transferencia binaria");
			System.out.println("3- SUbir un archivo");
			System.out.println("4- Descargar un archivo");
			System.out.println("5- Eliminar un archivo del servidor");
			System.out.println("6- Cambiar el nombre de un archivo");
			System.out.println("7- Ver una lista de archivos disponibles en el servidor");
			System.out.println("8- Cambiar el directorio de acceso");
			System.out.println("9- salir");
			
			
			// Entrar al fichero remoto generico
			outControl.println("CWD /");
			input.readLine();

			while (cerrarConexion == false) {

				// Login al servidor
				// USER

				System.out.println(">>");
				String texto = in.nextLine();

				// Transferencia por medio de ASCII
				if (texto.equals("1")) {
					outControl.println("TYPE ASCII");
					System.out.println(input.readLine());

				}
				// Transferencia binaria
				else if (texto.equalsIgnoreCase("2")) {
					outControl.println("TYPE I");
					System.out.println(input.readLine());
					isAscii = false;

				}
				// Subir un archivo desde la carpeta de root de usuario, dando
				// el nombre
				else if (texto.equalsIgnoreCase("3")) {
					System.out.println("Por favor seleccione el archivo a enviar");
					JFileChooser aja = new JFileChooser(this.currentPath);
					aja.showOpenDialog(e);
					File f = aja.getSelectedFile();
					String narch = f.getName();
                    
					
					outControl.println("TYPE I");
					input.readLine();
					//Establece el puerto de datos como el 4002
					outControl.println("PORT 127,0,0,1,15,162");
					System.out.println(input.readLine());
                    //Envía el comando STOR + nombre del archivo a enviar para subir el documento.
					outControl.println("STOR " + narch);
					String resp = input.readLine();
					
					//Si el mensaje inicia con 4 o con 5, implica un error
					if (resp.startsWith("4") || resp.startsWith("5")) {

						System.out.println(resp);
					}
					//De otra forma, crea la conexión para que el servidor pueda abrir el canal.
					else {

						System.out.println(resp);
						PuertoDatos pd = new PuertoDatos(4002, "STOR", f);
						pd.start();
						System.out.println(input.readLine());
					}

				} 
				//Descargar archivo desde el servidor
				else if (texto.equalsIgnoreCase("4")) {

					System.out.println("Ingrese el nombre del archivo a descargar:");
					String nom = in.nextLine();
					outControl.println("PORT 127,0,0,1,15,162");
					System.out.println(input.readLine());
					outControl.println("RETR " + nom);
					String resp = input.readLine();
					if (resp.startsWith("4") || resp.startsWith("5")) {

						System.out.println(resp);
					} else {

						System.out.println(resp);
						PuertoDatos pd = new PuertoDatos(4002, "RETR", nom);
						pd.start();
						System.out.println(input.readLine());
					}

				}
				// Eliminar Un archivo del servidor
				else if (texto.equalsIgnoreCase("5")) {

					System.out.println("Ingrese el nombre del archivo a eliminar:");
					String nom = in.nextLine();
					
					
					// Enviar comando DELE + nombre de archivo, para eliminar un
					// archivo del servidor.
					outControl.println("DELE " + nom);
					System.out.println(input.readLine());

				} else if (texto.equalsIgnoreCase("6")) {

					System.out.println("Ingrese el nombre del archivo que desea renombrar:");
					String nom = in.nextLine();
					//Envia el comando RNFR + nombre, para acceder al archivo destino a renombrar
					outControl.println("RNFR " + nom);
					System.out.println(input.readLine());
					
					//Envia el comando RNTO + nombre nuevo, para cambiar el nombre del archivo
					System.out.println("Ingrese el nuevo nombre para el archivo:");
					String nom2 = in.nextLine();
					outControl.println("RNTO " + nom2);
					System.out.println(input.readLine());

				}else if (texto.equalsIgnoreCase("7")) {

					//Envia el puerto de datos por donde se comunicara, en este caso el 4002
					outControl.println("PORT 127,0,0,1,15,162");
					System.out.println(input.readLine());
					//Enviar el comando MLSD para solicitar la lista de los ficheros y archivos en el servidor.
					outControl.println("MLSD");
					String resp = input.readLine();
					//Si el mensaje empieza en 4 o 5, implica un error
					if (resp.startsWith("4") || resp.startsWith("5")) {

						System.out.println(resp);
					} else {

						System.out.println(resp);
						String soluc="";
						PuertoDatos pd = new PuertoDatos(4002, "MLSD", e);
						pd.start();
						
						System.out.println(input.readLine());
					}

				}//Cambiar directorio
				else if (texto.equalsIgnoreCase("8")) {
					System.out.println("Ingrese el nombre del directorio al que desea entrar:");
					String nom = in.nextLine();

					//Envia el puerto de datos por donde se comunicara, en este caso el 4002
					outControl.println("CWD /" +  nom);
					System.out.println(input.readLine());
					}
				else if (texto.equalsIgnoreCase("9")){
					System.out.println("Muchas gracias por acceder a Camilo's FTPClient");
					this.sktControl.close();
					
					this.cerrarConexion=true;
					
				}
				else{
					if(input.ready()){
						
						System.out.println(input.readLine());
					}
					else{
						System.out.println("ingrese un comando valido");
					}
					
					
				}

			}

		} catch (IOException e) {
			System.out.println("Hay un problema en el flujo de informacion");

		}

		System.out.println("La conexion se ha cerrado");
       e.dispose();
	}

}
