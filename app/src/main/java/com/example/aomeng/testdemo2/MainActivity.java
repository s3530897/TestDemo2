package com.example.aomeng.testdemo2;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.http.Headers;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Function;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.EngageHumanBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TakePictureBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.camera.TakePicture;
import com.aldebaran.qi.sdk.object.conversation.Bookmark;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Chatbot;
import com.aldebaran.qi.sdk.object.conversation.ConversationStatus;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.object.geometry.Vector3;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.EngageHuman;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.aldebaran.qi.sdk.object.image.EncodedImage;
import com.aldebaran.qi.sdk.object.image.EncodedImageHandle;
import com.aldebaran.qi.sdk.object.image.TimestampedImageHandle;
import com.aldebaran.qi.sdk.object.touch.Touch;
import com.aldebaran.qi.sdk.object.touch.TouchSensor;
import com.aldebaran.qi.sdk.object.touch.TouchState;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private ConversationStatus conversationStatus;
    private Animation animation;
    private TouchSensor headTouchSensor;
    private Chat chat;
    private Button button;
    TextView textView;
    // An image view used to show the picture.
    // The QiContext provided by the QiSDK.
    private QiContext qiContext;
    // TimestampedImage future.
    Future<TimestampedImageHandle> timestampedImageHandleFuture;
    Future<Void> chatFuture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);
        button = (Button) findViewById(R.id.button);
        textView=(TextView)findViewById(R.id.textview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    //定义Handler对象
    private Handler handler =new Handler(){
        @Override
//当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
//处理UI
        }
    };



    @Override
    protected void onDestroy(){
        QiSDK.unregister(this,this);
        super.onDestroy();
    }
    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        // Get the Touch service from the QiContext.
        Touch touch = qiContext.getTouch();
        headTouchSensor = touch.getSensor("Head/Touch");
        // Add onStateChanged listener.
        headTouchSensor.addOnStateChangedListener(new TouchSensor.OnStateChangedListener() {
            @Override
            public void onStateChanged(TouchState touchState) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });


        Topic topic = TopicBuilder.with(qiContext) // Create the builder using the QiContext.
                .withResource(R.raw.greetings) // Set the topic resource.
                .build(); // Build the topic.

        QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext)
                .withTopic(topic)
                .build();

        chat = ChatBuilder.with(qiContext)
                .withChatbot(qiChatbot)
                .build();

        chat.addOnStartedListener(new Chat.OnStartedListener() {
            @Override
            public void onStarted() {

            }
        });
        chat.addOnHeardListener(new Chat.OnHeardListener() {
            @Override
            public void onHeard(Phrase heardPhrase) {
                if(heardPhrase!=null)
                    Log.i("TTTag", heardPhrase.getText());
                String s=heardPhrase.getText();
                new MyThread(s).start();
            }
        });

        //重要
        chatFuture = chat.async().run();

        chatFuture.thenConsume(new Consumer<Future<Void>>() {
            @Override
            public void consume(Future<Void> future) throws Throwable {
                if (future.hasError()) {
                }
            }
        });
        /*
        Say say = SayBuilder.with(qiContext).withText("你好我是数字生命管家").build();
        Topic topic = TopicBuilder.with(qiContext).withResource(R.raw.raw).build();
        animation =  AnimationBuilder.with(qiContext) // Create the builder with the context.
                .withResources(R.raw.show_head_a001) // Set the animation resource.
                .build();
        Animate animate=AnimateBuilder.with(qiContext).withAnimation(animation).build();
        conversationStatus = qiContext.getConversation().status(qiContext.getRobotContext());
        conversationStatus.addOnHeardListener(new ConversationStatus.OnHeardListener() {
            @Override
            public void onHeard(Phrase heardPhrase) {
                Log.i("myTag", heardPhrase.getText());
            }
        });

        Chat chat= ChatBuilder.with(qiContext).

                build();

        chat.addOnStartedListener(new Chat.OnStartedListener() {
            @Override
            public void onStarted() {
                System.console().printf("开始了么？");
            }
        });
        chat.addOnNoReplyFoundForListener(new Chat.OnNoReplyFoundForListener() {
            @Override
            public void onNoReplyFoundFor(Phrase input) {
                System.console().printf("没有发现");
            }
        });
       // Chatbot chatbot= QiChatbotBuilder.with(qiContext).withTopic()
    //    chat.run();
        say.async().run();
        animate.run();*/
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
        if (chat != null) {
            chat.removeAllOnStartedListeners();
            chat.removeAllOnHeardListeners();
        }
        /*
        if (conversationStatus != null) {
            conversationStatus.removeAllOnHeardListeners();
        }*/
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }


    public Map post(String url,BasicNameValuePair basicNameValuePair) {
        // 创建HttpPost对象
        Map docType = new HashMap();
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        // 创建传递参数集合
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(basicNameValuePair);
        // 设置字符集
        try {
            post.setEntity(new UrlEncodedFormEntity(paramsList, HTTP.UTF_8));
            Log.i("TTTag","测试一");
            HttpResponse response = client.execute(post);
            Log.i("TTTag","测试一.5");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Log.i("TTTag","测试二");
                HttpEntity httpEntity=response.getEntity();
                String strResult = EntityUtils.toString(httpEntity,"utf-8");
                //转MAP
                if(strResult.length()<4)
                    return null;
                strResult=strResult.substring(1,strResult.length() - 2);
                Log.i("TTTagMap",strResult);
                JsonObject returnData = new JsonParser().parse(strResult).getAsJsonObject();
                Log.i("TTTagMap",returnData.toString());
                Gson gson = new Gson();
                docType= gson.fromJson(strResult,Map.class);

                Log.i("TTTag",docType.toString());
                //JSONObject jo = JSONObject.fromObject(strResult);
                //Log.i("TTTag",jo.toString());
            }
            Log.i("TTTag","测试三");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return docType;
    }


    //post2

    public Map post(String url,String basicString,Boolean b) {
        // 创建HttpPost对象
        Map docType = new HashMap();
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        // 创建传递参数集合
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        String sssss="{'question':'"+basicString+"'}";
        // 设置字符集
        try {
            post.setEntity(new StringEntity(sssss, HTTP.UTF_8));
            post.setHeader("Content-Type","application/json");
            post.setHeader("Authorization","EndpointKey f0f30e69-8bf2-4beb-ad04-a63ef9c05cb0");

            Log.i("TTTagLLL","测试一");
            HttpResponse response = client.execute(post);
            Log.i("TTTagLLL","测试一.5");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Log.i("TTTagLLL","测试二");
                HttpEntity httpEntity=response.getEntity();
                String strResult = EntityUtils.toString(httpEntity,"utf-8");
                //转MAP
                JsonObject returnData = new JsonParser().parse(strResult).getAsJsonObject();
                Log.i("TTTagMapLLL",returnData.toString());
                Gson gson = new Gson();
                docType= gson.fromJson(strResult,Map.class);
                List<Map> docTypes=(List<Map>) docType.get("answers");
                docType=docTypes.get(0);
                Log.i("TTTagLLL",docType.get("answer").toString());
                //JSONObject jo = JSONObject.fromObject(strResult);
                //Log.i("TTTag",jo.toString());
            }
            Log.i("TTTagLLLMain",docType.get("answer").toString());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return docType;
    }
    public class MyThread extends Thread{
        private String ss;
        public MyThread(String s){
            ss=s;
        }
        public void run(){
            //你要执行的方法
            String s;
            Log.i("TTTagReturn",ss);
            if(ss.equals("<...>")||ss==null)
                s="我并没有听见你在说什么";
            else {
                Map map=post("https://diabete.azurewebsites.net/qnamaker/knowledgebases/44af971e-5765-46eb-848f-0ce76f1d35c2/generateAnswer",ss,true);
                s=map.get("answer").toString();
                Float accept=Float.valueOf(map.get("score").toString());
                if(accept>85.0){
                    Log.i("TTTagScore",accept.toString());
                    final String aaa=s;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(aaa);
                        }
                    });
                    return;
                }
                Map map2=post("http://119.29.248.56/nlp/api/v1.0/food_detect",new BasicNameValuePair("msg", ss));
                if(map2==null)
                    return;
                Map propertiesmap = (Map) map2.get("properties");
                Map sub_propertiesmap = (Map) map2.get("sub_properties");
                String finalresult="";
                finalresult += propertiesmap.get("name").toString()+"\n";
                finalresult += sub_propertiesmap.get("quantity").toString()+" ";
                finalresult += sub_propertiesmap.get("unit").toString()+"\n";
                finalresult += sub_propertiesmap.get("grams").toString()+"g\n";
                final String aa=finalresult;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(aa);
                    }
                });
                return;

            }


// Run the action synchronously.
            //say.async().run();

            //执行完毕后给handler发送一个空消息
            handler.sendEmptyMessage(0);
        }


    }
}
