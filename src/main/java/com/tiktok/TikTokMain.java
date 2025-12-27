import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


void main() throws IOException, InterruptedException {
    String pageUrl = "https://www.tiktok.com/@livealoha1230/video/7587966822691966222";


    HttpRequest rr = HttpRequest
            .newBuilder()
            .GET()
            .uri(URI.create(pageUrl))
            .build();
    var client = HttpClient.newHttpClient();
    System.out.println(client.getClass().getName());
    var jsonData = extractUniversalData(client, rr);
    var downloadAddr = extractDownloadAddr(jsonData);

    fetchAndStoreVideo(downloadAddr, client);


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

private void fetchAndStoreVideo(String replaced, HttpClient client) throws IOException, InterruptedException {
    var videoRequest = HttpRequest
            .newBuilder()
            .GET()
            .uri(URI.create(replaced))
            .build();
    HttpResponse<byte[]> respp = client.send(videoRequest, HttpResponse.BodyHandlers.ofByteArray());
    System.out.println(respp.statusCode());
}

