import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static final String MY_URL = "https://api.nasa.gov/planetary/apod?api_key=6ayKDVYWXrjAxD3OCBhvpNSVDenPNaG3Iu0DH3TX";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        String picUrl;

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("Vozzzila v5.0")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(MY_URL);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            InputStream content = response.getEntity().getContent();

            NasaObject nasaObject = mapper.readValue(content, NasaObject.class);
            picUrl = nasaObject.getUrl();
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] arrFileName = picUrl.split("/");
        String fileName = arrFileName[arrFileName.length-1];
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

       try {
           URL url = new URL(picUrl);
           InputStream inputStream = url.openStream();
           OutputStream outputStream = new FileOutputStream(fileName);
           byte[] buffer = new byte[2048];

           int length = 0;

           while ((length = inputStream.read(buffer)) != -1) {
               outputStream.write(buffer, 0, length);
           }

           inputStream.close();
           outputStream.close();

       } catch(Exception e) {
           throw new RuntimeException(e);
       }
    }
}
