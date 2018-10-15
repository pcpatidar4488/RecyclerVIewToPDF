package deep_linking.firebase.project.com.recyclerviewtopdf;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class WebViewHtmlActivity extends AppCompatActivity {
    WebView webView;

    String imageString = "";
    byte[] bufferChild ;

    String htmlContent="";

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_html);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        findViewById(R.id.print).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                createWebPrintJob(webView);
            }
        });

        try {
            int size = 0;
            InputStream is = getAssets().open("main.html");
            size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();


            for (int i=0;i<100;i++){
                int sizeChild = 0;
                InputStream isChild = getAssets().open("image_html.html");
                sizeChild = isChild.available();
                bufferChild = new byte[sizeChild];
                isChild.read(bufferChild);
                isChild.close();
                imageString = new String(bufferChild);
                String dataURL = "data:image/png;base64," + BitMapToString(BitmapFactory.decodeResource(this.getResources(), R.drawable.pdf_icon));
                imageString = imageString.replace("{SIGNATURE_PLACEHOLDER}", dataURL);
                htmlContent=htmlContent+imageString;
            }
        } catch (Exception e) {
            Toast.makeText(this, "catch", Toast.LENGTH_SHORT).show();
        }
        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null);
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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

}