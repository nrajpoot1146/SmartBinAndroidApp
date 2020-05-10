package com.blkcaphax.smartbin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class WebConnection{
    private URL url;
    private HttpURLConnection httpURLConnection;
    private HttpsURLConnection httpsURLConnection;

    WebConnection() {
        try {
            url = new URL("https://smartbin734.000webhostapp.com/app");
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param str message to sent to client
     * @return responce from server
     * @throws IOException throw IO exception
     */
    String send(String str) throws IOException {
        String responce = null;
        httpsURLConnection.setRequestMethod("POST");
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpsURLConnection.getOutputStream()));
        bufferedWriter.write(str);
        bufferedWriter.flush();
        responce = this.readResponce();
        bufferedWriter.close();
        return responce;
    }

    /**
     *
     * @return responce from server
     * @throws IOException
     */
    String readResponce() throws IOException {
        String responce = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
        String line = bufferedReader.readLine();
        do{
            if(responce == null)
                responce = "";
            responce+=line;
        }while ((line = bufferedReader.readLine()) != null);
        bufferedReader.close();
        return responce;
    }

}
