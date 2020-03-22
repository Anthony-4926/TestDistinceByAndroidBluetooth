# 基于Android的蓝牙测距
## 1.申请权限
    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

 - Android6.0+需要动态申请权限，详情见代码
 ## 2.定义广播接收器
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
 ## 3.注册广播
 
    //当发现一个蓝牙设备时触发
    registerReceiver(bluetoothReceive, new IntentFilter(ACTION_FOUND));
    
    //当搜索蓝牙设备结束时触发
    registerReceiver(bluetoothReceive, new IntentFilter(ACTION_DISCOVERY_FINISHED));
 ## 4.申请蓝牙适配器
     bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
     
 ## 5.搜索蓝牙设备
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