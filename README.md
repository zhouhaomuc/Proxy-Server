# Proxy-Server
Upon startup, the proxy server read a configuration file (specified on the command line)
open a TCP socket on a port indicated in the configuration file
make a list of the blocked websites, and start accepting requests 
the proxy server supports the GET method. 
In addition, when the server get a QUIT request, the server should exit immediately. 
To serve each request, first need to parse the request line and headers sent by the client. 


Here is the program skeleton

Main routine {
     Parse the configuration file; 
     Create a server socket listening on the specified port;
     For each incoming client socket connection {
         Spawn a worker thread / fork off a child process / use select to handle the connection;
         Wait for/handle the next connection;
     }
 }

 Handling any connection {
     Read the request line and header fields until two consecutive new lines;
     (Note that a new line can be a single "\n" or a character pair "\r\n".)
     Examine the first line (request line);
     If the request method is not "GET" {
         Return an error HTTP response with the status code "HTTP_BAD_METHOD";
     }
     If the requested site is marked as block in the configuration file {
         Return error 403 (Forbidden)
     }
     Make TCP connection to the "real" Web server;
     Send over an HTTP request;
     Receive the server's response;
     Close the TCP connection to the server;
     Send the server's response back to the client;
     Close the connection socket to the client.
 }
