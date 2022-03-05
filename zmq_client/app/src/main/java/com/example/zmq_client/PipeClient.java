package com.example.zmq_client;

import android.widget.EditText;
import android.widget.TextView;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.UnsupportedEncodingException;

public class PipeClient {

    private final MainActivity mainActivity;
    private final String chatConn;
    private final String echoConn;
    private final TextView tvText;
    private final EditText etChat;
    private ZMQ.Socket sender;
    private ZMQ.Socket receiverEcho;
    private String msg = "";
    private String tmp = "";


    public PipeClient(MainActivity mainActivity, String chatConnStr, String echoConnStr,
               TextView tv, EditText et) {
        // main thread elements
        this.mainActivity = mainActivity;
        this.chatConn = chatConnStr;
        this.echoConn = echoConnStr;
        this.tvText = tv;
        this.etChat = et;
    }


    /** construct new pipe
     *  msg flow
     *  [USERINPUT]  ->REQ client    -> server(REQ+PULL)    ->SUB client    -> [Output]
     *          [sender -> receiver]                  [sender -> receiver]
    */
    public void startPipe() {
        ZContext ctx = new ZContext();
        ZMQ.Socket receiver = ctx.createSocket(SocketType.PAIR);
        receiver.bind("inproc://chatclient");
        sender = ctx.createSocket(SocketType.PAIR);
        sender.connect("inproc://chatclient");
        ZMQClient zmqClient = new ZMQClient(mainActivity, chatConn, receiver, tvText, etChat);
        zmqClient.start();

        receiverEcho = ctx.createSocket(SocketType.PAIR);
        receiverEcho.bind("inproc://echoclient");
        ZMQ.Socket senderEcho = ctx.createSocket(SocketType.PAIR);
        senderEcho.connect("inproc://echoclient");
        EchoClient echoClient = new EchoClient(echoConn, senderEcho, tvText, etChat);
        echoClient.start();

        Thread t1 = new Thread(this::sendInput);
        Thread t2 = new Thread(this::echoOutput);
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();

    }


    // Send to chat receiver
    private void sendInput() {
//        while (true) {
        mainActivity.findViewById(R.id.btnOK).setOnClickListener(v -> {
            try {
                tmp = etChat.getText().toString();
                sender.send(tmp.getBytes("GBK"));
                etChat.setText("");
            } catch (UnsupportedEncodingException ignored) {
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        });

    }

    // Receive from echo sender
    private void echoOutput() {
        while (true) {
            try {
                byte[] received = receiverEcho.recv();
                msg = new String(received, "GBK");
            } catch (Exception ignored) {}

            mainActivity.runOnUiThread(() -> {
                tvText.append(msg + "\r\n");
                scrollToEnd(tvText);
            });
        }
    }

    // scroll tvText log window to the bottom
    public void scrollToEnd(TextView tv) {
        try {
            int scrollAmount = tv.getLayout().getLineTop(tv.getLineCount())
                    - tv.getHeight();
            tv.scrollTo(0, Math.max(scrollAmount, 0));
        } catch (NullPointerException ignored) {

        }
    }
}
