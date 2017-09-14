package com.ssu.sangjunianjuni.smartbabycare;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yoseong on 2017-05-24.
 * php 응답받는 클래스
 */

public class PHPRequest {
    private URL url;

    public PHPRequest(String url) throws MalformedURLException { this.url = new URL(url); }

    private String readStream(InputStream in) throws IOException {
        StringBuilder jsonHtml = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = null;

        while ((line = reader.readLine()) != null)
            jsonHtml.append(line);

        reader.close();
        return jsonHtml.toString();
    }

    public String PhPtest(final String USER_ID, String PASSWORD, String NAME, int MONTH, double HEIGHT, double WEIGHT, String DATE, String TIME, String SEX, String MEAL, String PHOTO_URI) {

        try {
            NetworkUtil.setNetworkPolicy();
            String postData = "USER_ID=" + USER_ID + "&" + "PASSWORD=" + PASSWORD + "&" + "NAME=" + NAME + "&" + "MONTH=" + MONTH + "&" + "HEIGHT=" + HEIGHT + "&" + "WEIGHT=" + WEIGHT + "&" + "DATE=" + DATE + "&" + "TIME=" + TIME + "&" + "SEX=" + SEX + "&" + "MEAL=" + MEAL+ "&" + "PHOTO_URI=" + PHOTO_URI;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            return result;
        } catch (IOException e) {
            Log.i("PHPRequest", "request was failed");
            return null;
        }

    }

    public String PhPtest(final String USER_ID, String PASSWORD, String NAME, int MONTH, double HEIGHT, double WEIGHT, String DATE, String TIME, String SEX, String MEAL) {

        try {
            NetworkUtil.setNetworkPolicy();
            String postData = "USER_ID=" + USER_ID + "&" + "PASSWORD=" + PASSWORD + "&" + "NAME=" + NAME + "&" + "MONTH=" + MONTH + "&" + "HEIGHT=" + HEIGHT + "&" + "WEIGHT=" + WEIGHT + "&" + "DATE=" + DATE + "&" + "TIME=" + TIME + "&" + "SEX=" + SEX + "&" + "MEAL=" + MEAL;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            return result;
        } catch (IOException e) {
            Log.i("PHPRequest", "request was failed");
            return null;
        }

    }

    public String PhPtest(final String Date, String Time, int Heartbeat, double Bodytemperature) {
        try {
            NetworkUtil.setNetworkPolicy();
            String postData = "Date=" + Date + "&" + "Time=" + Time + "&" + "Heartbeat=" + Heartbeat+ "&" + "Bodytemperature=" + Bodytemperature;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            return result;
        } catch (IOException e) {
            Log.i("PHPRequest", "request was failed");
            return null;
        }

    }

    public String PhPtest(final String Title, String Author, String Date, String Context) {
        try {
            NetworkUtil.setNetworkPolicy();
            String postData = "Title=" + Title + "&" + "Author=" + Author + "&" + "Date=" + Date+ "&" + "Context=" + Context;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            return result;
        } catch (IOException e) {
            Log.i("PHPRequest", "request was failed");
            return null;
        }

    }

    public String PhPtest(final String Title, String Author, String Date, String Context, String Flag, int tmp) {
        try {
            NetworkUtil.setNetworkPolicy();
            String postData = "Title=" + Title + "&" + "Author=" + Author + "&" + "Date=" + Date+ "&" + "Context=" + Context+ "&" + "Flag=" + Flag;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            return result;
        } catch (IOException e) {
            Log.i("PHPRequest", "request was failed");
            return null;
        }

    }

    public String PhPtest(final String ContentAuthor, String ContentDate, String ReplyAuthor, String ReplyDate, String ReplyContext) {
        try {
            NetworkUtil.setNetworkPolicy();
            String postData = "ContentAuthor=" + ContentAuthor + "&" + "ContentDate=" + ContentDate + "&" + "ReplyAuthor=" + ReplyAuthor+ "&" + "ReplyDate=" + ReplyDate+ "&" + "ReplyContext=" + ReplyContext;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            return result;
        } catch (IOException e) {
            Log.i("PHPRequest", "request was failed");
            return null;
        }

    }

    public String PhPtest(final String USER_ID, double HEIGHT, double WEIGHT, String MEAL, String PHOTO_URI) {

        try {
            NetworkUtil.setNetworkPolicy();
            String postData = "USER_ID=" + USER_ID + "&" + "HEIGHT=" + HEIGHT + "&" + "WEIGHT=" + WEIGHT +"&" + "MEAL=" + MEAL + "&" + "PHOTO_URI=" + PHOTO_URI;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            return result;
        } catch (IOException e) {
            Log.i("PHPRequest", "request was failed");
            return null;
        }

    }

    public String PhPtest(String token, String User_ID) {
        try {
            NetworkUtil.setNetworkPolicy();
            String postData = "Token=" + token + "&" + "User_ID=" + User_ID;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            Log.i("PHP", token);
            Log.i("PHP", User_ID);
            return result;
        } catch (IOException e) {
            Log.i("PHPRequest", "request was failed");
            return null;
        }
    }
}
