import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.*;

public class UDPClient 
{
	public static void main(String[] args) throws Exception
	{
		BufferedReader inFromUser = new BufferedReader (new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		System.out.println("Solicite la conexión escribiendo 'Conexión'...");
		
		String envia = "Conexión";
		sendData = envia.getBytes();
		
		DatagramPacket paqueteEnvidado = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		clientSocket.send(paqueteEnvidado);
				
		DatagramPacket paqueteRecibido = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(paqueteRecibido);
		
		String recibido = new String(paqueteRecibido.getData());
		
		if(recibido.trim().equals("OK"))
		{
			System.out.println("Recibí confirmación del servidor");
		}
		
		byte[] sendData2 = new byte[1024];
		
		String archivos = "ListoAR";
		sendData2 = archivos.getBytes();
		
		DatagramPacket paqueteEnviadoArchivos = new DatagramPacket(sendData2, sendData2.length, IPAddress, 9876);
		clientSocket.send(paqueteEnviadoArchivos);
		
		System.out.println("He enviado notificación de que puedo recibir archivos");
		
		//De aquí para abajo no sé que onda, probablemente nada este bien
		while(true) 
		{	
			
			byte[] sendData3 = new byte[8192];
			
			DatagramPacket paqueteArchivos = new DatagramPacket(sendData3, sendData3.length, IPAddress, 9876);
			clientSocket.receive(paqueteArchivos);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        
			int read;
			
	        while((read = sendData3.length) != -1)
	        {
	            bos.write(sendData3, 0, read);
	            
	            bos.flush();
	            
	            System.out.println("RECIBÍ: " + sendData3);
	            
	        }
	        
	        
	        		
		}
	}
}

