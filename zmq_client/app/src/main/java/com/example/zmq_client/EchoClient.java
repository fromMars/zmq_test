package com.example.zmq_client;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import zmq.Msg;
import zmq.pipe.Pipe;

public class EchoClient implements Runnable{

    private static final String __DBG__ = "+++ dbg echoclient +++";
    private final String echoConnStr;
    private final ZContext zmqContext;
    private ZMQ.Socket echoSock;
    private final ZMQ.Socket pipe;
    private ZMQ.Poller poller;
    public Thread zmqThread = null;


    // no singleton
    public EchoClient(String connstrEcho, ZMQ.Socket echoPipe,
                     TextView tv, EditText et) {

        // params to create socket and pipe
        this.echoConnStr = connstrEcho;
        this.pipe = echoPipe;

        // new context
        zmqContext = new ZContext();

        // new poller
        this.poller = zmqContext.createPoller(2);
    }


    // PUB-SUB connection
    public void echoConn() {
        echoSock = zmqContext.createSocket(SocketType.SUB);

        // set subscribe topic
        echoSock.subscribe("");

        // set ipv6 support
        echoSock.setIPv6(true);

        try{
            echoSock.connect(echoConnStr);
        } catch (Exception e) {
            Log.d("+++dbg+", e.getMessage());
            zmqThread = null;
        }

        // register poller
        poller.register(echoSock, ZMQ.Poller.POLLIN);
    }


    // Receive from PUB server
    public void getRecv() {

        // receive msg
        byte[] received = echoSock.recv();

        // send msg to pipe receiver (PipeClient::receiverEcho)
        pipe.send(received);
    }


    /**
     * thread related functions
     */
    @Override
    public void run() {
        echoConn();

        // keep receiving msg from PUB server
        while (true) {
            getRecv();
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
