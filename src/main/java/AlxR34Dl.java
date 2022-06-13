import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlxR34Dl {
    public static void main(String[] args) {
        try {
            //Initialize
            AlxR34DlVar v = new AlxR34DlVar();
            String tags = v.getTags();
            int numb = v.getNumber();
            StringBuilder apiReqBuilder = new StringBuilder("https://api.rule34.xxx/index.php?page=dapi&s=post&q=index&limit=1000");

            //Check number of files
            if(numb <= 0){
                System.out.println("No limit set. Using default limit of 201000.");
                numb = 201000;
            } else if(numb > 201000){
                System.out.println("The API of rule34.xxx does not allow for more than 201x1000 posts to be requested. Using default limit of 201000.");
                numb = 201000;
            } else {
                //System.out.println("Up to " + numb + " posts will be targeted.");
            }

            //Check tags
            if(!tags.isEmpty()){
                System.out.println("Targeting up to " + numb + " posts for tags '" + tags + "'.");
                apiReqBuilder.append("&tags=");
                apiReqBuilder.append(tags.replace("\s*", "+"));
            } else {
                System.out.println("Targeting up to " + numb + " posts for no tags in particular. Files will be downloaded to '000_notags'");
            }
            String apiReqBase = apiReqBuilder.toString();

            //Determine required number of pages
            int reqPages = neededPages(numb, 1000);
            System.out.println(reqPages + " page(s) will be requested.");

            //Initalize list for target files
            List<String> targetFiles = new ArrayList<String>();

            //Request each page
            for(int p=0; p < reqPages; p++){
                StringBuilder apiReqPageUrlBuilder = new StringBuilder(apiReqBase);
                apiReqPageUrlBuilder.append("&pid=");
                apiReqPageUrlBuilder.append(p);
                String apiReqPageUrl = apiReqPageUrlBuilder.toString();

                //Read page line by line
                BufferedReader pageReader = new BufferedReader(new InputStreamReader(new URL(apiReqPageUrl).openStream()));
                while(pageReader.ready() && targetFiles.size() < numb){
                    String currentLine = pageReader.readLine();
                    if(currentLine.contains("file_url=")){
                        targetFiles.add(extractTarget(currentLine));
                    }
                }
                pageReader.close();
            }
            System.out.println(targetFiles.size() + " target posts found.");

            //Target directory path
            StringBuilder targetDirectory = new StringBuilder();
            if(tags.isEmpty()){
                targetDirectory.append("/alxr34dl/000_notags/");
            } else {
                targetDirectory.append("/alxr34dl/" + tags);
            }

            //Prepare folder for download
            if(!tags.isEmpty()){
                String cpath = new File("").getAbsolutePath() + targetDirectory;
                Path path = Paths.get(cpath);
                Files.createDirectories(path);
                System.out.println("Download directory '" + cpath + "' created.");
            } else {
                String cpath = new File("").getAbsolutePath() + targetDirectory;
                Path path = Paths.get(cpath);
                Files.createDirectories(path);
                System.out.println("Download directory '" + cpath + "' created.");
            }


            //Download all files one by one
            String fitTarget = targetDirectory.deleteCharAt(0).toString();
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for(String target : targetFiles){
                executor.submit(new DownloadThread(target, fitTarget));
            }
            executor.shutdown();

            //Final message. Goodbye.
            if(executor.isTerminated()){
                System.out.println("Downloads finished. Errorless or complete downloads are not guaranteed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String extractTarget(String toExtract){
        String extracted = "";

        String[] lineparts = toExtract.split(" ");
        for(String sp : lineparts){
            if(sp.contains("file_url")){
                StringBuilder fileTarget = new StringBuilder(sp);
                fileTarget.replace(0, 10, "");
                fileTarget.deleteCharAt(fileTarget.length()-1);

                extracted = fileTarget.toString();
                //System.out.println(fileTarget);
            }
        }
        return extracted;
    }

    public static int totalPosts(URL dapirequest) throws Exception {
        int total = 0;

        BufferedReader reader = new BufferedReader(new InputStreamReader(dapirequest.openStream()));

        while (reader.ready()) {
            String currentline = reader.readLine();
            StringBuilder editedline = new StringBuilder();

            if (currentline.contains("posts count")) {
                Pattern pat = Pattern.compile("count=\".*\"\s*o");
                Matcher mat = pat.matcher(currentline);

                while(mat.find()){
                    editedline.append(mat.group());
                    editedline.delete(editedline.length()-3, editedline.length());
                    editedline.delete(0, 7);
                }

                total = Integer.valueOf(editedline.toString());
            }
        }
        return total;
    }

    public static int neededPages(int postCount, int pageSize) {
        return postCount / pageSize + (postCount % pageSize == 0 ? 0 : 1);
    }
}