/*
 AccountInterface.java
 @author Terence Tan Boon Kiat
 @guid 2228167t
 AccountInterface provides method prototypes for Account
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AccountInterface extends Remote {

    // Get Login ID
    public String getLoginID() throws RemoteException;

    // Get name
    public String getName() throws RemoteException;

    // Get Password
    public String getPassword() throws RemoteException;

    // Get Admin
    public boolean getAdmin() throws RemoteException;

    // Set Login ID
    public void setLoginID(String login) throws RemoteException;

    // Set Name
    public void setName(String name) throws RemoteException;

    // Set Password
    public void setPassword(String pw) throws RemoteException;

    // Set Admin
    public void setAdmin(boolean a) throws RemoteException;

    // Receive Message
    public void receiveMessage(String message) throws RemoteException;
}
