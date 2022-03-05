package com.example.zmq_client;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import zmq.pipe.Pipe;


public class MainActivity extends AppCompatActivity {

    public String chatConn, echoConn;
    public static String svAddr = "2409:8a3c:3d02:bb80:b4:b18:89f2:bd85";
//    public static String svAddr = "192.168.1.2";
    public static String chatPort = ":4242", echoPort = ":4243";
    private PipeClient pipeClient;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionCheck(this);
        clientHandler();
    }


    private void permissionCheck(Context context) {
        // set network related permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.CHANGE_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "Please Grant NETWORK PRIVILIGE!",
                    Toast.LENGTH_LONG).show();
        }
    }


    // create client
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void clientHandler() {
        // get UI elements
        Button btnOK = findViewById(R.id.btnOK);
        Button btnSTOP = findViewById(R.id.btnStop);
        Button btnCONN = findViewById(R.id.btnConn);
        ScrollView scrView = findViewById(R.id.scrVIEW);
        TextView tvText = findViewById(R.id.tvText);
        EditText etAddr = findViewById(R.id.etADDR);
        EditText etChat = findViewById(R.id.etCHAT);

        /* // another method to get display size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;*/

        // resize tvText
        Point pt = new Point();
        getDisplay().getSize(pt);
        tvText.setHeight(pt.y/2);

        // set textview scrollable
        tvText.setMovementMethod(ScrollingMovementMethod.getInstance());
        // show default server address
        etAddr.setText(svAddr);

        // set conn_str, start local server, connect remote server
        // by "C" button
        btnCONN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // set server ip
                svAddr = etAddr.getText().toString();
                chatConn = "tcp://" + svAddr + chatPort;
                echoConn = "tcp://" + svAddr + echoPort;
                tvText.append("connected to: \r\n");
                tvText.append(chatConn);
                tvText.append("\r\n");
                tvText.append(echoConn);

                // new pipe
                pipeClient = new PipeClient(MainActivity.this, chatConn, echoConn,
                        tvText, etChat);
                pipeClient.startPipe();
            }
        });

        /*
        // stop client by "STOP" button
        btnSTOP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    /*
    // get server uri
    public String getSvUri(String svAddr, String svPort) {
        return "tcp://" + svAddr + svPort;
    }*/
}