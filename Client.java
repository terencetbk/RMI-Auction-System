/*
 Client.java
 @author Terence Tan Boon Kiat
 @guid 2228167t
 1. Client provides the user interface for account login and interaction with the system
 2. Users are allowed to create new accounts or login with an existing account,
 then select from options to create and bid on auction items.
 */

import java.rmi.Naming;			//Import the rmi naming - so you can lookup remote object
import java.rmi.RemoteException;	//Import the RemoteException class so you can catch it
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;	//Import the MalformedURLException class so you can catch it
import java.rmi.NotBoundException;	//Import the NotBoundException class so you can catch it
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.InputMismatchException;

public class Client {

    public static void main(String[] args) {

        String reg_host = "localhost";
        int reg_port = 1099;

        if (args.length == 1) {
            reg_port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            reg_host = args[0];
            reg_port = Integer.parseInt(args[1]);
        }

        try {
            AuctionSystemInterface server = (AuctionSystemInterface) Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/AuctionSystemService");
            Account a = new Account();
            AccountInterface account = (AccountInterface) UnicastRemoteObject.exportObject(a, 0);
            displayAccountMenu(server, account);
        } // Catch the exceptions that may occur - rubbish URL, Remote exception, not bound exception
        catch (MalformedURLException murle) {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
        } catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        } catch (NotBoundException nbe) {
            System.out.println();
            System.out.println("NotBoundException");
            System.out.println(nbe);
        }
    }

    public static void displayAccountMenu(AuctionSystemInterface s, AccountInterface a) {
        while (true) {
            int choice = -1;
            System.out.println("//-------------------------//");
            System.out.println("[1] Create a new account");
            System.out.println("[2] Login with an existing account");
            System.out.println("//-------------------------//");
            System.out.println("Choose a number: ");

            Scanner scn = new Scanner(System.in);
            choice = scn.nextInt();
            switch (choice) {
                case 1:
                    createAccount(s, a);
                    break;
                case 2:
                    login(s, a);
                    break;
            }
        }
    }

    public static void createAccount(AuctionSystemInterface s, AccountInterface a) {
        Scanner scan = new Scanner(System.in);
        System.out.println("//-------------------------//");
        System.out.println("Enter your Login ID: ");
        String uid = scan.nextLine();
        System.out.println("Enter your Name: ");
        String n = scan.nextLine();
        System.out.println("Enter your Password:");
        String pw = scan.nextLine();
        try {
            boolean created = s.addUser(uid, n, pw, a);
            if (created) {
                System.out.println("Your account has been created. Please login using option [2].");
            } else {
                System.out.println("Login ID is taken. Please choose another Login ID.");
            }
        } catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        }
    }

    public static void login(AuctionSystemInterface s, AccountInterface a) {
        boolean retry = true;
        while (retry) {
            Scanner scan = new Scanner(System.in);
            System.out.println("//-------------------------//");
            System.out.println("Enter your Login ID: ");
            String uid = scan.nextLine();
            System.out.println("Enter your Password:");
            String pw = scan.nextLine();
            try {
                AccountInterface acc = s.userLogin(uid, pw, a);
                if (acc != null) {
                    a = acc;
                    System.out.println("Login successful. Welcome " + a.getName() + "!");
                    retry = false;
                    if (a.getAdmin()) {
                        displayAdminOptions(s, a);
                    } else {
                        displayOptions(s, a);
                    }
                } else {
                    System.out.println("Invalid Login ID or password. Please try again.");
                }
            } catch (RemoteException re) {
                System.out.println();
                System.out.println("RemoteException");
                System.out.println(re);
            }
        }
    }

    public static void displayAdminOptions(AuctionSystemInterface s, AccountInterface a) {
        while (true) {
            int option = -1;
            System.out.println("//------Admin Options------//");
            System.out.println("[1] Save System State");
            System.out.println("[2] Restore System State");
            System.out.println("[3] Check server status");
            System.out.println("[4] Performance testing");
            System.out.println("[0] Exit Auction System");
            System.out.println("//-------------------------//");
            System.out.print("Choose an option: ");

            Scanner scanner = new Scanner(System.in);
            option = scanner.nextInt();

            System.out.println("//-------------------------//");

            /* User Interface option */
            switch (option) {
                case 1:
                    try {
                        System.out.println(s.saveState());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        System.out.println(s.restoreState());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    checkStatus(s);
                    break;
                case 4:
                    loadTest(s, a);
                    break;
                case 0:
                    System.out.println("Exited from Auction System.");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Choose from 0 to 2.");
            }
        }
    }

    public static void displayOptions(AuctionSystemInterface s, AccountInterface a) {
        while (true) {
            int option = -1;
            System.out.println("//-------------------------//");
            System.out.println("[1] Create an auction");
            System.out.println("[2] View all auction item(s)");
            System.out.println("[3] Search for an item (by AuctionID)");
            System.out.println("[4] Bid for an item");
            System.out.println("[5] Check server status");
            System.out.println("[0] Exit Auction System");
            System.out.println("//-------------------------//");
            System.out.print("Choose an option: ");

            Scanner scanner = new Scanner(System.in);
            option = scanner.nextInt();

            System.out.println("//-------------------------//");

            /* User Interface option */
            switch (option) {
                case 1:
                    createAuction(s, a);
                    break;
                case 2:
                    showAllAuctions(s);
                    break;
                case 3:
                    getAuction(s);
                    break;
                case 4:
                    bid(s, a);
                    break;
                case 5:
                    checkStatus(s);
                    break;
                case 0:
                    System.out.println("Exited from Auction System.");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Choose from 0 to 4.");
            }
        }
    }

    // create an auction
    private static void createAuction(AuctionSystemInterface s, AccountInterface a) {
        try {
            System.out.print("Enter item name: ");
            Scanner scn = new Scanner(System.in);
            String name = scn.nextLine();

            System.out.print("Enter minimum price: ");
            double price = Double.parseDouble(scn.nextLine());

            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            System.out.print("Enter closing date and time (dd/MM/yyyy hh:mm:ss) for the auction item: ");
            Date endTime = date.parse(scn.nextLine());

            System.out.println(s.createAuctionItem(name, price, endTime, a));

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InputMismatchException ime) {
            ime.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    // Show all auction items
    private static void showAllAuctions(AuctionSystemInterface s) {
        try {
            System.out.println(s.listAllItems());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // Search for an auction item by auction id
    private static void getAuction(AuctionSystemInterface s) {
        try {
            System.out.print("Enter AuctionID: ");
            Scanner scn = new Scanner(System.in);
            long aid = scn.nextInt();
            System.out.println(s.getAuctionItem(aid));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InputMismatchException ime) {
            ime.printStackTrace();
        }
    }

    /* Perform auction bidding */
    private static void bid(AuctionSystemInterface s, AccountInterface a) {
        try {
            System.out.print("Enter AuctionID to bid: ");
            Scanner longScan = new Scanner(System.in);
            long aid = longScan.nextLong();

            System.out.print("Enter to bid value: ");
            Scanner lineScan = new Scanner(System.in);
            double value = Double.parseDouble(lineScan.nextLine());

            System.out.println(s.setBid(a, aid, value));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void checkStatus(AuctionSystemInterface s) {
        try {
            System.out.println(s.checkServer());
        } catch (RemoteException e) {
            System.out.println("The server is offline. Please try again later or contact the administrator.");
        }
    }

    public static void loadTest(AuctionSystemInterface s, AccountInterface a) {
        try {
            System.out.println("Enter the desired number of auctions to be created for load testing.");
            Scanner longScan = new Scanner(System.in);
            long number = longScan.nextLong();

            SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            String dateline = "11/11/2111 12:12:12";
            Date endTime = dt.parse(dateline);

            long start = System.currentTimeMillis();
            long end = 0;
            for (int i = 0; i < number; i++) {
                System.out.println(s.createAuctionItem("item" + i, (double) i, endTime, a));
            }
            end = System.currentTimeMillis();
            System.out.println("Time elapsed in Miliseconds: " + (end - start)); //Print the difference in mili seconds
            System.out.println("Time elapsed in Seconds: " + (end - start) / 1000); // Print the difference in seconds
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InputMismatchException | ParseException e) {
            System.out.println("Invalid input");
        }
    }
}
