package vmokshagroup.com.myapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MyService extends Service {

    static String UDP_BROADCAST = "UDPBroadcast";
    static String UDP_ACCESS_Point = "UDPAccessPoint";

    public static final String MyReceiver = "com.example.Broadcast";

    //Boolean shouldListenForUDPBroadcast = false;
    DatagramSocket socket;
    public static DatagramPacket packet;
    public static String macAddress;
    Intent intent;


    //broadcastIP is not yet required
    private void listenAndWaitAndThrowIntent(Integer port) throws Exception {
        byte[] recvBuf = new byte[1024];
        try {


            if (socket == null || socket.isClosed()) {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.setBroadcast(true);
                socket.bind(new InetSocketAddress(port));
            }

            packet = new DatagramPacket(recvBuf, recvBuf.length);


            socket.receive(packet);
          // Log.e("packet", packet + "");


            String senderIP = packet.getAddress().getHostAddress();
            String message = new String(packet.getData()).trim();



          Log.e("UDP", "Got UDB broadcast from " + senderIP + ", message: " + message);


            broadcastIntent(senderIP, message);
            socket.close();

        } catch (IOException e) {

            Log.e("e", packet + "");
        }
    }


    private void broadcastIntent(String senderIP, String message) {

        if (message.contains("SSID")) {
            intent = new Intent(MyService.UDP_ACCESS_Point);

        } else {
            intent = new Intent(MyService.UDP_BROADCAST);
        }
        intent.putExtra("sender", senderIP);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    Thread UDPBroadcastThread;

    void startListenForUDPBroadcast() {
        UDPBroadcastThread = new Thread(new Runnable() {
            public void run() {
                try {
                    // InetAddress broadcastIP = InetAddress.getByName("172.16.238.255"); //172.16.238.42 //192.168.1.255
                    Integer port = 54521;
                    while (shouldRestartSocketListen) {
                        listenAndWaitAndThrowIntent(port);
                    }
                    //if (!shouldListenForUDPBroadcast) throw new ThreadDeath();
                } catch (Exception e) {
                    Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                }
            }
        });
        UDPBroadcastThread.start();
    }

    private Boolean shouldRestartSocketListen = true;

    void stopListen() {
        shouldRestartSocketListen = false;
        socket.close();
    }

    @Override
    public void onCreate() {

    }

    ;

    @Override
    public void onDestroy() {
        stopListen();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        shouldRestartSocketListen = true;
        startListenForUDPBroadcast();
        Log.i("UDP", "Service started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}


