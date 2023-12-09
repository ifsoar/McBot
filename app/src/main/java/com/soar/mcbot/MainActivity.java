package com.soar.mcbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.material.button.MaterialButton;

import java.lang.ref.SoftReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private AppCompatTextView stateText;
    private Handler handler;
    private Thread scanThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        stateText = findViewById(R.id.stateText);
        findViewById(R.id.scanBtn).setOnClickListener(v -> {
            startScan();
        });
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread = null;
        }
    }

    private void startScan() {
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread = null;
        }
        scanThread = new Thread(() -> {
            SoftReference<Activity> refContext = new SoftReference<>(MainActivity.this);
            DatagramSocket socket = null;
            try {
                Inet4Address address = (Inet4Address) Inet4Address.getByName("255.255.255.255");
                byte[] data = "BOT:QUE".getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(data, data.length, address, 9881);
                socket = new DatagramSocket();
                socket.send(packet);

                data = new byte[1024];
                packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                String msgBack = new String(packet.getData(), 0, packet.getLength());
                if (msgBack.startsWith("BOT:BAK:QUE:")) {

                    TaskActivity.currentTaskId = Integer.valueOf(msgBack.substring(msgBack.lastIndexOf(":") + 1));
                }
                String host = packet.getAddress().getHostAddress();
                TaskActivity.serverIp = host;
                TaskActivity.serverPort = packet.getPort();
                if (refContext.get() != null) {
                    refContext.get().runOnUiThread(() -> {
                        stateText.setText("online");
                        goTaskActivity();
                    });
                }
            } catch (Exception e) {
                refContext.clear();
            } finally {
                refContext.clear();
                scanThread = null;
                if (socket != null) {
                    socket.close();
                }
            }
        });
        scanThread.start();
    }

    private void goTaskActivity() {
        Intent intent = new Intent(this, TaskActivity.class);
        startActivity(intent);
        finish();
    }
}