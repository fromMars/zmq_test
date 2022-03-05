//
// This is a REP-REQ server, implements a single dual client communicate method
// each device has a client connects with the server on the other device, and
// vice-versa. For example:
//          A and B connected mutually
//      A(client, server) <---> B(client, server)
//
// While in CMCC ipv6 network enviroment, we couldn't create a socket server by
// default, this means the servers would only work when they connect
// to a ipv6 supported WIFI enviroment.
// But a client works well.
// So I decide to use a Win32 server on PC which connects to the IPv6 WIFI network,
// and let the clients connects to it.
// And I also change to PUB-SUBs pattern, like this:
//
//                          C(client)
//                              ↓
//          A(client) → ChatRoom(Server) <-- B(client)
//                              ↑
//                        other clients
//
// So this class is now deprecated.
//

package com.example.zmq_client;

import android.util.Log;
import android.widget.TextView;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.UnsupportedEncodingException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class ZMQServer implements Runnable{

    public Thread zmqThread;
    public ZContext ctx;
    public ZMQ.Socket socket;
    public static String loalAddr = getLocalIpAddress();
    private static final String __DBG__ = "+++ dbg server +++";
    private static final String endl = "\r\n";
    private TextView tvText;
    private MainActivity mainActivity;
    private String msg = "";

    ZMQServer(MainActivity mainActivity, TextView tv) {
        this.mainActivity = mainActivity;
        tvText = tv;
        String svUri = new String("tcp://" + loalAddr + ":4242");

//        String svUri = new String("tcp://" + "*" + ":4242");
//        byte[] nulStr = new byte[]{0};
//        msg = new String(nulStr);

        this.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvText.append("local address = ");
                tvText.append(loalAddr + endl);
            }
        });

        ctx = new ZContext();
        socket = ctx.createSocket(SocketType.REP);
        socket.setIPv6(true);
        socket.setReceiveTimeOut(15000);
//        socket.setReqRelaxed(true);
//        socket.setReqCorrelate(true);

        try {
            socket.bind(svUri);
        } catch (Exception e) {
            Log.d(__DBG__, e.getMessage());
            zmqThread = null;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // raw bytes received
                byte[] received = socket.recv(0);
                // convert to string
                try {
                    msg = new String(received, "GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // update chat window
                this.mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tvText.append(msg);
                            tvText.append("\r\n");
                            scrollToEnd(tvText);
//                            ((EditText)mainActivity.findViewById(R.id.etADDR)).setText(msg);
                        } catch (ArrayIndexOutOfBoundsException e) {}
                    }
                });
                // send to client
                socket.send(msg.getBytes("GBK"));
            } catch (Exception e) {
                Log.d(__DBG__, e.getMessage() + endl + e.toString());
            }
        }
    }

    // get local ipv6 address
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en
                 = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr
                     = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()
                            && inetAddress instanceof Inet6Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // scroll tvText log window to the bottom
    public static void scrollToEnd(TextView tv) {
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

    void start() {
        try {
            zmqThread = new Thread(this);
            zmqThread.start();
        } catch (Exception e) {
            try {
                Log.d(__DBG__, e.getMessage());
            } catch (ArrayIndexOutOfBoundsException er) {}
        }
    }

    void stop() { zmqThread = null; }
}
