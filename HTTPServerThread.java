import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author hao
 * deal with every client request
 *
 */
public class HTTPServerThread extends Thread {
	public volatile static boolean exit = false; 
    Socket socket=null;
    static String config=null;
    ServerSocket serverSocket=null;
	/**
	 * @param socket: to accept client.
	 * @param serverSocket: to accept server, will be used when get a "QUIT" to terminate server.
	 * @param config: to get the configure information read in the file, to get information about block web sites.
	 */
	public HTTPServerThread(Socket socket,ServerSocket serverSocket,String config){
		this.socket=socket;
		this.serverSocket=serverSocket;
		this.config=config;
	}

    /* 
     * @see java.lang.Thread#run()
     * start to deal with client request
     */
    public void run() {

    	InputStream instr=null;
		InputStreamReader inread=null;
		BufferedReader buffread=null;
		String info=" ";
		String sumInfo="";
		try {
			while(info.length()!=0){
				instr = socket.getInputStream();
				inread = new InputStreamReader(instr);
				buffread = new BufferedReader(inread);
				info = buffread.readLine();
				sumInfo=sumInfo+info+"\r\n";;
			}				
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.print("Client input:"+sumInfo);		
		if((sumInfo.indexOf("https:")!=-1)){
			String err="SORRY, \"https\" is not allowed!\n";
			OutputStream clientOut;
			try {
				clientOut = socket.getOutputStream();
				clientOut.write(err.getBytes());
				
			} catch (IOException e) {
				e.printStackTrace();
			}     		 
		}
		if((sumInfo.indexOf("ftp:")!=-1)){
			String err="SORRY, \"ftp\" is not allowed!\n";
			OutputStream clientOut;
			try {
				clientOut = socket.getOutputStream();
				clientOut.write(err.getBytes());
				
			} catch (IOException e) {
				e.printStackTrace();
			}     		 
		}
		if((sumInfo.indexOf("file:")!=-1)){
			String err="SORRY, \"file\" is not allowed!\n";
			OutputStream clientOut;
			try {
				clientOut = socket.getOutputStream();
				clientOut.write(err.getBytes());
				
			} catch (IOException e) {
				e.printStackTrace();
			}     		 
		}
		if((sumInfo.indexOf("POST")!=-1)||(sumInfo.indexOf("HEAD")!=-1)||(sumInfo.indexOf("PUT")!=-1)||(sumInfo.indexOf("DELETE")!=-1)||(sumInfo.indexOf("TRACE")!=-1)||(sumInfo.indexOf("CONNECT")!=-1)){
			String err="Sorry, only GET method is allowed!\n";
			OutputStream clientOut;
			try {
				clientOut = socket.getOutputStream();
				clientOut.write(err.getBytes());
				
			} catch (IOException e) {
				e.printStackTrace();
			}     	
		}
		if((sumInfo.indexOf("GET")!=-1)){
			get(sumInfo,socket);
		}	
		if(sumInfo.indexOf("QUIT")!=-1){
			System.out.println("****SERVER IS TERMINATED BY CLIENT!****");
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
		}
    }	
    /**
     * Deal with GET request.
     * @param sumInfo: GET information got from client, will be used to get URL and port and request.
     * @param socket: client socket
     */
    public static void get(String sumInfo,Socket socket){
         //hName:hostname get in the sumInfo
    	 //pString: port string get in the sumInfo
    	 String hName = null, pString = null;
         Socket server = null;
         int port = 80;
         try {
                 if (sumInfo.indexOf("http://") != -1 && sumInfo.indexOf("HTTP/") != -1) {
                     hName = sumInfo.substring(sumInfo.indexOf("http://")+7, sumInfo.indexOf("HTTP/"));
                     hName = hName.substring(0, hName.indexOf("/"));
                     //if there is port number in the information line, get the port by ":"
                     if (hName.indexOf(":") != -1) {
                    	 pString = hName;
                         hName = hName.substring(0, hName.indexOf(":"));
                         pString = pString.substring(pString.indexOf(":") + 1);
                         port = Integer.parseInt(pString);
                         sumInfo=sumInfo.replace(":"+pString, "");
                     }
                 }
                //if hostname is got, then connect to this web site
                 if (hName != null) {
                	 if(config.indexOf(hName)==-1){
                         server = new Socket(hName, port);
                             try {
                            	 sumInfo=sumInfo.replace(hName,"");
                            	 sumInfo=sumInfo.replace("http://", "");
                            	 sumInfo=sumInfo.replace("HTTP/1.1","HTTP/1.1"+"\n"+"Host: "+hName );
                            	 //write request to server with outputstream, and get response.
                                 OutputStream out = server.getOutputStream();
                                 OutputStream clientOut = socket.getOutputStream();
                                 BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream())); 
                                 out.write(sumInfo.getBytes());  
                                //write response to client
                                 int d = -1 ;
                                 while(true){       
                                	 d=in.read();                                     
                                     clientOut.write(d);
                                     if(d==-1){
                                    	 break;
                                     }
                                 }
                                 out.close();
                                 clientOut.close();
                                 in.close();
                                 
                             } catch (UnknownHostException e) {
                                 e.printStackTrace();
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }	                             
                            
                	 }else{
                		 String err="403! Forbidden\n";
                		 OutputStream clientOut = socket.getOutputStream();
                		 clientOut.write(err.getBytes());
 
                	 }
                 }                   
         } catch (IOException e) {
         }
         
}

}

