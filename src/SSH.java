import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.IOException; 

import com.jcraft.jsch.*;

public class SSH {

    public static void main(String[] args) throws IOException {  
        String endLineStr = " # "; // it is dependant to the server  
        String host = "10.221.7."; // host IP  
        String user = "pi"; // username for SSH connection  
        String password = "raspberry"; // password for SSH connection  
        int port = 22; // default SSH port  
  
        
        for (int i = 1; i < 255; i++) {
			
		   String ip = host+i;
        
	        try {
	        	
	        	System.out.println("connecting to "+ip+" ...");
		        JSch shell = new JSch();    
		        Session session = shell.getSession(user, ip, port);  
		        session.setUserInfo(new SSHUserInfo(password));  
		        session.connect(); 
		        System.out.println("----------------------------------");
		        System.out.println("CONNECTED TO "+ip);
		        System.out.println("----------------------------------");
		        break;
			} catch (JSchException e) {
				System.err.println(ip + "  "+e.getMessage());
			}  
        
        }
        
//        Channel channel = session.openChannel("shell");  
//        channel.connect();  
//  
//        DataInputStream dataIn = new DataInputStream(channel.getInputStream());  
//        DataOutputStream dataOut = new DataOutputStream(channel.getOutputStream());  
//  
//        // send ls command to the server  
//        dataOut.writeBytes("ls -la\r\n");  
//        dataOut.flush();  
//  
//        // and print the response   
//        String line = dataIn.readLine();  
//        System.out.println(line);  
//        while(!line.endsWith(endLineStr)) {  
//            System.out.println(line);  
//            line = dataIn.readLine();  
//        }  
//        dataIn.close();  
//        dataOut.close();  
//        channel.disconnect();  
//        session.disconnect();  
        
//		} catch (JSchException e) {
//			System.err.println(host + "  "+e.getMessage());
//		}        
    }  
    
    
    // this class implements jsch UserInfo interface for passing password to the session  
    static class SSHUserInfo implements UserInfo {  
        private String password;  
  
        SSHUserInfo(String password) {  
            this.password = password;  
        }  
  
        public String getPassphrase() {  
            return null;  
        }  
  
        public String getPassword() {  
            return password;  
        }  
  
        public boolean promptPassword(String arg0) {  
            return true;  
        }  
  
        public boolean promptPassphrase(String arg0) {  
            return true;  
        }  
  
        public boolean promptYesNo(String arg0) {  
            return true;  
        }  
  
        public void showMessage(String arg0) {  
            System.out.println(arg0);  
        }  
    }      
    

}
