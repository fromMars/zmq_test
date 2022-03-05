package com.example.zmq_client;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import org.zeromq.SocketType;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import zmq.Msg;
import zmq.pipe.Pipe;

public class ZMQClient implements Runnable {

    private static final String __DBG__ = "+++ dbg client +++";
    private static final String endl = "\r\n";
    private String chatConnStr;
    private ZContext zmqContext;
    private ZMQ.Socket chatSock, pipe;
    private ZMQ.Poller poller;
    private MainActivity mainActivity;
    private TextView tvText;
    private EditText etChat;
    private boolean threadSuspended;
    public Thread zmqThread = null;

    /*
    // create singleton
    private static ZMQClient singleClient = null;
    public static ZMQClient getInstance(String conn_str, TextView tv, EditText et) {
        singleClient = new ZMQClient(conn_str, tv, et);
        return singleClient;
    }

    // private constructor
    private ZMQClient(String conn_str, TextView tv, EditText et) {
        tvText = tv;
        etChat = et;
        zmqContext = new ZContext();
        socket = zmqContext.createSocket(SocketType.REQ);
        socket.setIPv6(true);
        try{
            socket.connect(conn_str);
        } catch (Exception e) {
            Log.d("+++dbg+", e.getMessage());
            try {
                tvText.setText(e.getMessage());
            } catch (ArrayIndexOutOfBoundsException er) {}
        }
    }*/

    // no singleton
    public ZMQClient(MainActivity mainActivity, String connstrChat, ZMQ.Socket chatPipe,
                     TextView tv, EditText et) {
        this.mainActivity = mainActivity;
        this.chatConnStr = connstrChat;
        this.pipe = chatPipe;
        tvText = tv;
        etChat = et;

        zmqContext = new ZContext();
        this.poller = zmqContext.createPoller(2);
    }

    // connect to REP server
    public void conn() {
        chatSock = zmqContext.createSocket(SocketType.REQ);
        chatSock.setIPv6(true);
        try{
            chatSock.connect(chatConnStr);
        } catch (Exception e) {
            Log.d("+++dbg+", e.getMessage());
            zmqThread = null;
        }
    }

    // reconnect
    public void reconn() {
        poller.unregister(chatSock);
        chatSock.setLinger(0);
        chatSock.close();
        conn();
        regWithPoller();
    }

    // reg poller
    public void regWithPoller() {
        poller.register(chatSock, ZMQ.Poller.POLLIN);
    }

    // receive from REP server
    public void getReply() {
        chatSock.recv();
    }

    // find poller object
    public boolean hasMsg() {
        return poller.pollin(0);
    }

    // Send msg to server
    public byte[] promptForMsg() {
        try {
            return pipe.recv();
        } catch (Exception e) {}
        try {
            return "promptForMsg failed!".getBytes("GBK");
        } catch (UnsupportedEncodingException e) {}
        return new byte[]{0};
    }

    @Override
    public void run() {
        conn();
        regWithPoller();
        while (true) {
            // msg from input
            byte[] msg = promptForMsg();

            // send to server
            chatSock.send(msg);

            // receive from REP server
            if (hasMsg()) {
                getReply();
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scrollToEnd(tvText);
                    }
                });
            }
            else
                reconn();
        }
    }

    // scroll tvText log window to the bottom
    public void scrollToEnd(TextView tv) {
        try {
            int scrollAmount = tv.getLayout().getLineTop(tv.getLineCount())
                    - tv.getHeight();
            if (scrollAmount > 0)
                tv.scrollTo(0, scrollAmount);
            else
                tv.scrollTo(0, 0);
        } catch (NullPointerException e) {

        }
    }

    public void start() {
        try {
            zmqThread = new Thread(this);
            zmqThread.start();
        } catch (Exception e) {
            try {
                Log.d(__DBG__, e.getMessage());
            } catch (ArrayIndexOutOfBoundsException er) {}
        }
    }

    public void stop() {
        zmqThread = null;
    }
}
