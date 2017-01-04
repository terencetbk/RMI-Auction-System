/*
 AuctionSystemInterface.java
 @author Terence Tan Boon Kiat
 @guid 2228167t
 AuctionSystemInterface provides method prototypes for each method required in AuctionSystemImpl
 */

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.Date;

public interface AuctionSystemInterface extends Remote {

    // Add a new user account to server
    public boolean addUser(String userID, String name, String password, AccountInterface acc) throws RemoteException;

    // Login to server
    public AccountInterface userLogin(String userID, String password, AccountInterface acc) throws RemoteException;

    // Create an auction item
    public String createAuctionItem(String name, double minValue, Date endTime, AccountInterface a) throws RemoteException;

    // Get an auction item
    public String getAuctionItem(long id) throws RemoteException;

    // List all auction items
    public String listAllItems() throws RemoteException;

    // Bid for an auction item
    public String setBid(AccountInterface bidder, long auctionID, double bidValue) throws RemoteException;

    // Save state (for server admin only)
    public String saveState() throws RemoteException;

    // Restore to previous state (for server admin only)
    public String restoreState() throws RemoteException;
    
    // Check server status and return number of users currently online
    public String checkServer() throws RemoteException;
}
