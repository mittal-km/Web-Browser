package com.example.km.web_browser;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ProgressBar pgb;
    ImageView imgv;
    WebView webv;
    LinearLayout linearlayout;
    EditText input_URL;
    Button visit_URL;
    SwipeRefreshLayout swipeRefreshLayout;
    String myCurrentURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Initialisation
        pgb = (ProgressBar)findViewById(R.id.myProgressBar);
        imgv = (ImageView)findViewById(R.id.myImgView);
        webv = (WebView)findViewById(R.id.myWebView);
        linearlayout = (LinearLayout)findViewById(R.id.linear_layout);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.mySwipeLayout);
        input_URL = (EditText)findViewById(R.id.inputURL);
        visit_URL = (Button)findViewById(R.id.visitURL);

        WebSettings webSettings = webv.getSettings();
        pgb.setMax(100);
        onVisitURLPressed();

        webv.loadUrl("https://www.google.com");

        if(savedInstanceState != null){
            webv.restoreState(savedInstanceState);
        }
        else {
            webv.getSettings().setJavaScriptEnabled(true);
            webv.getSettings().setLoadWithOverviewMode(true);
        }

        //Improve WebView Performance
        webSettings.setSupportZoom(true);
        webv.getSettings().setLoadsImagesAutomatically(true);
        webv.getSettings().setBuiltInZoomControls(true);                             //Zoom Control
        webv.getSettings().setDisplayZoomControls(true);
        webv.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webv.getSettings().setAppCacheEnabled(true);
        webv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webv.getSettings().setAppCacheEnabled(false);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setDatabaseEnabled(true);
        webv.getSettings().setPluginState(WebSettings.PluginState.ON);


        webv.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                linearlayout.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                linearlayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                myCurrentURL = url;
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webv.loadUrl(url);
                CookieManager.getInstance().setAcceptCookie(true);
                return true;
            }

        });

        webv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pgb.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                imgv.setImageBitmap(icon);
            }
        });

        //Download Manager to enable download of files
        webv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                DownloadManager.Request myRequest = new DownloadManager.Request(Uri.parse(s));
                myRequest.allowScanningByMediaScanner();
                myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager myManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                myManager.enqueue(myRequest);

                Toast.makeText(MainActivity.this, "Download Started",Toast.LENGTH_SHORT);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webv.reload();
            }
        });

    }

    public void onVisitURLPressed(){
        visit_URL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide Keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                webv = (WebView)findViewById(R.id.myWebView);
                String URL = "https://" + input_URL.getText().toString();
                webv.loadUrl(URL);
                input_URL.setText("");
                input_URL.clearFocus();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.super_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webv.saveState(outState);
    }

    //Menu Options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_back:
                onBackPressed();
                break;

            case R.id.menu_forward:
                onForwardPressed();
                break;

            case R.id.menu_refresh:
                webv.reload();
                break;

            case R.id.home:
                webv.loadUrl("https://www.google.com");
                input_URL.setText("");
                break;

            case R.id.clear_data:
                clearData();
                Toast.makeText(MainActivity.this, "History Cleared", Toast.LENGTH_SHORT).show();
                break;

            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey, Check this out: \n"+myCurrentURL);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Title: ");
                startActivity(Intent.createChooser(shareIntent,"Share URL"));
                break;

            case R.id.exit:
                onExitPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onForwardPressed(){
        if(webv.canGoForward()){
            webv.goForward();
        }
        else{
            Toast.makeText(this, "Can't Go Further" , Toast.LENGTH_SHORT).show();
        }
    }

    private void clearData(){
        webv.clearCache(true);
        webv.clearHistory();
        webv.clearFormData();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    private void onExitPressed(){
        Toast.makeText(this, "Internet Khatam BC?", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed(){
        if(webv.canGoBack()){
            webv.goBack();
        }
        else{
            finish();
        }
    }


}
