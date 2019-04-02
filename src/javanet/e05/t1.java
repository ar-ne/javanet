package javanet.e05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class t1 {
    public static void main(String[] args) throws IOException {
        URL url = new URL("http://localhost:9090/");
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        boolean inForm = false;
        while ((line = reader.readLine()) != null) {
            if (line.toLowerCase().contains("<form")) inForm = true;
            if (line.toLowerCase().contains("</form>")) inForm = false;
            if (inForm) {
                if (line.replace(" ", "").toLowerCase().contains("<input")) {
                    String name = line.substring(line.indexOf("name=") + 6).replace(">", "");
                    System.out.println(name.substring(0, name.length() - 1));
                }
            }
        }
    }
}
