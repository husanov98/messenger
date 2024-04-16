package uz.mh.messenger.service;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import uz.mh.messenger.dto.Template;
import uz.mh.messenger.dto.TgmData;


import java.io.IOException;
import java.util.List;

@Service
public class IntegrateWithEgs {
    public void sendToEgs(List<TgmData> dataList, String company){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            String url = "https://egsgroupapp.uz/api/queries/store";
            HttpPost httpPost = new HttpPost(url);
            Template template = new Template();
            template.setData(dataList);
            template.setDb(company);
            Gson gson = new Gson();
            String json = gson.toJson(template);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type","application/json");
            try(CloseableHttpResponse response = httpClient.execute(httpPost)){
                HttpEntity responseEntity = response.getEntity();
                System.out.println("Response Status: " + response.getStatusLine());
                if (responseEntity != null){
                    String responseBody = EntityUtils.toString(responseEntity);
                    System.out.println("Response Content: " + responseBody);
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
