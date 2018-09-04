package com.example.dobitnarae;

import java.util.ArrayList;

public class Order{
    private ArrayList<BasketItem> basket;
    private int orderNo;
    private String userID;
    private String adminID;
    private int acceptStatus;
    private String orderDate;

    public Order(int orderNo, String userID, String adminID, int acceptStatus, String orderDate) {
        this.orderNo = orderNo;
        this.userID = userID;
        this.adminID = adminID;
        this.acceptStatus = acceptStatus;
        this.orderDate = orderDate;
    }
    public ArrayList<BasketItem> getBasket() {
        return basket;
    }

    public void setBasket(ArrayList<BasketItem> basket) {
        this.basket = basket;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public int getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(int acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}