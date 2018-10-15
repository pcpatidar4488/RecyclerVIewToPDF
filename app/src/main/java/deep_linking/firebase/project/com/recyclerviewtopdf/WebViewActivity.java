package deep_linking.firebase.project.com.recyclerviewtopdf;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import deep_linking.firebase.project.com.recyclerviewtopdf.new_pkg.BitmapGeneratingAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewActivity extends Activity implements BitmapGeneratingAsyncTask.Callback {
    WebView webView;
    RecyclerView mRecyclerView;
    StringBuilder htmlContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_preview);
        webView = findViewById(R.id.webView);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(getStorageDir() + "/Aaaa.png");
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        htmlContent = new StringBuilder();
        String html = getHtml("pdf_icon") ;


        new BitmapGeneratingAsyncTask(this, html, 150, this).execute();
        findViewById(R.id.print).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                createWebPrintJob(webView);
            }
        });
        findViewById(R.id.screen_shot).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                Picture picture = webView.capturePicture();
                Bitmap b = Bitmap.createBitmap(picture.getWidth(),
                        picture.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);
                picture.draw(c);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(getStorageDir() + "/screen_shot.png");
                    if (fos != null) {
                        b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                        Toast.makeText(WebViewActivity.this, "done!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }
        });

    }

    private static File getStorageDir() {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }
        return docsFolder;
    }

    void setAdapter() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        List<String> list = new ArrayList();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("G");
        list.add("H");
        list.add("I");
        list.add("J");
        list.add("K");
        list.add("L");
        list.add("M");
        list.add("N");
        list.add("O");
        list.add("P");
        list.add("Q");
        list.add("R");
        list.add("S");
        list.add("T");
        list.add("U");
        list.add("V");
        list.add("W");
        list.add("X");
        list.add("Y");
        list.add("Z");
        MyAdapter myAdapter = new MyAdapter(this, list);
        mRecyclerView.setAdapter(myAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
    }

    @Override
    public void done(Bitmap bitmap) {

        for (int i=0;i<3;i++) {
            try {
                int size = 0;
                InputStream is = getAssets().open("image_html.html");
                size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String imageString = new String(buffer);
                String dataURL = "data:image/png;base64," + BitMapToString(BitmapFactory.decodeResource(this.getResources(), R.drawable.pdf_icon));
                imageString = imageString.replace("{SIGNATURE_PLACEHOLDER}", dataURL);
                webView.loadDataWithBaseURL("file:///android_asset/", imageString, "text/html", "UTF-8", null);

            } catch (Exception e) {

            }
        }

        webView.loadDataWithBaseURL(null, htmlContent.toString(), "text/html", "utf-8", null);
    }


    String getHtml(String name) {
        return htmlContent.append(String.format("<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "<img src=\"   file:///android_asset/" + name + ".png\"/>\n" +
                "<img src=\"   file:///android_asset/" + name + ".png\"/>\n" +
                "<img src=\"   file:///android_asset/" + name + ".png\"/>\n" +
                "<img src='{SIGNATURE_PLACEHOLDER}'\n width=\"100\" height=\"70\">" +
                "</body>\n" +
                "</html>")).toString();
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
