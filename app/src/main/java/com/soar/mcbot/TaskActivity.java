package com.soar.mcbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;

public class TaskActivity extends AppCompatActivity {

    public static int currentTaskId;
    public static String serverIp;
    public static int serverPort;
    private AppCompatTextView serverText, statusText;
    private final String[] stateTexts = {
            "当前无任务",
            "定时右键 - 运行中",
            "定时左键 - 运行中",
            "定时左右移动 - 运行中",
            "自动种植 - 运行中",
            "按住右键 - 运行中",
            "按住左键 - 运行中",
            "按住W键 - 运行中",
            "按住Shift键 - 运行中",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        serverText = findViewById(R.id.serverText);
        statusText = findViewById(R.id.statusText);
        findViewById(R.id.mouseClickTimerRightBtn).setOnClickListener(v -> {
            if (currentTaskId != 0) return;
            currentTaskId = 1;
            sendMsg("BOT:SET:1:1:1000");
        });
        findViewById(R.id.mouseClickTimerLeftBtn).setOnClickListener(v -> {
            if (currentTaskId != 0) return;
            currentTaskId = 2;
            sendMsg("BOT:SET:2:1:1000");
        });
        findViewById(R.id.autoFishBtn).setOnClickListener(v -> {
            if (currentTaskId != 0) return;
            currentTaskId = 3;
            sendMsg("BOT:SET:3:1:1:60");
        });
        findViewById(R.id.autoPlantBtn).setOnClickListener(v -> {
            if (currentTaskId != 0) return;
            currentTaskId = 4;
            sendMsg("BOT:SET:4:1");
        });
        findViewById(R.id.mousePressRightBtn).setOnClickListener(v -> {
            if (currentTaskId != 0) return;
            currentTaskId = 5;
            sendMsg("BOT:SET:5:1");
        });
        findViewById(R.id.mousePressLeftBtn).setOnClickListener(v -> {
            if (currentTaskId != 0) return;
            currentTaskId = 6;
            sendMsg("BOT:SET:6:1");
        });
        findViewById(R.id.pressWKeyBtn).setOnClickListener(v -> {
            if (currentTaskId != 0) return;
            currentTaskId = 7;
            sendMsg("BOT:SET:7:1");
        });
        findViewById(R.id.pressShiftKeyBtn).setOnClickListener(v -> {
            if (currentTaskId != 0) return;
            currentTaskId = 8;
            sendMsg("BOT:SET:8:1");
        });
        findViewById(R.id.stopBtn).setOnClickListener(v -> {
            if (currentTaskId == 0) return;
            sendMsg(String.format("BOT:SET:%d:0", currentTaskId));
            currentTaskId = 0;
        });
        initData();
    }

    private void initData() {
        serverText.setText(String.format("%s:%d", serverIp, serverPort));
        updateStateText();
    }

    private void updateStateText() {
        statusText.setText(stateTexts[currentTaskId]);
    }

    private void sendMsg(String msg) {
        updateStateText();
        new Thread(() -> {
            DatagramSocket socket = null;
            try {
                Inet4Address address = (Inet4Address) Inet4Address.getByName(serverIp);
                byte[] data = msg.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(data, data.length, address, serverPort);
                socket = new DatagramSocket();
                socket.send(packet);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }).start();
    }
}