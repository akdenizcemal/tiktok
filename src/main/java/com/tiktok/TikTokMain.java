import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


void main() {
    String pageUrl = "https://www.tiktok.com/@livealoha1230/video/7587966822691966222";


    HttpRequest rr = HttpRequest
            .newBuilder()
            .GET()
            .uri(URI.create(pageUrl))
            .build();
    HttpClient client = HttpClient.newHttpClient();
    System.out.println(client.getClass().getName());
    try {
        HttpResponse<String> cevap = client.send(rr, HttpResponse.BodyHandlers.ofString());
        int start=cevap.body().indexOf("downloadAddr");
        String downloadadrString = cevap.body().substring(start);
        int end = downloadadrString.indexOf("t_chain_token");
        String downloadUrl = cevap.body().substring(start+15, start+end+13);
        String replaced=downloadUrl.replace("\\u002F","/");
        HttpRequest videoRequest= HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(replaced))
                .build();
        HttpResponse<byte[]> respp=client.send(videoRequest,HttpResponse.BodyHandlers.ofByteArray());
        System.out.println(respp.statusCode());
    }catch (Exception ex){
        System.out.println(ex);
    }


}

