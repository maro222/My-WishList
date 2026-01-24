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
            while (true) {
                Request request = (Request) in.readObject();
                Response response = null;

                switch (request.getAction()) {

                    case "REGISTER":
                        User newUser = (User) request.getData();
                        boolean reg = userDAO.register(newUser);
                        response = new Response(reg, reg ? "Registration Successful" : "Registration Failed (Email/Username might exist)", null);
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

                    case "ADD_TO_WISHLIST":
                        int[] addData = (int[]) request.getData();
                        boolean added = wishlistDAO.addToWishlist(addData[0], addData[1]);
                        response = new Response(added, added ? "Item added to wishlist" : "Failed to add", null);
                        break;
                        
                    // new case
                    case "GET_USER_WISHLIST":
                        User user = (User) request.getData();
                        List<WishlistItem> list = wishlistDAO.getUserWishlist(user.getId());
                        response = new Response(true, "Wishlist Items Fetched", list);
                        break;

                    case "GET_FRIEND_WISHLIST":
                        int friendId = (int) request.getData();
                        List<WishlistItem> wishes = wishlistDAO.getFriendWishlist(friendId);
                        response = new Response(true, "Friend wishlist fetched", wishes);
                        break;

                    case "CONTRIBUTE":
                        double[] contributionData = (double[]) request.getData();
                        boolean contributed = wishlistDAO.contribute(
                                (int) contributionData[0], // wishlistId
                                (int) contributionData[1], // userId
                                contributionData[2]        // amount
                        );
                        response = new Response(contributed, contributed ? "Contribution successful" : "Contribution failed", null);
                        break;
                        
                        
                    case "GET_FRIENDS":
                            int userId = (int) request.getData();
                            List<User> friends = friendDAO.getFriends(userId);
                            response = new Response(true, "Friends fetched", friends);
                            break;
                            
                            
                    case "REMOVE_FRIEND":
                                    int[] unfriendData = (int[]) request.getData();
                                    boolean removed = friendDAO.removeFriend(unfriendData[0], unfriendData[1]);
                                    response = new Response(removed, removed ? "Friend removed" : "Failed to remove friend", null);
                                    break;
                  
                  

                         
                }

                out.writeObject(response);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Client disconnected");
        } finally {
            try { if (in != null) in.close();
                  if (out != null) out.close();
                  if (socket != null) socket.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
