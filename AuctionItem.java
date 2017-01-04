/*
 AuctionItem.java
 @author Terence Tan Boon Kiat
 @guid 2228167t
 AuctionItem provides a class for creating auction items in the system
 */

import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionItem implements Serializable {

    private long auctionID;
    private String itemName;
    private double itemMinValue;
    private Date endTime;
    private String status;
    private AccountInterface owner;
    private AccountInterface lastBidder;
    private String lastBidderName;
    private HashMap<String, AccountInterface> biddersMap;

    // Initialise new auction item
    public AuctionItem(long auction, String name, double minValue, Date end, AccountInterface a) {
        auctionID = auction;
        itemName = name;
        itemMinValue = minValue;
        endTime = end;
        owner = a;
        status = "open";
        biddersMap = new HashMap<>();
        setTimer();
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized double getMinBidValue() {
        return itemMinValue;
    }

    public synchronized Date getEndTime() {
        return endTime;
    }

    // Method for a timer to close auction after end time and check after restore state
    public synchronized void setTimer() {
        if (endTime.before(new Date())) {
            status = "closed";
        } else {
            (new Timer(true)).schedule(new TimerTask() {
                public void run() {
                    closeAuction();
                }
            }, endTime);
        }
    }
    /* auction closed method */

    private synchronized void closeAuction() {
        status = "closed";

        try {
            if (lastBidder == null) {
                owner.receiveMessage("\nYour auction item: " + auctionID + ", " + itemName + " closed with no bidders.\n You may continue to enter an option from the menu.\n");
            } else {
                owner.receiveMessage("\nYour auction item: " + auctionID + ", " + itemName + " closed with winner: " + lastBidderName + " at a price of: $" + itemMinValue + ".\n You may continue to enter an option from the menu.\n");
                for (Entry<String, AccountInterface> entry : biddersMap.entrySet()) {
                    AccountInterface bidder = entry.getValue();
                    if (bidder == lastBidder) {
                        lastBidder.receiveMessage("\nYou have won the bid for auction item: " + itemName + " at the price of: " + itemMinValue + ".\n You may continue to enter an option from the menu.\n");
                    } else {
                        bidder.receiveMessage("\nThe auction item: " + itemName + " you had previously bidded on was won at the price of: " + itemMinValue + " by " + lastBidderName + ".\n You may continue to enter an option from the menu.\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Set method */
    public synchronized void setAuctionItemBid(AccountInterface bidder, double bidValue) {
        try {
            // add bidder to biddersMap
            biddersMap.put(bidder.getLoginID(), bidder);
            lastBidder = bidder;
            lastBidderName = bidder.getName();
            itemMinValue = bidValue;

            for (Entry<String, AccountInterface> entry : biddersMap.entrySet()) {
                AccountInterface b = entry.getValue();
                if (b == lastBidder) {
                    lastBidder.receiveMessage("You have successfully placed a bid for auction item: " + itemName + " at the price of: " + itemMinValue + ".\n");
                } else {
                    b.receiveMessage("Your bid for auction item: " + itemName + " was outbidded at the price of: " + itemMinValue + " by " + lastBidderName + ".\n");
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* Get method */
    public synchronized String getItemDetails() {

        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        String s = "\n//-------------------------//";
        s += "\nID: " + auctionID;
        s += "\nName: " + itemName;
        s += "\nMinimum bid value: " + itemMinValue;
        s += "\nNo. of bidders: " + biddersMap.size();
        s += "\nClosing time: " + date.format(endTime);
        s += "\nStatus: " + status;

        if (status == "open") {
            if (lastBidder == null) {
                s += "\nLast bidder: No bidders yet.";
            } else {
                s += "\nLast bidder: " + lastBidderName;
            }
        } else if (status == "closed") {
            if (lastBidder == null) {
                s += "\nLast bidder: Closed with no bidders.";
            } else {
                s += "\nLast bidder: Won by " + lastBidderName + " at the price of " + itemMinValue;
            }
        }

        s += "\n//-------------------------//\n";
        return s;
    }
}
