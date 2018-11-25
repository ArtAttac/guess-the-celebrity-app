package com.arthuritzhaki.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();

    String[] newAnswers = new String[4];
    int locationofCorrectAnswer = 0;

    int chosenCeleb = 0;

    ImageView imageView;

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChosen(View view){

        if (view.getTag().toString().equals(Integer.toString(locationofCorrectAnswer))){

            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();

        }else{

            Toast.makeText(getApplicationContext(), "Wrong, it was " + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();

        }

        newQuestion();

    }

    public void newQuestion(){


        try {
            //Allows a random celeb pic to be picked
            Random rand = new Random();

            chosenCeleb = rand.nextInt(celebURLs.size());

            ImageDownloader imageTask = new ImageDownloader();

            Bitmap celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();


            imageView.setImageBitmap(celebImage);

            //creating the answers
            locationofCorrectAnswer = rand.nextInt(4);
            int inCorrectAnswerLocation;

            for (int i = 0; i < 4; i++) {

                if (i == locationofCorrectAnswer) {

                    newAnswers[i] = celebNames.get(chosenCeleb);


                } else {
                    inCorrectAnswerLocation = rand.nextInt(celebURLs.size());

                    while (inCorrectAnswerLocation == chosenCeleb) {

                        inCorrectAnswerLocation = rand.nextInt(celebURLs.size());

                    }

                    newAnswers[i] = celebNames.get(inCorrectAnswerLocation);
                }

            }


            button0.setText(newAnswers[0]);
            button1.setText(newAnswers[1]);
            button2.setText(newAnswers[2]);
            button3.setText(newAnswers[3]);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        button0 = findViewById(R.id.button1);
        button1 = findViewById(R.id.button2);
        button2 = findViewById(R.id.button3);
        button3 = findViewById(R.id.button4);

        DownloadTask task = new DownloadTask();
        String result = null;
        try{
            //Contents of the URL are stored in result.
            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class =\"listedArticles\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()){
                celebURLs.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()){
                celebNames.add(m.group(1));
            }




        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                //Creates a string array full of URLs of picture URLs (downloading an image)
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;


            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }




    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        //change string to urls
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }
}
