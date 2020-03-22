package com.example.testdistincebyandroidbluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String ACTION_FOUND = BluetoothDevice.ACTION_FOUND;
    private static final String ACTION_DISCOVERY_FINISHED = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;

    /**
     * 搜索蓝牙按钮
     */
    private Button actMainBuSacnBluetooth;

    /**
     * 显示蓝牙设备的recyclerview
     */
    private RecyclerView actMainRvDevice;
    private List<BluetoothDeviceBean> bluetoothDevices;
    private LinearLayout actMainLlSeaching;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDeviceAdapter bluetoothDeviceAdapter;
    /**
     * 定义一个广播接收器
     */
    private BroadcastReceiver bluetoothReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: action " + action);
            if (ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    //封装设备对象
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    String name = device.getName();
                    String mac = device.getAddress();
                    double dis = Math.abs(calculateDistance(61, rssi));
                    BluetoothDeviceBean deviceBean = new BluetoothDeviceBean(name, mac, dis, rssi);
                    Log.i(TAG, "onReceive: 搜索到设备 " + deviceBean.toString());

                    //装入集合
                    bluetoothDevices.add(deviceBean);
                }
            } else if (ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "onReceive: 搜索结束");
            }
        }
    };


    /**
     * 待申请权限集合
     */
    private String[] allPermissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            actMainLlSeaching.setVisibility(View.GONE);
            bluetoothAdapter.cancelDiscovery();
            showDevice();
        }
    };

    /**
     * @description 显示蓝牙设备
     * @author xin
     * @datetime 21/03/2020 23:15
     * @param
     */
    private void showDevice() {
        //自定义排序规则
        Comparator c = (Comparator<BluetoothDeviceBean>) (o1, o2) -> {
            if(o1.getDistince() < o2.getDistince()){
                return -1;
            }
            return 1;
        };
        //按照位置从近到远排序
        bluetoothDevices.sort(c);
        actMainRvDevice.setAdapter(bluetoothDeviceAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyPermission();
        findViews();
        initData();
    }

    private void initData() {
        //获取蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothDevices = new ArrayList<>();
        //注册一个接收器，当发现一个蓝牙设备时发广播
        registerReceiver(bluetoothReceive, new IntentFilter(ACTION_FOUND));

        //注册一个接收器，当搜索蓝牙设备结束时发广播
        registerReceiver(bluetoothReceive, new IntentFilter(ACTION_DISCOVERY_FINISHED));
        // 指定一个默认的布局管理器
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        actMainRvDevice.setLayoutManager(mLayoutManager);
        // 指定item分割线
        actMainRvDevice.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        // 指定适配器
        bluetoothDeviceAdapter = new BluetoothDeviceAdapter(this, bluetoothDevices);

    }

    /**
     * @param
     * @return 是否执行了权限申请
     * @description 申请权限
     * @author xin
     * @datetime 21/03/2020 22:07
     */
    private boolean applyPermission() {
        boolean isNeedApply = false;
        if (Build.VERSION.SDK_INT >= 23) {
            for (String allPermission : allPermissions) {
                int isPermited = ContextCompat.checkSelfPermission(getApplicationContext(), allPermission);
                if (isPermited != PackageManager.PERMISSION_GRANTED) {
                    isNeedApply = true;
                }
            }
            if (isNeedApply) {
                ActivityCompat.requestPermissions(this, allPermissions, 1);
            }
        }
        return isNeedApply;
    }

    private void findViews() {
        actMainBuSacnBluetooth = (Button) findViewById(R.id.act_main_bu_sacn_bluetooth);
        actMainRvDevice = (RecyclerView) findViewById(R.id.act_main_rv_device_list);
        actMainLlSeaching = findViewById(R.id.act_main_ll_seaching);
        actMainBuSacnBluetooth.setOnClickListener(v -> {
            if (!bluetoothAdapter.isEnabled()) {//如果蓝牙没开，申请开启
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            } else {//如果蓝牙已开
                bluetoothDevices.clear();
                actMainLlSeaching.setVisibility(View.VISIBLE);
                //开始扫描蓝牙设备
                seachBluetoothDevice();
            }
        });
    }

    /**
     * @param
     * @description 搜索蓝牙设备
     * @author xin
     * @datetime 21/03/2020 22:14
     */
    private void seachBluetoothDevice() {
        //开个线程搜索蓝牙设备
        new Thread(() -> {
            bluetoothAdapter.startDiscovery();
            Log.i(TAG, "seachBluetoothDevice: 开始搜索蓝牙设备");
        }).start();
        Intent intent = new Intent();
        intent.setAction("com.example.testdistincebyandroidbluetooth");
        intent.setPackage("com.example.testdistincebyandroidbluetooth");
        sendBroadcast(intent);

        //10S后停止扫描蓝牙
        handler.sendEmptyMessageDelayed(0, 1000 * 10);
    }

    private double calculateDistance(double txPower, int rssi) {
        // 信号值得绝对值.
        int absRssi = Math.abs(rssi);
        // txPower 一米值. 暂时使用经验值 59 .需要替换成自己的真实值.
        double power = (absRssi - txPower) / (10 * 2.0);
        return Math.pow(10, power);
    }
}
