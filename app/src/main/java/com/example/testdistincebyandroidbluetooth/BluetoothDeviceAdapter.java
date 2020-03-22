package com.example.testdistincebyandroidbluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author xin
 * @datetime 21/03/2020 21:47
 * @description
 */
public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {
    private static final String TAG = "BluetoothDeviceAdapter";
    private Context context;
    private List<BluetoothDeviceBean> bluetoothDevices;

    public BluetoothDeviceAdapter(Context context, List<BluetoothDeviceBean> bluetoothDevices) {
        this.context = context;
        this.bluetoothDevices = bluetoothDevices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bluetooth_device_list, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(bluetoothDevices.get(position).getName());
        holder.itemView.setOnClickListener(v ->{
            Log.i(TAG, "onBindViewHolder: click"+position);
            listDialog(bluetoothDevices.get(position));
        });
    }
    /**
     * 列表对话框
     */
    private void listDialog(BluetoothDeviceBean device) {
        final String[] items = {
                "名称："+device.getName(),
                "地址："+device.getMac(),
                "距离："+device.getDistince(),
                "rssi： "+ device.getRssi()};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, null);
        builder.create().show();
    }



    @Override
    public int getItemCount() {
        return bluetoothDevices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_bluetooth_device_list);
        }
    }
}