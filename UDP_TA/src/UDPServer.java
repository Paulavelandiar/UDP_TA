import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.*;

public class UDPServer 
{
	public static void main(String[] args) throws Exception 
	{
		Scanner scanner = new Scanner(System.in);
		DatagramSocket serverSocket = new DatagramSocket(9876);
		System.out.println("Elija un archivo");
		System.out.println("1-100MiB");
		System.out.println("2-250MiB");

		int arch = Integer.parseInt(scanner.nextLine());
		
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		
		while(true)
		{
			PrintWriter bf = new PrintWriter(new BufferedWriter(new FileWriter("./data/logs.txt",true)));
			bf.write(new Date() + " \n");
			bf.flush();
			
			
			DatagramPacket paqueteRecibido = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(paqueteRecibido);
			
			paqueteRecibido.getData();
						
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
				
				File archivo = null;
				
				if(arch == 1) {
					archivo = new File("./data/small.png");
				}
				else {

					archivo = new File("./data/large.png");
				}

				bf.write("Se va a enviar el archivo "+ archivo.getPath() +" de tamano " + archivo.length() + " \n");

				
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archivo));
				
				int numero = (int) (archivo.length()/8192)+1;
				
				String num = "" + numero;
				long tiempoInicial = 0;
				DatagramPacket paqueteNum = new DatagramPacket(num.getBytes(),num.getBytes().length,IPAddress,puerto);
				serverSocket.send(paqueteNum);
				System.out.println(num);
				int in = 0;
				byte[] byteArray = new byte[8192];
				int cont = 0;
				tiempoInicial = System.nanoTime();
				while ((in = bis.read(byteArray)) != -1)
				{
					DatagramPacket paqueteArchivo = new DatagramPacket(byteArray, byteArray.length, IPAddress, puerto);
					byteArrayOutputStream.write(byteArray,0,in);
					serverSocket.send(paqueteArchivo);
					String res = resultadoDigest(byteArray);
					sendData = res.getBytes("UTF-8");
					DatagramPacket paqueteDigest = new DatagramPacket(sendData, sendData.length, IPAddress, puerto);
					serverSocket.send(paqueteDigest);
					System.out.println("Holi " + in);
					System.out.println("El digest es: " + res);
					cont ++;
					System.out.println(cont);
				}
				
				long tiempo = System.nanoTime() - tiempoInicial;
				
				bf.write("El tiempo de envio del archivo fue de : "+ (tiempo)/(1000000)+ "segundos" + " \n");

					 
				bis.close();
				
				bf.close();
			}
		}
		
	}
	
	public static String resultadoDigest(byte[] paquete) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		
		md.update(paquete);
		byte[] digest = md.digest();      

		StringBuffer hexString = new StringBuffer();

		for (int i = 0;i<digest.length;i++) 
		{
			hexString.append(Integer.toHexString(0xFF & digest[i]));
		}
		String prueba = hexString.toString();
		
		return prueba;
	}
	
	
	
}
