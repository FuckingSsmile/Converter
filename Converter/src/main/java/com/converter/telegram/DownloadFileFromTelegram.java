package com.converter.telegram;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DownloadFileFromTelegram {

    public void uploadFile(String file_name, String file_id, String token) {
        try {

            URL url = new URL("https://api.telegram.org/bot" + token + "/getFile?file_id=" + file_id);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String res = in.readLine();
            JSONObject jresult = new JSONObject(res);
            JSONObject path = jresult.getJSONObject("result");
            String file_path = path.getString("file_path");
            URL download = new URL("https://api.telegram.org/file/bot" + token + "/" + file_path);
            FileOutputStream fos = new FileOutputStream(file_name);
            System.out.println("Start upload");
            ReadableByteChannel rbc = Channels.newChannel(download.openStream());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Uploaded!");
    }
}
