package kr.foryou.ssum;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.buzzvil.buzzad.analytics.BATracker;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import util.BackPressCloseHandler;
import util.Common;
import util.NetworkCheck;
import util.RetrofitItem;
import util.RetrofitPushService;
import util.retrofit.RetrofitService;
import util.retrofit.ServerPost;
import util.retrofit.StaticRetrofit;

import static com.facebook.FacebookSdk.setAutoLogAppEventsEnabled;
import static com.facebook.appevents.AppEventsConstants.EVENT_PARAM_CONTENT;


public class MainActivity extends AppCompatActivity {
    private static final int APP_PERMISSION_STORAGE = 9787;
    LinearLayout webLayout;
    RelativeLayout networkLayout;
    public static WebView webView;
    NetworkCheck netCheck;
    Button replayBtn;
    ProgressBar loadingProgress;
    public static boolean execBoolean = true;
    private BackPressCloseHandler backPressCloseHandler;
    boolean isIndex = true;
    private final int AUDIO_RECORED_REQ_CODE=1500,SNS_REQ_CODE=2000;
    String firstUrl = "";
    final int REQUEST_IMAGE_CODE = 1010;
    Context mContext;
    Activity mActivity;
    private CustomDialog customDialog;
    private Uri cameraImageUri;
    public static String no;
    private WebView mWebviewPop;
    private FrameLayout mContainer;
    // 파일첨부 관련 변수
    final int FILECHOOSER_NORMAL_REQ_CODE = 1200,FILECHOOSER_LOLLIPOP_REQ_CODE=1300;
    ValueCallback<Uri> filePathCallbackNormal;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    Uri mCapturedImageURI;
    AppEventsLogger logger;
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logAddToCartEvent (String contentData, String contentId, String contentType, String currency, double price) {
        Bundle params = new Bundle();
        params.putString(EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, price, params);
    }
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logSearchEvent (String contentType, String contentData, String contentId, String searchString, boolean success) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_SEARCH_STRING, searchString);
        params.putInt(AppEventsConstants.EVENT_PARAM_SUCCESS, success ? 1 : 0);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, params);
    }
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logCompleteRegistrationEvent (String registrationMethod) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, registrationMethod);
        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, params);
    }
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logViewContentEvent (String contentType, String contentData, String contentId, String currency, double price) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, price, params);
    }
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logAchieveLevelEvent (String level) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_LEVEL, level);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ACHIEVED_LEVEL, params);
    }
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logUnlockAchievementEvent (String description) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, description);
        logger.logEvent(AppEventsConstants.EVENT_NAME_UNLOCKED_ACHIEVEMENT, params);
    }
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logSpendCreditsEvent (String contentData, String contentId, String contentType, double totalValue) {
        Bundle params = new Bundle();
        params.putString(EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SPENT_CREDITS, totalValue, params);
    }
    void onAction() {

        // 액션 완료시 호출!
        BATracker.actionCompleted(this);
    }
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logAddToWishlistEvent (String contentData, String contentId, String contentType, String currency, double price) {
        Bundle params = new Bundle();
        params.putString(EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, price, params);
    }
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logAddPaymentInfoEvent (boolean success) {
        Bundle params = new Bundle();
        params.putInt(AppEventsConstants.EVENT_PARAM_SUCCESS, success ? 1 : 0);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_PAYMENT_INFO, params);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BATracker.init(this, "553029962520355");
        BATracker.actionCompleted(this);
        onAction();

        //페이스북  광고
        setAutoLogAppEventsEnabled(true);
        logger = AppEventsLogger.newLogger(this);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ACTIVATED_APP);
        FacebookSdk.sdkInitialize(getApplicationContext()); AppEventsLogger.activateApp(this);

        logAddToCartEvent(EVENT_PARAM_CONTENT,AppEventsConstants.EVENT_PARAM_CONTENT_ID,AppEventsConstants.EVENT_PARAM_CONTENT_TYPE,AppEventsConstants.EVENT_PARAM_CURRENCY,0);
        logSearchEvent(EVENT_PARAM_CONTENT,AppEventsConstants.EVENT_PARAM_CONTENT_ID,AppEventsConstants.EVENT_PARAM_CONTENT_TYPE,AppEventsConstants.EVENT_PARAM_SEARCH_STRING,Boolean.parseBoolean(AppEventsConstants.EVENT_PARAM_SUCCESS));
        logCompleteRegistrationEvent(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD);
        logViewContentEvent(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, EVENT_PARAM_CONTENT,AppEventsConstants.EVENT_PARAM_CONTENT_ID,AppEventsConstants.EVENT_PARAM_CURRENCY,0);
        logAchieveLevelEvent(AppEventsConstants.EVENT_PARAM_LEVEL);
        logUnlockAchievementEvent(AppEventsConstants.EVENT_PARAM_DESCRIPTION);
        logSpendCreditsEvent(EVENT_PARAM_CONTENT,AppEventsConstants.EVENT_PARAM_CONTENT_ID,AppEventsConstants.EVENT_PARAM_CONTENT_TYPE,0);
        logAddToWishlistEvent(EVENT_PARAM_CONTENT,AppEventsConstants.EVENT_PARAM_CONTENT_ID,AppEventsConstants.EVENT_PARAM_CONTENT_TYPE,AppEventsConstants.EVENT_PARAM_CURRENCY,0);
        logAddPaymentInfoEvent(true);


        //AppEventsLogger.newLogger();
        //logger.logPurchase(, AppEventsConstants.EVENT_PARAM_CURRENCY, EVENT_PARAM_CONTENT);






        MyFirebaseMessagingService.identity=0;
        mContext=this;
        mActivity=this;
        mContainer=(FrameLayout)findViewById(R.id.mContainer);

        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("kr.foryou.ssum");
        FirebaseInstanceId.getInstance().getToken();
       
        Intent intent = getIntent();
        if(Common.getPref(this,"ss_mb_id","").equals("")||Common.getPref(this,"ss_mb_id","").equals(null)) {
            firstUrl = getString(R.string.url);
        }else{
            firstUrl = getString(R.string.url2);
        }
        CookieSyncManager.createInstance(this);
        try{
            if(!intent.getExtras().getString("goUrl").equals("")){
                firstUrl =intent.getExtras().getString("goUrl");
            }
        }catch(Exception e){

        }
        try {
            if (Common.TOKEN.equals("") || Common.TOKEN.equals(null)) {
                refreshToken();
            } else {
                //postPush();
            }
        }catch (Exception e){
            refreshToken();
        }
        Log.d("TOKEN",Common.TOKEN);
        setLayout();
    }
    private void refreshToken(){
        FirebaseMessaging.getInstance().subscribeToTopic("ssum");
        Common.TOKEN= FirebaseInstanceId.getInstance().getToken();
        //postPush();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void postPush(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(getString(R.string.domain))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //서버에 보낼 파라미터
        Map map=new HashMap();
        map.put("token",Common.TOKEN);
        map.put("mb_id",Common.getPref(this,"ss_mb_id",""));
        map.put("DeviceID",Common.getMyDeviceId(this));


        RetrofitPushService retrofitService=retrofit.create(RetrofitPushService.class);
        Call<RetrofitItem> call=retrofitService.getPush(map);

        call.enqueue(new Callback<RetrofitItem>() {
            @Override
            public void onResponse(Call<RetrofitItem> call, Response<RetrofitItem> response) {
                //서버에 데이터 받기가 성공할시
                if(response.isSuccessful()){

                }else{

                }
            }
            //데이터 받기가 실패할 시
            @Override
            public void onFailure(Call<RetrofitItem> call, Throwable t) {

            }
        });
    }
    //레이아웃 설정
    public void setLayout() {
        networkLayout = (RelativeLayout) findViewById(R.id.networkLayout);//네트워크 연결이 끊겼을 때 레이아웃 가져오기
        webLayout = (LinearLayout) findViewById(R.id.webLayout);//웹뷰 레이아웃 가져오기
        loadingProgress = (ProgressBar)findViewById(R.id.loadingProgress);
        mContainer=(FrameLayout)findViewById(R.id.mContainer);

        webView = (WebView) findViewById(R.id.webView);//웹뷰 가져오기
        webView.loadUrl(firstUrl);
        webViewSetting();
        customDialog=new CustomDialog(this,galleryOnClickListener,photoOnClickListener);
        WindowManager.LayoutParams params = customDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        customDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams)params);
    }
    //갤러리 온클릭리스너 만들기
    View.OnClickListener galleryOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "클릭", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, AlbumSelectActivity.class);
//set limit on number of images that can be selected, default is 10
            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 1);
            startActivityForResult(intent, Constants.REQUEST_CODE);
            customDialog.dismiss();
        }
    };
    //사진찍기 온클릭 리스너 만들기
    View.OnClickListener photoOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "mb_photo.jpg"));
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,cameraImageUri);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CODE);
                customDialog.dismiss();
            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR_MR1)
    public void webViewSetting() {

        WebSettings setting = webView.getSettings();//웹뷰 세팅용

        setting.setAllowFileAccess(true);//웹에서 파일 접근 여부
        setting.setAppCacheEnabled(true);//캐쉬 사용여부
        setting.setGeolocationEnabled(true);//위치 정보 사용여부
        setting.setDatabaseEnabled(true);//HTML5에서 db 사용여부
        setting.setDomStorageEnabled(true);//HTML5에서 DOM 사용여부
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);//캐시 사용모드 LOAD_NO_CACHE는 캐시를 사용않는다는 뜻
        setting.setJavaScriptEnabled(true);//자바스크립트 사용여부

        setting.setMediaPlaybackRequiresUserGesture(false);//웹에서 미디어 컨트롤 풀기

        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setSupportMultipleWindows(true);//윈도우 창 여러개를 사용할 것인지의 여부 무조건 false로 하는 게 좋음
        setting.setUseWideViewPort(true);//웹에서 view port 사용여부
        webView.setWebChromeClient(chrome);//웹에서 경고창이나 또는 컴펌창을 띄우기 위한 메서드
        webView.setWebViewClient(client);//웹페이지 관련된 메서드 페이지 이동할 때 또는 페이지가 로딩이 끝날 때 주로 쓰임
        setting.setUserAgentString("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Mobile Safari/537.36");
        if (Build.VERSION.SDK_INT >= 21) {
            setting.setMixedContentMode( WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        webView.addJavascriptInterface(new WebJavascriptEvent(), "Android");
        webView.addJavascriptInterface(new WebJavascriptEvent(),"android");

        //네트워크 체킹을 할 때 쓰임
        netCheck = new NetworkCheck(this, this);
        netCheck.setNetworkLayout(networkLayout);
        netCheck.setWebLayout(webLayout);
        netCheck.networkCheck();
        //뒤로가기 버튼을 눌렀을 때 클래스로 제어함
        backPressCloseHandler = new BackPressCloseHandler(this);

        replayBtn=(Button)findViewById(R.id.replayBtn);
        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netCheck.networkCheck();
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            setting.setMixedContentMode( WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
        }
    }

    WebChromeClient chrome;

    {
        chrome = new WebChromeClient() {
            //새창 띄우기 여부
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                mWebviewPop = new WebView(mContext);
                mWebviewPop.setVerticalScrollBarEnabled(false);
                mWebviewPop.setHorizontalScrollBarEnabled(false);
                mWebviewPop.setWebViewClient(client);
                mWebviewPop.setWebChromeClient(chrome);
                mWebviewPop.getSettings().setJavaScriptEnabled(true);
                mWebviewPop.getSettings().setSavePassword(false);
                mWebviewPop.clearHistory();
                mWebviewPop.clearFormData();
                mWebviewPop.clearCache(true);
                mWebviewPop.getSettings().setUserAgentString( "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
                mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mContainer.addView(mWebviewPop);
                mContainer.setVisibility(View.VISIBLE);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(mWebviewPop);
                resultMsg.sendToTarget();

                return true;


            }
            @Override
            public void onCloseWindow(WebView window) {
                Log.d("onCloseWindow", "called");
            }


            //경고창 띄우기
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("\n" + message + "\n")
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                }).create().show();
                return true;
            }

            //컴펌 띄우기
            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("\n" + message + "\n")
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.cancel();
                                    }
                                }).create().show();
                return true;
            }

            //현재 위치 정보 사용여부 묻기
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // Should implement this function.
                final String myOrigin = origin;
                final GeolocationPermissions.Callback myCallback = callback;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Request message");
                builder.setMessage("현재 위치를 좌표를 받으시겠습니까?");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        myCallback.invoke(myOrigin, true, false);
                    }

                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        myCallback.invoke(myOrigin, false, false);
                    }

                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                filePathCallbackNormal = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_NORMAL_REQ_CODE);
            }

            // For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

            // For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (filePathCallbackLollipop != null) {
//                    filePathCallbackLollipop.onReceiveValue(null);
                    filePathCallbackLollipop = null;
                }
                filePathCallbackLollipop = filePathCallback;


                // Create AndroidExampleFolder at sdcard
                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs();
                }

                // Create camera captured image file path and name
                File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                mCapturedImageURI = Uri.fromFile(file);

                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");

                // Create file chooser intent
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                // Set camera intent to file chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                // On select image call onActivityResult method of activity
                startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
                return true;
            }
        };
    }


    WebViewClient client;
    {
        client = new WebViewClient() {
            //페이지 로딩중일 때 (마시멜로) 6.0 이후에는 쓰지 않음
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadingProgress.setVisibility(View.VISIBLE);
                Log.d("url",url);
                String host=Uri.parse(url).getHost();
                if( url.startsWith("http://") || url.startsWith("https://") ) {


                    if (host.equals("www.xn--c04b.com")) {
                        if (mWebviewPop != null) {
                            mWebviewPop.setVisibility(View.GONE);
                            mContainer.removeView(mWebviewPop);
                            mWebviewPop = null;
                        }
                        return false;
                    }

                    if (host.equals("m.facebook.com") || host.equals("www.facebook.com") || host.equals("facebook.com")) {

                        return false;
                    }
                    if(url.startsWith("https://open")){
                        loadingProgress.setVisibility(View.GONE);
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);
                        return true;
                    }else if(url.startsWith("http://pf.kakao.com/")){
                        loadingProgress.setVisibility(View.GONE);
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);
                        return true;
                    }else if(url.startsWith("https://open")){
                        loadingProgress.setVisibility(View.GONE);
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);
                        return true;
                    }else{
                        if (host.equals("www.xn--c04b.com")) {
                            return false;
                        }else{
                            loadingProgress.setVisibility(View.GONE);
                            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            webView.loadUrl("javascript:history.back();");
                            return false;

                        }
                    }

                }else if (url.startsWith("tel")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    loadingProgress.setVisibility(View.GONE);
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.


                        }
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }else if(url.startsWith("market://")){
                    try {
                        loadingProgress.setVisibility(View.GONE);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=kr.foryou.ssum"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivity(intent);
                        return true;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if (url.startsWith("intent:")) {
                    loadingProgress.setVisibility(View.GONE);
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                        if (existPackage != null) {
                            getBaseContext().startActivity(intent);
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                            startActivity(marketIntent);
                        }
                        return true;
                    } catch (Exception e) {
                        Log.d("error1",e.toString());
                        e.printStackTrace();
                    }
                }


                return true;
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.d("onReceivedSslError", "onReceivedSslError");
                //super.onReceivedSslError(view, handler, error);
            }
            //페이지 로딩이 다 끝났을 때
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //webLayout.setRefreshing(false);
                loadingProgress.setVisibility(View.GONE);

                Log.d("url",url);
                Log.d("ss_mb_id", Common.getPref(getApplicationContext(),"ss_mb_id",""));
                if(url.startsWith("https://m.facebook.com/v3.1/dialog/oauth")){
                    if(mWebviewPop!=null)
                    {
                        mContainer.setVisibility(View.GONE);
                        mWebviewPop.setVisibility(View.GONE);
                        mWebviewPop=null;
                    }
                    view.loadUrl("https://www.xn--c04b.com:44536/");
                    return;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.getInstance().sync();
                } else {
                    CookieManager.getInstance().flush();
                }

                //로그인할 때
                if(url.startsWith(getString(R.string.domain)+"bbs/login.php")){
                    view.loadUrl("javascript:fcmKey('"+Common.TOKEN+"')");
                }
                if (url.equals(getString(R.string.url)) || url.equals(getString(R.string.domain))||url.equals(getString(R.string.url2))) {
                    isIndex=true;
                } else {
                    isIndex=false;
                }
                String id = Common.getPref(MainActivity.this,"ss_mb_id","");
                String Allcookie = CookieManager.getInstance().getCookie(url);
                int first = 0;
                int last = 0;
                String mb_idStr="";
                if(Allcookie!=null) {
                    first=Allcookie.lastIndexOf("mb_id=");
                    last = Allcookie.length();
                    try {
                        mb_idStr= Allcookie.substring(first, last);
                    }catch (Exception   e){

                    }

                }
                Log.d("first",first+"");
                if(0<=first) {




                    int first1 = mb_idStr.indexOf("=") + 1;
                    int last1 = mb_idStr.indexOf(";");

                    if (last1 < first1) {
                        last1 = mb_idStr.length();
                    }

                    String mb_id = mb_idStr.substring(first1, last1);
                    Log.d("cookie111", mb_id);
                    CookieManager.getInstance().setAcceptCookie(true);

                    Common.savePref(MainActivity.this, "ss_mb_id", mb_id);
                    if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_PHONE_STATE)) {
                            Toast.makeText(MainActivity.this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                        }else{
                            requestPermissions(new String[]{
                                            android.Manifest.permission.READ_PHONE_STATE
                                    },
                                    APP_PERMISSION_STORAGE);
                        }
                    }else {
                        postPush();
                    }
                }else{
                    Common.savePref(MainActivity.this, "ss_mb_id", "");
                }
                super.onPageFinished(view, url);


            }
            //페이지 오류가 났을 때 6.0 이후에는 쓰이지 않음
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //super.onReceivedError(view, request, error);
                //view.loadUrl("");
                //페이지 오류가 났을 때 오류메세지 띄우기
                /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setMessage("네트워크 상태가 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
                builder.show();*/
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    //쿠키 값 삭제
    public void deleteCookie(){
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(webView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        cookieSyncManager.sync();
    }
    //다시 들어왔을 때
    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().startSync();
        }*/
        CookieSyncManager.getInstance().startSync();
        execBoolean=true;
        Log.d("newtork","onResume");
        try{
            Intent intent=getIntent();
            Uri data=intent.getData();
            Log.d("data111",data.toString());
        }catch (Exception e){

        }

        //netCheck.networkCheck();
    }
    //홈버튼 눌러서 바탕화면 나갔을 때
    @Override
    protected void onPause() {
        super.onPause();
        webView.pauseTimers();
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().stopSync();
        }*/
        CookieSyncManager.getInstance().stopSync();

        execBoolean=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        netCheck.stopReciver();
       // unregisterReceiver(receiver);


    }
    //뒤로가기를 눌렀을 때
    public void onBackPressed() {
        //super.onBackPressed();
        //웹뷰에서 히스토리가 남아있으면 뒤로가기 함
        Log.d("isIndex",isIndex+"");
        if(mContainer.getVisibility()==View.VISIBLE){
            mContainer.setVisibility(View.GONE);
        }else {
            if (!isIndex) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else if (webView.canGoBack() == false) {
                    backPressCloseHandler.onBackPressed();
                }
            } else {
                backPressCloseHandler.onBackPressed();
            }
        }
    }
    //로그인 로그아웃
    class WebJavascriptEvent{


        @JavascriptInterface
        public void setLogin(String mb_id){
            Log.d("login","로그인");
            Common.savePref(getApplicationContext(),"ss_mb_id",mb_id);
        }
        @JavascriptInterface
        public void setLogout(){
            Log.d("logout","로그아웃");
            Common.savePref(getApplicationContext(),"ss_mb_id","");
        }
        @JavascriptInterface
        public void soundRecord(){
            Intent intent=new Intent(getApplicationContext(),RecordActivity.class);
            startActivityForResult(intent,AUDIO_RECORED_REQ_CODE);
        }
        @JavascriptInterface
        public void getPhoto(String no){
            MainActivity.no=no;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    customDialog.show();
                }
            });
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        @JavascriptInterface
        public void setSms() {
            if(checkSelfPermission(Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED){
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.RECEIVE_SMS)) {
                    Toast.makeText(MainActivity.this, "설정 애플리케이션에서 권한설정을 하십시오.", Toast.LENGTH_SHORT).show();
                }else{
                    requestPermissions(new String[]{
                                    android.Manifest.permission.RECEIVE_SMS,
                                    android.Manifest.permission.READ_SMS
                            },
                            APP_PERMISSION_STORAGE);
                }

            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

                    }
                });
            }
        }
        @JavascriptInterface
        public void stopReceiver(){
            Log.d("stop","STOP하기");

        }
        @JavascriptInterface
        public void hiddenContainer(){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==AUDIO_RECORED_REQ_CODE) {
            //오디오 녹음이 끝났을 때 서버로 업로드 하기
            if(resultCode==RESULT_OK){
                Uri filePath=Uri.parse(data.getStringExtra("filePath"));
                serverPost(filePath,"audio");

            }
        }else if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images =  data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            Uri imageUri=Uri.parse(images.get(0).path);
            if(MainActivity.no.equals("1")) {
                serverPost(imageUri,"photo1");
            }else{
                serverPost(imageUri,"photo2");
            }
        }else if(requestCode==REQUEST_IMAGE_CODE && resultCode == RESULT_OK){
            if(MainActivity.no.equals("1")) {
                serverPost(cameraImageUri,"photo1");
            }else{
                serverPost(cameraImageUri,"photo2");
            }

        }else if (requestCode == FILECHOOSER_NORMAL_REQ_CODE) {
            if (filePathCallbackNormal == null) return;
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            filePathCallbackNormal.onReceiveValue(result);
            filePathCallbackNormal = null;

        } else if (requestCode == FILECHOOSER_LOLLIPOP_REQ_CODE) {
            Uri[] result = new Uri[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(resultCode == RESULT_OK){
                    result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                }
                filePathCallbackLollipop.onReceiveValue(result);
            }
        }


            /*if (requestCode == FILECHOOSER_NORMAL_REQ_CODE) {
            if (filePathCallbackNormal == null) return;
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            filePathCallbackNormal.onReceiveValue(result);
            filePathCallbackNormal = null;
            if(MainActivity.no.equals("1")) {
                serverPost(result, "photo1");
            }else{
                serverPost(result, "photo2");
            }
        } else if (requestCode == FILECHOOSER_LOLLIPOP_REQ_CODE) {
            Uri[] result = new Uri[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if(resultCode == RESULT_OK){
                    result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                }
                if(MainActivity.no.equals("1")) {
                    serverPost(result[0], "photo1");
                }else{
                    serverPost(result[0], "photo2");
                }
                filePathCallbackLollipop.onReceiveValue(result);

            }
        }*/
    }

    public void serverPost(Uri fileUri, final String type){
        //httpok 로그 보기
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //클라이언트 설정
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();
        //레트로핏 설정
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(getString(R.string.domain))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //파라미터 넘길 값 설정
        Map<String, RequestBody> map=new HashMap<>();
        map.put("division", StaticRetrofit.toRequestBody("file_upload"));

        Log.d("filePath",fileUri.getPath());

        File file = new File(fileUri.getPath()); // 이미지파일주소는 확인됨
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        if(type.equals("audio")) {
            map.put("file\"; filename=\"audio.mp4", fileBody);
        }else{
            map.put("file\"; filename=\"mb_photo.jpg", fileBody);
        }



        //레트로핏 서비스 실행하기
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        //데이터 불러오기
        Call<ServerPost> call=retrofitService.FileUpload(map);
        call.enqueue(new Callback<ServerPost>() {
            @Override
            public void onResponse(Call<ServerPost> call, Response<ServerPost> response) {
                if(response.isSuccessful()){
                    ServerPost repo=response.body();
                    Log.d("response",response+"");
                    if(Boolean.parseBoolean(repo.getSuccess())==false){
                    }else{

                        String file_url=repo.getFileUrl().replace("\\","");
                        if(type.equals("audio")){
                            webView.loadUrl("javascript:audioShow('"+file_url+"');");
                        }else{
                            webView.loadUrl("javascript:photoShow('"+file_url+"','"+type+"')");
                        }
                    }
                }else{
                }
            }

            @Override
            public void onFailure(Call<ServerPost> call, Throwable t) {
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case APP_PERMISSION_STORAGE:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

                        }
                    });
                }else{


                    //startActivity(intent);

                }
                break;
        }
    }
}