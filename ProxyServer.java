import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;
public class ProxyServer{
	/**
	 * main method to start the program
	 * @param args as file path 
	 */
	public static void main(String[] args) {
		int count=1;
		try {
			if(args.length!=1){
				System.out.println("Please enter one argument as FILE!");
			}
			String port=null;
			String line = null;
            String config="";
			try {   
		            File file = new File(args[0]); 
		            //read the file line by line, when "#" got, ignore the line and continue
		            //get the port in the file,
		            if (file.isFile() && file.exists()) {   
		                InputStreamReader read = new InputStreamReader(new FileInputStream(file));   
		                BufferedReader bufferedReader = new BufferedReader(read);   
		                
		                while ((line = bufferedReader.readLine()) != null) {   
		                	if(line.indexOf("#")==-1){
		                		config=config+line;
		                	}		                   
		                }
        			port=config.substring((config.indexOf("port")+5),config.indexOf("block"));
        			read.close();   
		            }else{   
		                System.out.println("Cannot find the file!");   
		            }   
		        } catch (Exception e) {   
		            System.out.println("Cannot read the file!");   
		            e.printStackTrace();   
		        }
			
		    int portNum=Integer.parseInt(port);  
			//initialize a server socket, bind port 
			ServerSocket serverSocket=new ServerSocket(portNum);
			Socket socket=null;
			System.out.println("***server starts!***");
			
			//accept client continuosly
			while(true){
				socket=serverSocket.accept();
				HTTPServerThread serverThread=new HTTPServerThread(socket,serverSocket,config);
				System.out.println("client number: "+count);
				count++;
				serverThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
