/*
 AuctionSystem.java
 @author Terence Tan Boon Kiat
 @guid 2228167t
 AuctionSystem implements the methods for the AuctionSystemInterface
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AuctionSystem implements AuctionSystemInterface {

    private static final String AUCTION_FILE = "Auctions.bin";
    private static final String USER_FILE = "Users.bin";
    private ConcurrentHashMap<Long, AuctionItem> auctionMap; // to store all auctions by auctionID
    private ConcurrentHashMap<String, AccountInterface> userMap; // to store all users currently registered in auction system

    protected AuctionSystem() throws RemoteException {
        auctionMap = new ConcurrentHashMap<>();
        userMap = new ConcurrentHashMap<>();
        Account a = new Account();
        AccountInterface admin = (AccountInterface) UnicastRemoteObject.exportObject(a, 0);
        admin.setLoginID("admin");
        admin.setName("administrator");
        admin.setPassword("admin");
        admin.setAdmin(true);
        userMap.put("admin", admin);
    }

    // Add a new User to server
    public boolean addUser(String userID, String name, String password, AccountInterface acc) throws RemoteException {
        boolean created = false;
        if (!userMap.containsKey(userID)) {
            acc.setLoginID(userID);
            acc.setName(name);
            acc.setPassword(password);
            userMap.put(userID, acc);
            created = true;
        } else {
            created = false;
        }

        return created;
    }

    // Login to server
    public AccountInterface userLogin(String userID, String password, AccountInterface acc) throws RemoteException {
        if (userMap.containsKey(userID) && userMap.get(userID).getPassword().equals(password)) {
            acc = userMap.get(userID);
        }
        else{
            return null;
        }
        return acc;
    }

    // Create an auction item, return id if created else if unsuccessful then return 0
    public String createAuctionItem(String name, double minValue, Date endTime, AccountInterface a) throws RemoteException {
        String result = "";
        long id = 0;
        if (endTime.before(new Date())) {
            result = "Please enter a valid end time (Any datetime after today/now).";
        } else if (auctionMap.isEmpty()) {
            id = 1;
            AuctionItem ai = new AuctionItem(id, name, minValue, endTime, a);
            auctionMap.put(id, ai);
            result = "Created auction successfully with auction ID: " + id;
        } else {
            id = auctionMap.size() + 1;
            AuctionItem ai = new AuctionItem(id, name, minValue, endTime, a);
            auctionMap.put(id, ai);
            result = "Created auction successfully with auction ID: " + id;
        }
        return result;
    }

    // List all auction items
    public String listAllItems() throws RemoteException {
        String result = "";
        if (auctionMap.isEmpty()) {
            result = "There are currently no auction items in the system.";
        } else {
            for (Entry<Long, AuctionItem> entry : auctionMap.entrySet()) {
                result = result + entry.getValue().getItemDetails();
            }
        }
        return result;
    }

    // Get an auction item
    public String getAuctionItem(long id) throws RemoteException {
        String result = "";
        if (auctionMap.isEmpty()) {
            result = "There are currently no auction items in the system.";
        } else if (auctionMap.get(id) == null) {
            result = "Could not find item with ID: " + id;
        } else {
            result = auctionMap.get(id).getItemDetails() + "\n";
        }
        return result;
    }

    // Bid for an auction item
    public String setBid(AccountInterface bidder, long auctionID, double bidValue) throws RemoteException {
        String result = "";
        AuctionItem ai = auctionMap.get(auctionID);
        if (ai == null) {
            result = "Please enter a valid Auction ID.";
        } else if (bidValue <= ai.getMinBidValue()) {
            result = "Please place a bid of more than: $" + ai.getMinBidValue();
        } else if (ai.getStatus() == "closed") {
            result = "The auction period for this item is over. It is now closed. Please bid for another item.";
        } else {
            ai.setAuctionItemBid(bidder, bidValue);
            result = "The bid has been placed successfully for auction ID: " + auctionID;
        }
        return result;
    }

    // Save state (for server admin only)
    public String saveState() throws RemoteException {
        String result = "";
        try {
            ObjectOutputStream o = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(AUCTION_FILE)));
            o.writeObject(auctionMap);
            o.close();
            ObjectOutputStream o2 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(USER_FILE)));
            o2.writeObject(userMap);
            o2.close();
            result = "Auction state saved in: " + AUCTION_FILE + "\nUsers state saved in: " + USER_FILE;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Restore to previous state (for server admin only)
    @SuppressWarnings("unchecked")
    public String restoreState() throws RemoteException {
        String result = "";
        try {
            ObjectInputStream i = new ObjectInputStream(new BufferedInputStream(new FileInputStream(AUCTION_FILE)));
            auctionMap = (ConcurrentHashMap<Long, AuctionItem>) i.readObject();
            ObjectInputStream i2 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(USER_FILE)));
            userMap = (ConcurrentHashMap<String, AccountInterface>) i2.readObject();

            for (AuctionItem ai : auctionMap.values()) {
                ai.setTimer();
            }

            result = "Auction state restored from: " + AUCTION_FILE + "\nUsers state restored from: " + USER_FILE;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Check server status and return number of users currently online
    public String checkServer() throws RemoteException{
        return "Yes, the server is active with " + (userMap.size()-1) + " users online.\n";
    }
}
