package com.example.dobitnarae;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public  class JSONTask extends AsyncTask<String, String, String> {
    String user_id;
    String admin_id;
    int reserve_ID = 0;
    int acceptStatus = 0;
    String password = "0";
    Store upStore = new Store(0,"example","example","example",
            "example","example","example",0,
            0.0,0.0,"09:00", "22:00");
    Clothes inCloth = new Clothes(0,0,0,"example","example",0,0,0);
    BasketItem basketItem = new BasketItem(inCloth, 0);
    Order order = new Order(0,"example","example",0,"example");
    Account account = new Account("example","example","example","example",0);
    Reserve reserve = new Reserve(0, "example", "example", 0, "example");
    ArrayList<Store> storeList;
    ArrayList<BasketItem> basketList;
    int flag = 0;
    private JSONTask jsonTaskTmp;

    private JSONTask(){
    }

    private static class Singleton{
        private static final JSONTask instance = new JSONTask();
    }

    public static JSONTask getInstance(){
        return Singleton.instance;
    }
    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setReserve_ID(int reserve_ID) {
        this.reserve_ID = reserve_ID;
    }
    public void setAcceptStatus(int acceptStatus){// 삭제를 위한 셋함수(매개변수 옷이름)
        this.acceptStatus = acceptStatus;
    }
    public void setUpStore(Store upStore){
        this.upStore = upStore;
        flag = 1;
    }
    public void setCloth(Clothes inCloth){// cloth의 update와 insert를 위한 매개변수 전달
        this.inCloth = inCloth;
        flag = 2;
    }
    public void setOrderBasket(Order order,ArrayList<BasketItem> basketList){
        this.order = order;
        this.basketList = basketList;
        flag = 3;
    }
    public void setAccount(Account account){// accountd의 update와 insert를 위한 매개변수 전달
        this.account = account;
        flag = 4;
    }
    public void setBasketItem(BasketItem basketItem){
        this.basketItem = basketItem;
        flag = 5;
    }
    public void setReserveBasket(Reserve reserve, ArrayList<BasketItem> basketList){
        this.reserve = reserve;
        this.basketList = basketList;
        flag = 6;
    }


    @Override
    protected String doInBackground(String... urls) {

        StringBuilder jsonHtml = new StringBuilder();
        try {
            // 연결 url 설정


            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("user_id", user_id);
            jsonObject.accumulate("admin_id", admin_id);
            jsonObject.accumulate("password", password);
            jsonObject.accumulate("reserve_ID", reserve_ID);
            jsonObject.accumulate("acceptStatus", acceptStatus);


                if (flag == 1) {//updateStore를 위한 서버에 데이터 전송

                    jsonObject.accumulate("name", upStore.getName());//update를 위해 서버로 보낼 데이터들 req.on
                    jsonObject.accumulate("admin_id", upStore.getAdmin_id());
                    jsonObject.accumulate("tel", upStore.getTel());
                    jsonObject.accumulate("intro", upStore.getIntro());
                    jsonObject.accumulate("inform", upStore.getInform());
                    jsonObject.accumulate("address", upStore.getAddress());
                    jsonObject.accumulate("sector", upStore.getSector());
                    jsonObject.accumulate("longitude", upStore.getLongitude());
                    jsonObject.accumulate("latitude", upStore.getLatitude());

                }

                if (flag == 2) {//insertCloth
                    Log.e("err", inCloth.getName());
                    jsonObject.accumulate("cloth_ID", inCloth.getCloth_id());//insert를 위해 서버로 보낼 데이터들 req.on
                    jsonObject.accumulate("store_ID", inCloth.getStore_id());
                    jsonObject.accumulate("category", inCloth.getCategory());
                    jsonObject.accumulate("name", inCloth.getName());
                    jsonObject.accumulate("intro", inCloth.getIntro());
                    jsonObject.accumulate("price", inCloth.getPrice());
                    jsonObject.accumulate("count", inCloth.getCount());
                    jsonObject.accumulate("sex", inCloth.getSex());
                }

                if (flag == 3) {//insertOrder, updateOrder
                    jsonObject.accumulate("user_id", order.getUserID());//insert를 위해 서버로 보낼 데이터들 req.on
                    jsonObject.accumulate("admin_id", order.getAdminID());
                    jsonObject.accumulate("accept", order.getAcceptStatus());

                    JSONArray jArray = new JSONArray();// 배열을 위해 선언
                    for (int i = 0; i < basketList.size(); i++) {
                        JSONObject sObject = new JSONObject();
                        sObject.put("cloth_id", basketList.get(i).getClothes().getCloth_id());
                        sObject.put("count", basketList.get(i).getCnt());
                        jArray.put(sObject);
                    }
                    jsonObject.put("basketList", jArray);//배열을 넣음
                }
                if (flag == 4) {//insertAccount, updateAccount
                    jsonObject.accumulate("ID", account.getId());//insert를 위해 서버로 보낼 데이터들 req.on
                    jsonObject.accumulate("PW", account.getPw());
                    jsonObject.accumulate("name", account.getName());
                    jsonObject.accumulate("HP", account.getPhone());
                    jsonObject.accumulate("priv", account.getPrivilege());

                }

                if (flag == 5) {//insertBasket, deleteBasket
                    jsonObject.accumulate("cloth_ID", basketItem.getClothes().getCloth_id());
                    jsonObject.accumulate("count", basketItem.getCnt());

                }
                if (flag == 6) {//insertOrder, updateOrder
                    jsonObject.accumulate("user_id", reserve.getUser_id());//insert를 위해 서버로 보낼 데이터들 req.on
                    jsonObject.accumulate("admin_id", reserve.getAdmin_id());
                    jsonObject.accumulate("accept", reserve.getAcceptStatus());
                    jsonObject.accumulate("time", reserve.getRentalDate());

                    JSONArray jArray = new JSONArray();// 배열을 위해 선언
                    for (int i = 0; i < basketList.size(); i++) {
                        JSONObject sObject = new JSONObject();
                        sObject.put("cloth_id", basketList.get(i).getClothes().getCloth_id());
                        sObject.put("count", basketList.get(i).getCnt());
                        jArray.put(sObject);
                    }
                    jsonObject.put("basketList", jArray);//배열을 넣음
                }



                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();//이거문제

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                conn.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                conn.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                conn.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                conn.connect();

                //서버로 보내기위해서 스트림 만듬
                OutputStream outStream = conn.getOutputStream();
                //버퍼를 생성하고 넣음
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();//버퍼를 받아줌
                //////////서버로 데이터 전송




/*
            if(flag !=0){// insert update delete는 받아올 필요없음 고로 건너뜀
                flag = 0;
                conn.getInputStream();
                conn.disconnect();
                return jsonHtml.toString();
            }
*/


            // 연결되었으면.

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            for (; ; ) {
                // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                String line = br.readLine();
                if (line == null)
                    break;
                // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                jsonHtml.append(line + "\n");
            }
            br.close();
            conn.disconnect();



        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return jsonHtml.toString();

    }

    public int changeStoreID(String admin_id){ // "jong4876" 값을 -> 1로
        int id = -1;
        JSONTask JT = new JSONTask();
        try{

            JT.setUser_id(admin_id);
            String str = JT.execute("http://13.209.89.187:3443/changeID").get();
            Log.e("err",str);
            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                id = jo.getInt("id");

            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            Log.e("err","JSONTask Disconnect!");
            JT.cancel(true);
        }


        return id;
    }
    public String changeToAdminID(int storeID){ // 1 -> jong4876
        String adminID = null;
        JSONTask JT = new JSONTask();
        try{

            JT.setUser_id(storeID+"");
            String str = JT.execute("http://13.209.89.187:3443/changeToAdminID").get();

            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                adminID = jo.getString("admin_id");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            Log.e("err","JSONTask Disconnect!");
            JT.cancel(true);
        }
        return adminID;
    }



    /////////검색메서드

    public  ArrayList<Account> getAccountAll(String user_id){ // JSON.HTML넣어서 사용, 전송되는 user_id jong4876~~
        ArrayList<Account> accountList = new ArrayList<Account>();
        Account account;

        try {
            JSONTask JT = new JSONTask();
            JT.setUser_id(user_id);
            String str = JT.execute("http://13.209.89.187:3443/account").get();
            JSONArray ja = new JSONArray(str);

            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                String ID = jo.getString("ID");
                String PW = jo.getString("PW");
                String name = jo.getString("name");
                String HP = jo.getString("HP");
                int priv = jo.getInt("priv");

                // TODO 영업 시작시간 종료시간 가져와서 넣어야됨
                account = new Account(ID, PW, name, HP, priv);
                accountList.add(account);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return accountList;
    }

    public  ArrayList<Store> getAdminStoreAll(String user_id){ // JSON.HTML넣어서 사용, 전송되는 user_id jong4876~~
        ArrayList<Store> storeList = new ArrayList<Store>();
        Store store;

        JSONTask JT = new JSONTask();
        try {
            JT.setUser_id(user_id);
            String str = JT.execute("http://13.209.89.187:3443/store").get();
            JSONArray ja = new JSONArray(str);
            // txtView.setText(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                int id = jo.getInt("id");
                String name = jo.getString("name");
                String admin_id = jo.getString("admin_id");
                String tel = jo.getString("tel");
                String intro = jo.getString("intro");
                String inform = jo.getString("inform");
                String address = jo.getString("address");
                int sector = jo.getInt("sector");
                double latitude = jo.getDouble("latitude");
                double longitude = jo.getDouble("longitude");

                // TODO 영업 시작시간 종료시간 가져와서 넣어야됨
                store = new Store(id, name, admin_id,tel,intro, inform, address, sector, latitude, longitude, "", "");
                storeList.add(store);//accountList 차례대로 삽입
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            Log.e("err","JSONTask Disconnect!");
            JT.cancel(true);
        }
        return storeList;
    }

    public  ArrayList<Store> getCustomerStoreAll(){ // JSON.HTML넣어서 사용, 전송되는 user_id jong4876~~
        ArrayList<Store> storeList = new ArrayList<Store>();
        Store store;
        String user_id = "allUser";

        try {
            JSONTask JT = new JSONTask();
            JT.setUser_id(user_id);
            String str = JT.execute("http://13.209.89.187:3443/storeCustomer").get();
            JSONArray ja = new JSONArray(str);
            // txtView.setText(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                int id = jo.getInt("id");
                String name = jo.getString("name");
                String admin_id = jo.getString("admin_id");
                String tel = jo.getString("tel");
                String intro = jo.getString("intro");
                String inform = jo.getString("inform");
                String address = jo.getString("address");
                int sector = jo.getInt("sector");
                double latitude = jo.getDouble("latitude");
                double longitude = jo.getDouble("longitude");

                // TODO 영업 시작시간 종료시간 가져와서 넣어야됨
                store = new Store(id, name, admin_id,tel,intro, inform, address, sector, latitude, longitude, "", "");
                storeList.add(store);//accountList 차례대로 삽입
            }


        }catch(Exception e){
            e.printStackTrace();
        }
        return storeList;
    }

    public ArrayList<Clothes> getStoreClothesList(int storeID){ //storeID로 해당 매장 옷 검색
        ArrayList<Clothes> clothesList = new ArrayList<Clothes>();
        Clothes clothes;

        try{
            JSONTask JT = new JSONTask();
            JT.setUser_id(""+storeID);
            String str = JT.execute("http://13.209.89.187:3443/clothes").get();

            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                int cloth_ids = jo.getInt("cloth_id");
                int store_ids = jo.getInt("store_id");
                int category = jo.getInt("category");
                String name= jo.getString("name");
                String intro = jo.getString("intro");
                int price = jo.getInt("price");
                int count = jo.getInt("count");
                int sex = jo.getInt("sex");
                clothes = new Clothes(cloth_ids,store_ids,category, name,intro, price, count, sex);
                clothesList.add(clothes);//accountList 차례대로 삽입
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return clothesList;
    }

    public ArrayList<Clothes> getClothesAll(String admin_id){ // 아이디에 해당하는 매장의 옷 검색
        ArrayList<Clothes> clothesList = new ArrayList<Clothes>();
        Clothes clothes;

        JSONTask JT = new JSONTask();
        try{
            int id = JSONTask.getInstance().changeStoreID(admin_id);// store클래스의 id값으로 변환 1,2,3,4~~

            JT.setUser_id(id+"");
            String str = JT.execute("http://13.209.89.187:3443/clothes").get();

            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                int cloth_ids = jo.getInt("cloth_id");
                int store_ids = jo.getInt("store_id");
                int category = jo.getInt("category");
                String name= jo.getString("name");
                String intro = jo.getString("intro");
                int price = jo.getInt("price");
                int count = jo.getInt("count");
                int sex = jo.getInt("sex");
                clothes = new Clothes(cloth_ids,store_ids,category, name,intro, price, count, sex);
                clothesList.add(clothes);//accountList 차례대로 삽입
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return clothesList;
    }
    public ArrayList<Clothes> getRandomClothesAll(int cnt){ // 옷 랜덤뽑기***
        ArrayList<Clothes> clothesList = new ArrayList<Clothes>();
        Clothes clothes;

        JSONTask JT = new JSONTask();
        try{
            JT.setUser_id(cnt+"");
            String str = JT.execute("http://13.209.89.187:3443/RandomClothes").get();
            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                int cloth_ids = jo.getInt("cloth_id");
                int store_ids = jo.getInt("store_id");
                int category = jo.getInt("category");
                String name= jo.getString("name");
                String intro = jo.getString("intro");
                int price = jo.getInt("price");
                int count = jo.getInt("count");
                int sex = jo.getInt("sex");
                clothes = new Clothes(cloth_ids,store_ids,category, name,intro, price, count, sex);
                clothesList.add(clothes);//accountList 차례대로 삽입
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return clothesList;
    }

    // client의 예약 목록 가져오는 메소드
    public ArrayList<Reserve> getCustomerReservationList(String customerID){ // user_id가 주문한 옷 전체 검색
        ArrayList<Reserve> reserves = new ArrayList<Reserve>();
        Reserve reserve;

        JSONTask JT = new JSONTask();
        try{
            JT.setUser_id(customerID);
            String str = JT.execute("http://13.209.89.187:3443/reserveCustomer").get();
            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                int reserveID = jo.getInt("ID");
                String userID = jo.getString("user_ID");
                String adminID = jo.getString("admin_ID");
                int acceptStatus = jo.getInt("accept");
                String date = jo.getString("date");//Date형?

                reserve = new Reserve(reserveID, userID, adminID, acceptStatus, date);
                reserves.add(reserve);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return reserves;
    }

    public ArrayList<Reserve> getReserveBeforeReserveID(String user_id, String admin_id) { // reserve_id가 주문한 옷 전체 검색
        ArrayList<Reserve> reserveArrayList = new ArrayList<>();
        Reserve reserve;

        try {
            JSONTask JT = new JSONTask();
            JT.setUser_id(user_id);
            JT.setAdmin_id(admin_id);

            String str = JT.execute("http://13.209.89.187:3443/reserveID").get();
            JSONArray ja = new JSONArray(str);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                int orderNo = jo.getInt("ID");
                String userID = jo.getString("user_ID");
                String adminID = jo.getString("admin_ID");
                int acceptStatus = jo.getInt("accept");
                String date = jo.getString("date");//Date형?

                reserve = new Reserve(orderNo, userID, adminID, acceptStatus, date);
                reserveArrayList.add(reserve);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reserveArrayList;
    }

    public ArrayList<Order> getOrderCustomerAll(String customer_id){ // user_id가 주문한 옷 전체 검색
        ArrayList<Order> orderList = new ArrayList<Order>();
        Order order;

        try{
            JSONTask JT = new JSONTask();
            JT.setUser_id(customer_id);
            String str = JT.execute("http://13.209.89.187:3443/reserveCustomer").get();
            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                int orderNo = jo.getInt("ID");
                String userID = jo.getString("user_ID");
                String adminID = jo.getString("admin_ID");
                int acceptStatus = jo.getInt("accept");
                String date = jo.getString("date");//Date형?

                order = new Order(orderNo,userID,adminID,acceptStatus,date);
                orderList.add(order);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return orderList;
    }
    public ArrayList<Order> getOrderAdminAll(String admin_id){ // user_id가 주문한 옷 전체 검색
        ArrayList<Order> orderList = new ArrayList<Order>();
        Order order;

        JSONTask JT = new JSONTask();
        try{
            JT.setUser_id(admin_id);
            String str = JT.execute("http://13.209.89.187:3443/reserveAdmin").get();
            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                int orderNo = jo.getInt("ID");
                String userID = jo.getString("user_ID");
                String adminID = jo.getString("admin_ID");
                int acceptStatus = jo.getInt("accept");
                String date = jo.getString("date");//Date형?

                order = new Order(orderNo,userID,adminID,acceptStatus,date);
                orderList.add(order);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return orderList;
    }

    public ArrayList<BasketItem> getBascketCustomerAll(int reserve_ID){ // reserve_id가 장바구니에 담은 옷 전체 검색***
        ArrayList<BasketItem> basketList = new ArrayList<BasketItem>();
        Clothes clothes;


        try{
            JSONTask JT = new JSONTask();
            JT.setReserve_ID(reserve_ID);
            String str = JT.execute("http://13.209.89.187:3443/basketCustomer").get();

            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                int cloth_ids = jo.getInt("cloth_id");
                int store_ids = jo.getInt("store_id");
                int category = jo.getInt("category");
                String name= jo.getString("name");
                String intro = jo.getString("intro");
                int price = jo.getInt("price");
                int count = jo.getInt("count");
                int sex = jo.getInt("sex");
                int basket_count = jo.getInt("basket_count");
                clothes = new Clothes(cloth_ids,store_ids,category, name,intro, price, count, sex);
                BasketItem basketItem = new BasketItem(clothes,basket_count);
                basketList.add(basketItem);//accountList 차례대로 삽입
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return basketList;
    }




    public ArrayList<Clothes> getBascketAdminAll(String admin_id){ // user_id가 장바구니에 담은 옷 전체 검색
        ArrayList<Clothes> clothesList = new ArrayList<Clothes>();
        Clothes clothes;

        try{
            JSONTask JT = new JSONTask();
            JT.setUser_id(admin_id);
            String str = JT.execute("http://13.209.89.187:3443/basketAdmin").get();

            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                int cloth_ids = jo.getInt("cloth_id");
                int store_ids = jo.getInt("store_id");
                int category = jo.getInt("category");
                String name= jo.getString("name");
                String intro = jo.getString("intro");
                int price = jo.getInt("price");
                int count = jo.getInt("count");
                int sex = jo.getInt("sex");
                clothes = new Clothes(cloth_ids,store_ids,category, name,intro, price, count, sex);
                clothesList.add(clothes);//accountList 차례대로 삽입
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return clothesList;
    }

    //////////수정메서드
    public void updateAccount(Account upAccount){ //바꿀 값이 들어 있는 account 클래스와, 바꿀 account의 아이디 전달
        try {
            JSONTask JT = new JSONTask();
            JT.setAccount(upAccount);
            JT.execute("http://13.209.89.187:3443/updateAccount");
            Log.e("err","update Success");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateStore(Store upStore, String admin_id){ //바꿀 값이 들어 있는 store 클래스와, 바꿀 store의 아이디 전달
        JSONTask JT = new JSONTask();
        try {
            JT.setUpStore(upStore);
            JT.setUser_id(admin_id);
            JT.execute("http://13.209.89.187:3443/updateStore");
            Log.e("err","where are you");

        }catch(Exception e){
            e.printStackTrace();
        } finally{
            Log.e("err","JSONTask Disconnect!");
            JT.cancel(true);
        }
    }
    public void updateCloth(Clothes upClothes){
        try {
            JSONTask JT = new JSONTask();
            JT.setUser_id(upClothes.getCloth_id()+"");// user_id가 필요없으므로 적당히 전달
            JT.setCloth(upClothes);
            JT.execute("http://13.209.89.187:3443/updateCloth");
            Log.e("err","update 성공!!");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateOrderAccept(int reserve_id, int acceptstatus){//acceptStatus 상태 변경   /// reserve = order!!!
        try {
            JSONTask JT = new JSONTask();
            JT.setUser_id(reserve_id+"");// user_id가 필요없으므로 적당히 전달
            JT.setAcceptStatus(acceptstatus);
            JT.execute("http://13.209.89.187:3443/updateReserve");
            Log.e("err","update 성공!!");
        }catch(Exception e){
            e.printStackTrace();
        }
    }




    //////////삽입메서드

    public void insertAccount(Account newAccount){ // user_id에 해당하는 매장에 옷 추가(관리자)
        JSONTask JT = new JSONTask();
        try {
            JT.setAccount(newAccount);
            String str = JT.execute("http://13.209.89.187:3443/insertAccount").get();// URL변경필수

            if(str != null){
                Log.e("err","JSONTask Disconnect!");
                JT.cancel(true);
            }

            Log.e("err","account삽입 성공!!");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void insertCloth(Clothes inCloth, int store_id){ // user_id에 해당하는 매장에 옷 추가(관리자)
        JSONTask JT = new JSONTask();
        try {

            JT.setUser_id(store_id+"");
            JT.setCloth(inCloth);
            JT.execute("http://13.209.89.187:3443/insertCloth");// URL변경필수
            Log.e("err","cloth삽입 성공!!");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void insertReserve(Reserve reserve, ArrayList<BasketItem> basketList){ // user_id에 해당하는 매장에 옷 추가(관리자)
        JSONTask JT = new JSONTask();
        try {
            JT.setReserveBasket(reserve, basketList);
            String str = JT.execute("http://13.209.89.187:3443/insertReserve").get();// URL변경필수
            Log.e("err","order삽입 성공!!");
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            JT.cancel(true);
        }
    }

    public void insertOrder(Order order, ArrayList<BasketItem> basketList){ // user_id에 해당하는 매장에 옷 추가(관리자)
        try {////
            JSONTask JT = new JSONTask();
            JT.setOrderBasket(order, basketList);
            JT.execute("http://13.209.89.187:3443/insertReserve");// URL변경필수
            Log.e("err","order삽입 성공!!");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /////////삭제메서드
    public void deleteCloth(int clothID){ // user_id에 해당하는 매장에 옷 삭제(관리자)
        JSONTask JT = new JSONTask();
        try {
            JT.setUser_id(""+clothID);
            JT.execute("http://13.209.89.187:3443/deleteCloth");// URL변경필수
            Log.e("err","cloth삭제 성공");
        }catch(Exception e) {
            e.printStackTrace();
        } finally{
            Log.e("err","JSONTask Disconnect!");
            JT.cancel(true);
        }
    }
    public void deleteOrder(int orderNo){ // user_id에 해당하는 매장에 옷 삭제(관리자)
        try {

            JSONTask JT = new JSONTask();
            JT.setReserve_ID(orderNo);
            JT.execute("http://13.209.89.187:3443/deleteReserve");// URL변경필수
            Log.e("err","Order삭제 성공");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteBasket(int reserve_ID){ // user_id에 해당하는 매장에 옷 삭제(관리자)
        try {

            JSONTask JT = new JSONTask();
            JT.setReserve_ID(reserve_ID);
            JT.execute("http://13.209.89.187:3443/deleteReserve");// URL변경필수
            Log.e("err","Basket삭제 성공");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteOrderAndBasket(int reserve_ID){// order basket 삽입한번에
        //JSONTask.getInstance().deleteBasket(reserve_ID);
        JSONTask.getInstance().deleteOrder(reserve_ID);//cascade로 삭제
    }


    //로그인 메서드
    public int getLoginResult(String user_id, String password){ // user_id가 주문한 옷 전체 검색
        int result = -999;
        JSONTask JT = new JSONTask();
        try{

            JT.setUser_id(user_id);
            JT.setPassword(password);
            String str = JT.execute("http://13.209.89.187:3443/login").get();
            JSONArray ja = new JSONArray(str);
            if(ja.length() == 0)
                return 0;

            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                String ID = jo.getString("ID");
                Log.e("err",""+ID);
            }

            return 1;
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            Log.e("err","JSONTask Disconnect!");
            JT.cancel(true);
        }

        return 1;
    }

    public String getLoginID(){// 현재 로그인 중인 아이디 가져오기(세션에 있는 아이디)
        String userID = null;
        JSONTask JT = new JSONTask();
        try{
            String str = JT.execute("http://13.209.89.187:3443/getLoginID").get();
            JSONArray ja = new JSONArray(str);
            for(int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                userID = jo.getString("ID");
                Log.e("err",""+userID);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            JT.cancel(true);
        }

        return userID;

    }
}