import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class UDPClient 
{
	public static void main(String[] args) throws Exception
	{
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		PrintWriter bf = new PrintWriter(new BufferedWriter(new FileWriter("./data/logsCliente.txt",true)));
		bf.write(new Date() + " \n");
		bf.flush();
		
		bf.write("Solicite la conexión escribiendo 'Conexión'..." + "\n");
		
		String envia = "Conexión";
		sendData = envia.getBytes();
		
		DatagramPacket paqueteEnvidado = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		clientSocket.send(paqueteEnvidado);
				
		DatagramPacket paqueteRecibido = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(paqueteRecibido);
		
		String recibido = new String(paqueteRecibido.getData());
		
		if(recibido.trim().equals("OK"))
		{
			bf.write("Recibí confirmación del servidor" + "\n");
		}
		
		byte[] sendData2 = new byte[1024];
		
		String archivos = "ListoAR";
		sendData2 = archivos.getBytes();
		
		DatagramPacket paqueteEnviadoArchivos = new DatagramPacket(sendData2, sendData2.length, IPAddress, 9876);
		clientSocket.send(paqueteEnviadoArchivos);

		bf.write("He enviado notificación de que puedo recibir archivos" + "\n");
		
		byte[] numP = new byte[1024];
		
		DatagramPacket paqueteNum = new DatagramPacket(numP, numP.length);
		
		clientSocket.receive(paqueteNum);
		
		int numPaquetes = Integer.parseInt(new String(paqueteNum.getData()).trim());
		
		int i = 0;
		
		System.out.println(numPaquetes);
		
		byte[] receiveData2 = new byte[8192];
		
		DatagramPacket paqueteArchivos = new DatagramPacket(receiveData2, receiveData2.length);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		byte[] rec = null;
		
		long tiempoInicial = System.nanoTime();
		
		do 
		{	
			clientSocket.setSoTimeout(50);
			try {
			clientSocket.receive(paqueteArchivos);
			}
			catch(Exception e) {
				break;
			}
			rec = paqueteArchivos.getData();
	        
			DatagramPacket paqueteDigest = new DatagramPacket(receiveData, receiveData.length);
	        try {
			clientSocket.receive(paqueteDigest);
	        }
	        catch(Exception e){
	        	break;
	        }
			String res = resultadoDigest(rec);
			
			String comp = new String(paqueteDigest.getData(),"UTF-8");
			
			comp = comp.trim();
			
			if(comp.equals(res)) {
				bf.write("El paquete numero " + i +" llego correctamente" +  "\n");
			}
			else if(comp.length() - res.length() >= 1 && comp.substring(0, res.length()).equals(res))
			{
				bf.write("El paquete numero " + i +" llego correctamente" +  "\n");
			}
			else {
				bf.write("El paquete numero " + i +" no llego correctamente" +  "\n");
			}
			bos.write(rec, 0, rec.length);
			
			
	        bos.flush();
	            
	        i ++;
	        
		}
		while(i < numPaquetes);
		
		System.out.println("Sali");
		
		bf.write("El tiempo de transferencia de archivos fue de : " +(System.nanoTime() -tiempoInicial)/(1000000) +" segundos"+ "\n");
		
		if(i < numPaquetes) {
			bf.write("El numero de paquetes perdidos fue de " + (numPaquetes - i) + "\n");
		}
		else
		{
			bf.write("No se perdieron paquetes, llegaron " + numPaquetes +" de " + numPaquetes + "\n");
		}
		
		bf.close();
		
		clientSocket.close();
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

