# JavaSocket
6Th sem Java CN project. This project is based on file transfer from one computer to another with socket prgramming
## About
Socket Programming is way to connect different client/host from a server and handle the request.

## Installation And running
1. Install Latest JDK (JAVA DEVELOPMENT KIT) [Download here](https://www.oracle.com/in/java/technologies/javase-downloads.html)
2. If You are running code from two different computer then make sure both should connected with same Router/wifi address.
3. Now You need any source file( can be img,video,html etc) that you want to transfer from server to client.
4. Put Source file in direct path with FServer.java file in computer from where you want to send file.
5. Now run FServer file using below command using any port no (for eg 5000)
```
javac FServer.java
java FServer PORT_NO
```
6. Now Server Is Ready to respond to client.
7. Now You need to Run Client using below command
- if Client (which need a file ) and server(which will send file) are in same computer/host than use below command for Running client. (That means if you want to run client 
from same pc where server is running).
```
javac FClient2.java
java FClient2 localhost PORT_NO 
```
here PORT_NO denote same PORT_NO that was given while running Server. After command You will ask for File name to Send ("Send File Request"). It mean client also need to send
file name to server to get that specific file from server , Make sure server should have file that client is requesting. Given below input format that client need to write where
client only need to pass filename.
```
Send File Request 
REQUEST fileName clrf
```
8. After Client given filename , File will be transfer from server to user code directory. And then Client can preview it.

## Limitation
- Currently, Code is not multithreaded so, at a time only one client request for file.
