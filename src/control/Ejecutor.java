package control;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Ejecutor extends JFrame
{
	
	public Ejecutor(){
		setForeground(Color.BLACK);
		setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 12));
		setBackground(Color.BLACK);
		getContentPane().setLayout(null);
		
		JTextArea txtrQueAccionDesea = new JTextArea();
		txtrQueAccionDesea.setFont(new Font("Constantia", Font.ITALIC, 13));
		txtrQueAccionDesea.setText("Que accion desea realizar? Ingrese:\r\n1 - Transferencia ASCII\r\n2- Transferencia binaria\r\n3 - Subir un archivo\r\n4- Descargar un archivo del servidor\r\n5- Eliminar un archivo del servidor\r\n6- Cambiar el nombre de una arhivo del servidor\r\n7- Ver la lista de archivos disponibles en el servidor\r\n8- Cambiar el fichero de acceso\r\n9- Salir\r\n");
		txtrQueAccionDesea.setBounds(10, 11, 414, 226);
		getContentPane().add(txtrQueAccionDesea);
		setSize(444,293);
	}

	// Dirección IP del servidor
	static InetAddress localhost = null;

	public static void main(String[] args)
	{
		try
		{
			localhost = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e1)
		{
			System.out.println("Error obteniendo la direccion localhost");
		}

		Ejecutor eje = new Ejecutor();
		eje.setVisible(true);
		Cliente hilo = new Cliente(eje);
		hilo.start();

	}
}