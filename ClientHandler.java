package com.mycompany.network;

import com.mycompany.dao.FriendDAO;
import com.mycompany.dao.ItemDAO;
import com.mycompany.dao.UserDAO;
import com.mycompany.dao.WishlistDAO;
import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.User;
import com.mycompany.model.Item;
import com.mycompany.model.WishlistItem;

import java.io.EOFException;
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
    private FriendDAO friendDAO = new FriendDAO();
    private WishlistDAO wishlistDAO = new WishlistDAO();

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
            System.out.println("Client handler started, waiting for requests...");
            
            while (true) {
                System.out.println("Waiting to read request object...");
                Request request = (Request) in.readObject();
                System.out.println("Received request: " + request.getAction());
                
                Response response = null;

                switch (request.getAction()) {

                    case "REGISTER":
                        System.out.println("Processing REGISTER request");
                        User newUser = (User) request.getData();
                        boolean reg = userDAO.register(newUser);
                        response = new Response(reg, reg ? "Registration Successful" : "Registration Failed (Email/Username might exist)", null);
                        break;

                    case "LOGIN":
                        System.out.println("Processing LOGIN request");
                        User loginData = (User) request.getData();
                        User loggedUser = userDAO.login(loginData.getUsername(), loginData.getPassword());
                        response = new Response(loggedUser != null, loggedUser != null ? "Login Successful" : "Invalid Username/Password", loggedUser);
                        break;

                    case "GET_ITEMS":
                        System.out.println("Processing GET_ITEMS request");
                        List<Item> items = itemDAO.getAllItems();
                        response = new Response(true, "Items Fetched", items);
                        break;

                    case "ADD_TO_WISHLIST":
                        System.out.println("Processing ADD_TO_WISHLIST request");
                        int[] addData = (int[]) request.getData();
                        boolean added = wishlistDAO.addToWishlist(addData[0], addData[1]);
                        response = new Response(added, added ? "Item added to wishlist" : "Failed to add", null);
                        break;
                        
                    case "GET_USER_WISHLIST":
                        System.out.println("Processing GET_USER_WISHLIST request");
                        User user = (User) request.getData();
                        List<WishlistItem> list = wishlistDAO.getUserWishlist(user.getId());
                        response = new Response(true, "Wishlist Items Fetched", list);
                        break;

                    case "GET_FRIEND_WISHLIST":
                        System.out.println("Processing GET_FRIEND_WISHLIST request");
                        int friendId = (int) request.getData();
                        List<WishlistItem> wishes = wishlistDAO.getFriendWishlist(friendId);
                        response = new Response(true, "Friend wishlist fetched", wishes);
                        break;

                    case "CONTRIBUTE":
                        System.out.println("Processing CONTRIBUTE request");
                        double[] contributionData = (double[]) request.getData();
                        boolean contributed = wishlistDAO.contribute(
                                (int) contributionData[0],
                                (int) contributionData[1],
                                contributionData[2]
                        );
                        response = new Response(contributed, contributed ? "Contribution successful" : "Contribution failed", null);
                        break;
                        
                    case "GET_FRIENDS":
                        System.out.println("Processing GET_FRIENDS request");
                        int userId = (int) request.getData();
                        List<User> friends = friendDAO.getFriends(userId);
                        response = new Response(true, "Friends fetched", friends);
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
            try { 
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
                System.out.println("Client connection closed");
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        }
    }
}