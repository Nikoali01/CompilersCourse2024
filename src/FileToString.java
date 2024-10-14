import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileToString {
    public String getStringFromTheLink(String filePath) {
        try {
            return readFileToString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFileToString(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex);
                }
                contentBuilder.append(line).append(System.lineSeparator());
            }
        }
        return contentBuilder.toString();
    }
}
