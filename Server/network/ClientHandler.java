package com.mycompany.network;

import com.mycompany.dao.FriendDAO;
import com.mycompany.dao.ItemDAO;
import com.mycompany.dao.NotificationDAO;
import com.mycompany.dao.UserDAO;
import com.mycompany.dao.WishlistDAO;
import com.mycompany.model.Contribution;
import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.User;
import com.mycompany.model.Item;
import com.mycompany.model.Notification;
import com.mycompany.model.WishlistItem;
import java.io.EOFException;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private UserDAO userDAO = new UserDAO();
    private ItemDAO itemDAO = new ItemDAO();
    private WishlistDAO wishlistDAO = new WishlistDAO();
    private FriendDAO friendDAO = new FriendDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Request request = (Request) in.readObject();
                Response response = null;

                switch (request.getAction()) {

                    case "REGISTER":
                        User newUser = (User) request.getData();
                        boolean reg = userDAO.register(newUser);
                        System.out.println(reg);
                        response = new Response(reg, reg ? "Registration Successful" : "Registration Failed (Email/Username might exist)", newUser);
                        break;

                    case "LOGIN":
                        User loginData = (User) request.getData();
                        User loggedUser = userDAO.login(loginData.getUsername(), loginData.getPassword());
                        response = new Response(loggedUser != null, loggedUser != null ? "Login Successful" : "Invalid Username/Password", loggedUser);
                        break;

                    case "GET_ITEMS":
                        List<Item> items = itemDAO.getAllItems();
                        response = new Response(true, "Items Fetched", items);
                        break;
                        
                    // ... (inside the switch statement)
                    case "INSERT_NEW_WISH":
                        String[] itemData = (String[]) request.getData();
                        String name = itemData[0];
                        double price = Double.parseDouble(itemData[1]);
                        int userId = Integer.parseInt(itemData[2]);

                        boolean success = false;
                        String message = "";

                        Item existingItem = itemDAO.findItemByName(name);

                        if (existingItem != null) {
                            // Item exists. Check if it's already in THIS user's wishlist.
                            if (wishlistDAO.isItemInWishlist(userId, existingItem.getId())) {
                                message = "'" + name + "' is already in your wishlist.";
                            } else {
                                // Item exists but not in their list, so add it.
                                success = wishlistDAO.addToWishlist(userId, existingItem.getId());
                                message = success ? "Added existing item to your wishlist." : "Failed to add to wishlist.";
                            }
                        } else {
                            // Item does NOT exist. Create it, then add to wishlist.
                            Item newItem = new Item(0, name, price);
                            int newItemId = itemDAO.addItem(newItem);
                            if (newItemId != -1) {
                                success = wishlistDAO.addToWishlist(userId, newItemId);
                                message = success ? "New wish created and added successfully!" : "Failed to add new wish.";
                            } else {
                                message = "Failed to create the new item.";
                            }
                        }

                        response = new Response(success, message, null);
                        break;
                

                    case "ADD_TO_WISHLIST":
                        int[] addData = (int[]) request.getData();
                        boolean added = wishlistDAO.addToWishlist(addData[0], addData[1]);
                        response = new Response(added, added ? "Item added to wishlist" : "Failed to add", null);
                        break;
                        
                    case "GET_USER_WISHLIST":
                        User user = (User) request.getData();
                        List<WishlistItem> list = wishlistDAO.getUserWishlist(user.getId());
                        response = new Response(true, "Wishlist Items Fetched", list);
                        break;
                        
                    case "DELETE_WISH":
                        int wishlistIdToDelete = (int) request.getData();
                        boolean deleted = wishlistDAO.deleteWishItem(wishlistIdToDelete);
                        response = new Response(deleted, deleted ? "Item deleted successfully" : "Failed to delete item", null);
                        break;

                    case "DECLINE_FRIEND":
                        int[] declineIds = (int[]) request.getData();
                        boolean declined = friendDAO.declineFriendRequest(declineIds[0], declineIds[1]);
                        response = new Response(declined, declined ? "Friend request declined" : "Failed to decline", null);
                        break;
                        
                    case "ACCEPT_FRIEND":
                        int[] acceptIds = (int[]) request.getData();
                        boolean accepted = friendDAO.acceptFriendRequest(acceptIds[0], acceptIds[1]);
                        response = new Response(accepted, accepted ? "Friend request Accepted" : "Failed to decline", null);
                        break;
                    
                      
                    case "GET_FRIEND_WISHLIST":
                        int friendId = (int) request.getData();
                        List<WishlistItem> wishes = wishlistDAO.getFriendWishlist(friendId);
                        response = new Response(true, "Friend wishlist fetched", wishes);
                        break;
                        
                    case "GET_REQUEST_LIST":
                        User mine = (User) request.getData();
                        List<User> friends = friendDAO.getPendingRequests(mine.getId());
                        response = new Response(true, "friends returned", friends);
                        break;

                    case "CONTRIBUTE":
                        Contribution contribution = (Contribution) request.getData();
                        
                        int wishId = contribution.getWishId();
                        int payerId = contribution.getPayerId();
                        double amount = contribution.getAmount();

                        boolean contributeSuccess = false;
                        String contributeMsg = "";

                        WishlistItem targetItem = wishlistDAO.getWishItemById(wishId);

                        if (targetItem != null) {
                            double remaining = targetItem.getTotalPrice() - targetItem.getCollectedAmount();

                            if (amount > (remaining + 0.01)) {
                                contributeSuccess = false;
                                contributeMsg = "Amount exceeds remaining price ($" + String.format("%.2f", remaining) + ")";
                            } else {
                                contributeSuccess = wishlistDAO.contribute(contribution);
                                
                                if (contributeSuccess) {
                                    contributeMsg = "Contribution successful";
                                    

                                    User payer = userDAO.getUserById(payerId);
                                    String payerName = (payer != null) ? payer.getUsername() : "A friend";
                                    
                                    // 1. Notify the Receiver (Wish Owner)
                                    String ownerMsg = payerName + " contributed $" + amount + " to your wish '" + targetItem.getName() + "'";
                                    notificationDAO.addNotification(targetItem.getUserId(), ownerMsg);

                                    // 2. Notify the Sender (Contributor) -- NEW CODE
                                    String senderMsg = "You successfully contributed $" + amount + " to " + targetItem.getName();
                                    notificationDAO.addNotification(payerId, senderMsg);
                                    
                                        double newTotal = targetItem.getCollectedAmount() + amount;
                                        double Itemprice = targetItem.getTotalPrice();

                                        if (Math.abs(Itemprice - newTotal) < 0.01) {

                                            String itemName = targetItem.getName();

                                            // A. Notify the Wish Owner
                                            String successOwnerMsg = "CONGRATULATIONS! Your wish '" + itemName + "' is fully completed!";
                                            notificationDAO.addNotification(targetItem.getUserId(), successOwnerMsg);

                                            // B. Notify All Contributors
                                            List<Integer> allContributors = wishlistDAO.getDistinctContributors(wishId);

                                            for (int user_id : allContributors) {
                                                String successContributorMsg = "Great news! The wish '" + itemName + "' you supported is now fully funded!";
                                                notificationDAO.addNotification(user_id, successContributorMsg);
                                            }
                                        }
                                } else {
                                    contributeMsg = "Database error";
                                }
                            }
                        } else {
                            contributeMsg = "Wish item not found";
                        }
                        
                        response = new Response(contributeSuccess, contributeMsg, null);
                        break;
                        
                    case "GET_FRIENDS":
                        System.out.println("Processing GET_FRIENDS request");
                        int userid = (int) request.getData();
                        List<User> Myfriends = friendDAO.getFriends(userid);
                        response = new Response(true, "Friends fetched", Myfriends);
                        break;
                    
                    case "REMOVE_FRIEND":
                        System.out.println("Processing REMOVE_FRIEND request");
                        int[] unfriendData = (int[]) request.getData();
                        boolean removed = friendDAO.removeFriend(unfriendData[0], unfriendData[1]);
                        response = new Response(removed, removed ? "Friend removed" : "Failed to remove friend", null);
                        break;
                    
                    case "SEARCH_USER":
                        System.out.println("Processing SEARCH_USER request");
                        String searchQuery = (String) request.getData();
                        List<User> searchResults = userDAO.searchUsers(searchQuery);
                        response = new Response(true, "Search completed", searchResults);
                        break;

                    case "ADD_FRIEND":
                        System.out.println("Processing ADD_FRIEND request");
                        int[] friendRequestData = (int[]) request.getData();
                        boolean sent = friendDAO.sendFriendRequest(friendRequestData[0], friendRequestData[1]);
                        response = new Response(sent, sent ? "Friend request sent" : "Request already exists or you are already friends", null);
                        break;
                        
                    case "GET_NOTIFICATIONS":
                        int notifUserId = (int) request.getData();
                        List<Notification> notifs = notificationDAO.getUserNotifications(notifUserId);
                        notificationDAO.markNotificationsAsRead(notifUserId);
                        response = new Response(true, "Notifications fetched", notifs);
                        break;
                        
                    case "CHECK_UNREAD":
                        int uidToCheck = (int) request.getData();
                        boolean hasUnread = notificationDAO.hasUnreadNotifications(uidToCheck);
                        // We send the boolean back as the data
                        response = new Response(true, "Check complete", hasUnread);
                        break;
                        
                    case "CLEAR_ALL_NOTIFICATIONS":
                        int userIdToClear = (int) request.getData();
                        boolean cleared = notificationDAO.clearAllNotifications(userIdToClear);
                        response = new Response(cleared, cleared ? "Notifications cleared" : "Failed to clear", null);
                        break;
                        
                    default:
                        System.out.println("Unknown action: " + request.getAction());
                        response = new Response(false, "Unknown action", null);
                        break;
                }

                System.out.println("Sending response back to client...");
                out.writeObject(response);
                out.flush();
                System.out.println("Response sent successfully");
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected (EOF) - this is normal when client closes");
        } catch (Exception e) {
            System.out.println("Error in ClientHandler:");
            e.printStackTrace();
        } finally {
            try { if (in != null) in.close();
                  if (out != null) out.close();
                  if (socket != null) socket.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    public void closeEverything() {
        try {
            // Closing the socket will cause the run() loop to throw an IOException
            // and terminate the thread.
            if (socket != null) {
                socket.close();
            }
            // Close streams if necessary (though closing socket usually closes streams)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
