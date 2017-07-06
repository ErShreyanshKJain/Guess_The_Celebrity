package com.example.jains.guess_the_person;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
//    Button option1,option2,option3,option4;
    String nameSrc;
    int rightChoice;
    ArrayList<String> CelebNames,CelebPics;
    ArrayList<Button> options;

    public class DownloadImage extends AsyncTask<String ,Void ,Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... urls) {

            Bitmap result;
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url =new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();

                urlConnection.connect();

                InputStream in = urlConnection.getInputStream();
                Bitmap myImage = BitmapFactory.decodeStream(in);

                return myImage;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadContent extends AsyncTask<String ,Void ,String >
    {
        @Override
        protected String doInBackground(String... urls) {

            try {

                String result = "";
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

                urlConnection.connect();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Pattern name,image;
        Matcher mNames,mImages;
        CelebNames = new ArrayList<>();
        CelebPics = new ArrayList<>();
        DownloadContent taskNames = new DownloadContent();
        String result;


        try {
            result = taskNames.execute("http://www.bbc.com/news/entertainment-arts-40248415").get();

            name = Pattern.compile("data-alt=\"(.*?)\" data");
            mNames = name.matcher(result);

            image = Pattern.compile(" data-src=\"(.*?)\" data");
            mImages = image.matcher(result);

            while (mNames.find() && mImages.find())
            {
                CelebNames.add(mNames.group(1));
                CelebPics.add(mImages.group(1));
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }

        imageView = (ImageView)findViewById(R.id.imageView);
        Button op1 = (Button)findViewById(R.id.option1);
        Button op2 = (Button)findViewById(R.id.option2);
        Button op3 = (Button)findViewById(R.id.option3);
        Button op4 = (Button)findViewById(R.id.option4);

        options= new ArrayList<>();
        options.add(op1);
        options.add(op2);
        options.add(op3);
        options.add(op4);

        generateGame();


    }
    public void generateGame()
    {
        Random random = new Random();
        int count = random.nextInt(10);
        String imgUrl = CelebPics.get(count);
        nameSrc = CelebNames.get(count);
        DownloadImage taskImage = new DownloadImage();
        int d=0;

        for(int i=0;i<4;i++)
        {
            int c = random.nextInt(10);

            if(c!=d)
                options.get(i).setText(CelebNames.get(random.nextInt(10)));

            d=c;
        }
        rightChoice = random.nextInt(4);
        options.get(rightChoice).setText(nameSrc);

        try
        {
            Bitmap imgSrc = taskImage.execute(imgUrl).get();
            imageView.setImageBitmap(imgSrc);
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }

        //return count;



    }
    public void optionClicked(View view)
    {
        if(Integer.parseInt(view.getTag().toString())==rightChoice)
        {
            Toast.makeText(getApplicationContext(),"Your Right",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"The Correct answer was"+nameSrc,Toast.LENGTH_SHORT).show();
        }
        generateGame();
    }
    /*public void option2Clicked(View view)
    {
        generateGame(2);
    }
    public void option3Clicked(View view)
    {
        generateGame(3);
    }
    public void option4Clicked(View view)
    {
        generateGame(4);
    }
*/

}
