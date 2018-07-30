package com.example.l.l_clothers.Fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l.l_clothers.Activity.MainActivity;
import com.example.l.l_clothers.Activity.SearchActivity;
import com.example.l.l_clothers.Activity.TreatlistActivity;
import com.example.l.l_clothers.Info.ClientManger;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.Service.BluetoothChatService;

/**
 * Created by L on 2017/6/6.
 */

public class MainPart1treat extends android.app.Fragment {
    Toolbar mToolbar;
    TextView mTextView;
    private PopupMenu mOverflowMenu;
    private ImageView toolbarMoreIcon;
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    private String mConnectedDeviceName = null;

    private ArrayAdapter<String> mConversationArrayAdapter;

    private StringBuffer mOutStringBuffer;

    private BluetoothAdapter mBluetoothAdapter = null;

    public BluetoothChatService mChatService = null;
    Button search;
    View view;

    String address;//存的是总地址

    Context context;



    //导入东西
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "蓝牙不可用，请打开蓝牙",
                    Toast.LENGTH_LONG).show();
            return null;
        }else {
            if(ClientManger.complete.equals("false")) {
                Toast.makeText(getActivity(),"请先完善个人资料",Toast.LENGTH_LONG).show();
                view = inflater.inflate(R.layout.fragment_main_part1, container, false);
                return view;
            }else {
                view = inflater.inflate(R.layout.fragment_main_part1, container, false);
                return view;
            }
        }

    }


    public void setupChat(){
        search = (Button) view.findViewById(R.id.search_start);
        if (ClientManger.isconnect == false) {
            search.setText("开始连接");
        } else
            search.setText("开始训练");

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClientManger.isconnect == false) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE);//返回结果,上面这个是发送指令，接收的时候就是reequestcode
                } else {
                    Intent intent = new Intent(getActivity(), TreatlistActivity.class);
                    startActivity(intent);
                }
            }
        });

        mChatService = new BluetoothChatService(getActivity(), mHandler);
        mOutStringBuffer = new StringBuffer("");
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)//Dj就是true
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
//                            mTitle.setText(R.string.title_connected_to);
//                            mTitle.append(mConnectedDeviceName);
//                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
//                            mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
//                            mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;

//                case MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
//                    Toast.makeText(getActivity(),
//                            "Connected to " + mConnectedDeviceName,
//                            Toast.LENGTH_SHORT).show();
//                    search.setText("开始训练");
//                    ClientManger.isconnect=true;
//                    ClientManger.address=address;
//                    break;
//                case MESSAGE_TOAST:
//                    Toast.makeText(getActivity(),
//                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
//                            .show();
//                    break;
            }
        }
    };
    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

        }else {
            if (mChatService == null)
                setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mChatService != null)
            mChatService.stop();
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (true)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE://通过这里来获得
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    address = data.getExtras().getString(
                            SearchActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter
                            .getRemoteDevice(address);
                    // Attempt to connect to the device
                    //此时其实还没有连接
                    Toast.makeText(getActivity(),"连接成功",Toast.LENGTH_LONG).show();
                    search.setText("开始训练");
                    ClientManger.isconnect=true;
                    ClientManger.address=address;
//                    mChatService.connect(device);
//                    ClientManger.isconnect=true;//此时表示已经连上了
//                    search.setText("开始训练");
//                    Toast.makeText(getActivity(),"已经连接上了",Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(),"蓝牙没有打开",
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }
}


