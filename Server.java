/*
 Server.java
 @author Terence Tan Boon Kiat
 @guid 2228167t
 Server code for hosting the AuctionSystem
 */

import java.rmi.Naming;	// import naming classes to bind to rmiregistry
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    public Server(int port) {
        try {
            AuctionSystem si = new AuctionSystem();
            AuctionSystemInterface s = (AuctionSystemInterface) UnicastRemoteObject.exportObject(si, 0);
            Naming.rebind("rmi://localhost:" + port + "/AuctionSystemService", s);
            System.out.println("Server is starting...");
        } catch (MalformedURLException murle) {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
        } catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        }
    }

    public static void main(String args[]) {

        // create new server for auction system
        int p = 1099; // default port number

        if (args.length == 1) {
            p = Integer.parseInt(args[0]); // custom port number
        }
        new Server(p);
    }
}
