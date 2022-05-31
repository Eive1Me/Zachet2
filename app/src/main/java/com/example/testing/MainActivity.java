package com.example.testing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import org.beyka.tiffbitmapfactory.CompressionScheme;
import org.beyka.tiffbitmapfactory.Orientation;
import org.beyka.tiffbitmapfactory.TiffBitmapFactory;
import org.beyka.tiffbitmapfactory.TiffConverter;
import org.beyka.tiffbitmapfactory.TiffSaver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public void threadOpen(){
        FileInputStream fileos = null;
        ImageView iv = findViewById(R.id.imageView2);
        try{
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }
        catch (Exception e) {
            System.out.println("Err" + e);
        }
    }

    public class SaveAsynk extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            ImageView iv = findViewById(R.id.imageView2);
            FileOutputStream fOut = null;
            try{
                try {
                    Switch s = findViewById(R.id.switch1);
                    String s1 = "bmp";
                    if (s.isChecked())
                    {
                        s1 = "tiff";
                        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        TiffSaver.SaveOptions options = new TiffSaver.SaveOptions();
                        options.compressionScheme = CompressionScheme.NONE;
                        options.orientation = Orientation.TOP_LEFT;
                        options.author = "beyka";
                        options.copyright = "Some copyright";
                        TiffSaver.saveBitmap(Environment.getExternalStorageDirectory() + "/image.tif", bitmap, options);
                    }else {
                        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        TiffSaver.SaveOptions options = new TiffSaver.SaveOptions();
                        options.compressionScheme = CompressionScheme.NONE;
                        options.orientation = Orientation.TOP_LEFT;
                        options.author = "beyka";
                        options.copyright = "Some copyright";
                        TiffSaver.saveBitmap(Environment.getExternalStorageDirectory() + "/imageTmp.tif", bitmap, options);
                        TiffConverter.ConverterOptions optionsC = new TiffConverter.ConverterOptions();
                        optionsC.throwExceptions = false;
                        optionsC.availableMemory = 128 * 1024 * 1024;
                        optionsC.readTiffDirectory = 1;
                        TiffConverter.convertTiffBmp(Environment.getExternalStorageDirectory() + "/imageTmp.tif", Environment.getExternalStorageDirectory() + "/image.bmp", optionsC, null);
//                    File myFile = new File(Environment.getExternalStorageDirectory(), "image." + s1);
//                    System.out.println(Environment.getExternalStorageDirectory());
//                    myFile.createNewFile();
//                    BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
//                    Bitmap bitmap = drawable.getBitmap();
//                    fOut = new FileOutputStream(myFile);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fOut != null) {
                        fOut.close();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            File file = new File(Environment.getExternalStorageDirectory() + "/imageTmp.tif");
            file.delete();
            Toast.makeText(getBaseContext(),"Сохранено",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }
//        Thread thread = new Thread () {
//            public void run() {
//                String PATH=null ;
//                FileOutputStream fileos = null;
//                try{
//                    if(fileos==null)
//                    {
//                        // If the file don't exists
//                        File file = new File(Environment.getExternalStorageDirectory().toString()+"/xml_nova_contagem.xml" );
//                        fileos = new FileOutputStream(file);
//
//                    }
//                    // If the file exists
//
//
//                    /////////-------- THIS METHOD WON'T WORK --------///////
//
//
//                                    /*  if (fileos!=null){
//
//
//                                           System.out.println("aqui vai dar bronca: " + Environment.getExternalStorageDirectory().toString());
//
//
//                                    //     PATH = Environment.getExternalStorageDirectory().toString() + "/xml_nova_contagem.xml";
//                                           Context context= getApplicationContext();
//
//                                           fileos = context.openFileOutput("xml_nova_contagem.xml", Context.MODE_APPEND);
//
//                                           System.out.println("okkkkkkkkk ");
//                                            }
//                                             */
//                }
//                catch (Exception e) {
//                    System.out.println("Erro a escrever"+e);
//                    System.out.println("path"+PATH);
//                }
//            }
//        };
//        thread.start();
    }

    public void open(View view){
        threadOpen();
    }

    public void save(View view) {
        new SaveAsynk().execute();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                ImageView selectedImagePreview = findViewById(R.id.imageView2);
                Uri selectedImageUri = data.getData();
                selectedImagePreview.setImageURI(selectedImageUri);
            }
        }
    }
}