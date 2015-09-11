package control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class PuertoDatos extends Thread {

	//Socket del servidor que crea la conexion de datos con el servidor ftp
	private ServerSocket server;
	//Socket donde se comunican las partes
	private Socket soc;
	
	//Cadena con el Mensaje de comando a realizar
	private String msj;
	
	//El nombre del archivo a modificar o buscar
	private String archName;
	//Archivo a transferir
	private File fTransfer;
	//Clase de la interfaz para el llamado de herramientas visuales
	private Ejecutor e;
	
	/**
	 * Constructor de la Clase
	 * @param puerto, el número de puerto de la conexion de datos
	 * @param msg, el comando a realizar
	 * @param eje, la clase de la interfaz grafica
	 */
public PuertoDatos(int puerto, String msg, Ejecutor eje){
		
		try {
			//Se crea el socket del servidor por el puerto especificado
			server = new ServerSocket(puerto);
			//Se crea el socket para la comunicación
			soc = server.accept();
			
			msj = msg;
			this.e = eje;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Constructor de la clase
	 *  @param puerto, el número de puerto de la conexion de datos
	 * @param msg, el comando a realizar
	 * @param name, el nombre del archivo que se desea buscar
	 */
	public PuertoDatos(int puerto, String msg, String name){
		
		try {
			server = new ServerSocket(puerto);
			soc = server.accept();
			
			msj = msg;
			archName = name;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Constructor de la clase
	 *  @param puerto, el número de puerto de la conexion de datos
	 * @param msg, el comando a realizar
	 * @param f, el archivo que se va a enviar al servidor FTP
	 */
public PuertoDatos(int puerto, String msg, File f){
		
		try {
			server = new ServerSocket(puerto);
			soc = server.accept();
			
			msj = msg;
			this.fTransfer = f;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void run()
	{
		try {
		if(msj.equalsIgnoreCase("RETR")){
			
			String path = System.getProperty("user.dir") + "/root"  + "/"+ archName;
				File chevere = new File(path);
					if(soc.isConnected()){
							
						if(chevere.exists()){
							
							System.out.println("Se sobreescribe: " + archName);
						}	
							//Se descarga el archivo y se guarda en la carpeta
						byte[] entrada= new byte[soc.getInputStream().available()];
						
						soc.getInputStream().read(entrada);
						FileOutputStream fos = new FileOutputStream(path);
						BufferedOutputStream bos = new BufferedOutputStream(fos);
						bos.write(entrada);
						
					}
					soc.close();
					server.close();
			
		}
		else if(msj.equalsIgnoreCase("STOR")){
			
			
				
					if(soc.isConnected()){
							
						if(fTransfer.exists()){
							
							//Se descarga el archivo y se guarda en un buffer de bytes
						byte[] entrada= new byte[(int) fTransfer.length()];
						FileInputStream fis = new FileInputStream(fTransfer);
						BufferedInputStream bis = new BufferedInputStream(fis);						
						bis.read(entrada);
						//Se monta al canal el arreglo de bytes del archivo
						soc.getOutputStream().write(entrada);
						
					}
						}
					soc.close();
					server.close();
			
		}
		//Comando de mostrar los ficheros
      if(msj.equalsIgnoreCase("MLSD")){
			
			//Dirección donde se cargaran los ficheros del servidor
    	  String path = System.getProperty("user.dir") + "/root"  + "/"+ "ficheros.txt";
			
			
				if(soc.isConnected()){
					
					File f = new File(path);
					//Se crea el escritor de archivos
					FileWriter w = new FileWriter(f);					
					BufferedWriter bw = new BufferedWriter(w);					
					PrintWriter wr = new PrintWriter(bw); 
					
					//Se solicita el tamano de la informacion a recibir
					int ava = soc.getInputStream().available();
						//Se descarga el archivo y se guarda en la carpeta
					byte[] entrada= new byte[ava];
					System.out.println(ava);
					soc.getInputStream().read(entrada);
					
									
					String bla = "";
					//Se convierte cada bytes en un solo texto
					for(int i = 0; i<entrada.length;i++){
						bla = bla + (char)entrada[i];
						
					}
					
					
					
						
						
						wr.write(bla);//escribimos en el archivo
						wr.flush();
						wr.append(" - y aqui continua"); 
				}
				soc.close();
				server.close();
		
			
		}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
