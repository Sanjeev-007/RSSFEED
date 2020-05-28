package com.example.rssfeed;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
ListView lvrss;
ArrayList<String> titles,links;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvrss=(ListView)findViewById(R.id.lvrss);
        titles=new ArrayList<String>();
        links=new ArrayList<String>();
        lvrss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri=Uri.parse(links.get(position));
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        new progressbackrng().execute();
    }
    public InputStream getipstrm(URL url){
        try {
            return url.openConnection().getInputStream();
        }catch (IOException e){
            return  null;
        }
    }
    public class progressbackrng extends AsyncTask<Integer,Void,Exception>{
        Exception exception=null;
       ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        @Override
        protected Exception doInBackground(Integer... integers) {
            try {
                URL url=new URL("https://timesofindia.indiatimes.com/rssfeeds/66949542.cms");
                XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xp=factory.newPullParser();
                xp.setInput(getipstrm(url),"UTF_8");
                boolean insde=false;
                int type=xp.getEventType();
                while(type!= XmlPullParser.END_DOCUMENT){
                    if(type==XmlPullParser.START_TAG){
                        if(xp.getName().equalsIgnoreCase("item")){
                            insde=true;
                        }else if(xp.getName().equalsIgnoreCase("title")){
                            if(insde){
                                titles.add(xp.nextText());
                            }
                        }else if(xp.getName().equalsIgnoreCase("link")){
                            if(insde){
                                links.add(xp.nextText());
                            }
                        }
                    }else if(type==XmlPullParser.END_TAG && xp.getName().equalsIgnoreCase("item")){
                        insde=false;
                    }
                    type=xp.next();
                }
            }catch (MalformedInputException e){
            exception=e;
            }catch (XmlPullParserException e){
                exception=e;
            }catch (IOException e){
                exception=e;
            }



            return exception;
        }

        @Override
        protected void onPostExecute(Exception   s) {
            super.onPostExecute(s);
            ArrayAdapter<String> adapter=new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,titles);
            lvrss.setAdapter(adapter);


            Toast.makeText(MainActivity.this, "visible?", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("loading page re");
            progressDialog.show();
        }
    }
}
