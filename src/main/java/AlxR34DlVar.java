import java.io.*;

public class AlxR34DlVar {
    private String tags = "";
    private int number = 0;

    public AlxR34DlVar() throws Exception{
        File parameters = new File(new File("").getAbsolutePath() + "/parameters.txt");
        if(parameters.createNewFile()){
            BufferedWriter write = new BufferedWriter(new FileWriter(new File("").getAbsolutePath() + "/parameters.txt", true));
            write.append("Enter tags as you would on rule34.xxx, seperated by single spaces. Keep the number of posts as low as needed, otherwise runtime might be lengthy."); write.newLine();
            write.append("tags="); write.newLine();
            write.append("number="); write.newLine();
            write.close();
            System.out.println("'parameters.txt' did not exist and has been created. Enter your parameters and restart.");
            System.out.println("Enter tags as you would on rule34.xxx, seperated by single spaces so that the line similar to this:");
            System.out.println("    tags=big_breasts blonde_hair bodysuit");
            System.out.println("Enter the maximum number of posts you would like to download so that the line looks similar to this:");
            System.out.println("    number=150");
            System.exit(0);
        }

        try {
            BufferedReader read = new BufferedReader(new FileReader(parameters));
            read.readLine();
            String line2 = read.readLine().substring(5);
            tags = line2;
            String line3 = read.readLine().substring(7);
            if(line3 != null && !line3.isEmpty()){
                number = Integer.valueOf(line3);
            }
        } catch(NullPointerException e){
            System.out.println("'parameters.txt' might be defect, please delete the file and rerun the program.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    //List of tags to retrieve posts from
    public String getTags(){
        return tags;
    }

    //Number of posts to retrieve
    public int getNumber(){
        return number;
    }
}
