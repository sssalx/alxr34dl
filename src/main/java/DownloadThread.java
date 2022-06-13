import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.URL;

public class DownloadThread extends Thread{
    private final String targetURL;
    private final String targetFile;

    public DownloadThread(String targetURL, String targetFile) {
        this.targetURL = targetURL;
        this.targetFile = targetFile;
    }

    public void run(){
        try{
            BufferedInputStream fileLoader = new BufferedInputStream(new URL(targetURL).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile + "/" + targetURL.split("/")[5]);
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileLoader.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            System.out.println(targetURL);
            System.out.println(targetFile + "/" + targetURL.split("/")[5]);
            fileLoader.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
