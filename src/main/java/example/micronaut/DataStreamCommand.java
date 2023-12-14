package example.micronaut;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.reactor.http.client.ReactorStreamingHttpClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Command(name = "downloadfile", description = "...",
        mixinStandardHelpOptions = true)
public class DataStreamCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "Whether you want a verbose output", defaultValue = StringUtils.TRUE)
    boolean verbose;

    @Option(names = {"--url"}, required = true, defaultValue = "https://images.sergiodelamo.com/avatar.png")
    String url;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(DataStreamCommand.class, args);
    }

    public void run() {
        try {
            if (verbose) {
                System.out.println("Downloading " + url);
            }
            URL baseURL = new URL(url);
            HttpRequest<?> request = HttpRequest.GET(url);

            File outputFile = new File("downloadedImageWithHttpClient.png");
            if (outputFile.exists()) {
                System.out.println("Doing nothing. File already exists");

            } else {
                outputFile.createNewFile();
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    outputStream.write(downloadWithHttpClient(baseURL, request));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] downloadWithHttpClient(URL baseURL, HttpRequest<?> request) {
        HttpClient httpClient = HttpClient.create(baseURL);
        BlockingHttpClient client = httpClient.toBlocking();
        HttpResponse<byte[]> resp = client.exchange(request, byte[].class);
        return resp.body();
    }

}
