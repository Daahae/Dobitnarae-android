package com.example.dobitnarae;

import java.util.ArrayList;

public class Order extends Reserve{
    private ArrayList<BasketItem> basket;

    public Order(int id, String user_id, String admin_id, int acceptStatus, String rentalDate) {
        super(id, user_id, admin_id, acceptStatus, rentalDate);
    }

    public ArrayList<BasketItem> getBasket() {
        return basket;
    }

    public void setBasket(ArrayList<BasketItem> basket) {
        this.basket = basket;
    }
}