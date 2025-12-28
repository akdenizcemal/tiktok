package com.tiktok;

import java.io.IOException;
import java.net.URI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TikTokVideDownloader {

   public byte[] download(String pageUrl)  {
        HttpRequest rr = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(pageUrl))
                .build();
        var client = HttpClient.newHttpClient();
        System.out.println(client.getClass().getName());
       String jsonData = null;
       try {
           jsonData = extractUniversalData(client, rr);
           var downloadAddr = extractDownloadAddr(jsonData);
           return fetchVideo(downloadAddr, client);
       } catch (IOException | InterruptedException e) {
           throw new RuntimeException(e);
       }
    }

    private String extractUniversalData(HttpClient client, HttpRequest rr) throws IOException, InterruptedException {
        HttpResponse<String> cevap = client.send(rr, HttpResponse.BodyHandlers.ofString());
        Document document = Jsoup.parse(cevap.body());
        Element jsonElement = document.getElementById("__UNIVERSAL_DATA_FOR_REHYDRATION__");
        return jsonElement.data();
    }

    private String extractDownloadAddr(String jsonData) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonData);
        return root.get("__DEFAULT_SCOPE__").get("webapp.video-detail").get("itemInfo")
                .get("itemStruct").get("video").get("downloadAddr").asText();
    }

    private byte[] fetchVideo(String replaced, HttpClient client) throws IOException, InterruptedException, IOException {
        var videoRequest = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(replaced))
                .build();
        HttpResponse<byte[]> resp = client.send(videoRequest, HttpResponse.BodyHandlers.ofByteArray());
        System.out.println(resp.statusCode());
        if (!isSuccessStatusCode(resp.statusCode())){
            throw new IllegalStateException("An error occured - status code :%s".formatted(resp.statusCode()));
        }
        return resp.body();
    }
    private boolean isSuccessStatusCode(int statusCode)
    {
         return (statusCode >= 200) && (statusCode <= 299); }
    }





