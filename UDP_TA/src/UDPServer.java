import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.*;

public class UDPServer 
{
	public static void main(String[] args) throws Exception 
	{
		DatagramSocket serverSocket = new DatagramSocket(9876);
		
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		
		while(true)
		{
			DatagramPacket paqueteRecibido = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(paqueteRecibido);
			
			String recibido =  new String(paqueteRecibido.getData());
						
			System.out.println("Me han solicitado conexión, enviando confirmación...");

			InetAddress IPAddress = paqueteRecibido.getAddress();
				
			int puerto =  paqueteRecibido.getPort();
				
			String enviado =  "OK";
			sendData = enviado.getBytes();
				
			DatagramPacket paqueteEnvidado = new DatagramPacket(sendData, sendData.length, IPAddress, puerto);
			serverSocket.send(paqueteEnvidado);	
			
			byte[] receiveData2 = new byte[1024];
			
			DatagramPacket paqueteRecibidoArchivos = new DatagramPacket(receiveData2, receiveData2.length);
			serverSocket.receive(paqueteRecibidoArchivos);
			
			String recibidoArchivos =  new String(paqueteRecibidoArchivos.getData());
			
			if(recibidoArchivos.trim().equals("ListoAR"))
			{
				System.out.println("Me han notificado que estan listos para recibir archivos");
				System.out.println("Procedo a enviar un archivo");
				
				byte[] sendData3 = new byte[1024];
				File archivo = new File("Foto.jpg");
				
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archivo));
					
				int in = 0;
				byte[] byteArray = new byte[8192];
				while ((in = bis.read(byteArray)) != -1)
				{
					DatagramPacket paqueteArchivo = new DatagramPacket(byteArray, byteArray.length, IPAddress, 9876);
					byteArrayOutputStream.write(byteArray,0,in);
				}
					 
				bis.close();

				System.out.println("No exploté");
			}
		}
		
	}
}
