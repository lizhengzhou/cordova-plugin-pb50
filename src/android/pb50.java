package cn.lizz.cordova.plugin;

import android.app.Activity;
import android.text.TextUtils;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;

import honeywell.connection.ConnectionBase;
import honeywell.connection.Connection_Bluetooth;
/**
 * This class echoes a string called from JavaScript.
 */
public class pb50 extends CordovaPlugin {

    ConnectionBase _conn = null;
    String _mac = "";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("print")) {
            String mac = "";
            String strData = "";
            try {
                mac = args.getString(0);
                strData = args.getString(1);
            } catch (Exception e) {
                callbackContext.error(e.getMessage());
            }
            this.printMethod(mac, strData, callbackContext);
            return true;
        }
        return false;
    }

    private void printMethod(String mac, String strData, CallbackContext callbackContext) {
        if (mac != null && mac.length() > 0 && strData != null && strData.length() > 0) {
            Activity context = cordova.getActivity();
            try {
                if (_conn == null || !TextUtils.equals(_mac, mac)) {
                    _conn = Connection_Bluetooth.createClient(mac, false);
                    _mac = mac;
                }
                if (!_conn.getIsOpen()) {
                    _conn.open();
                }
                byte[] printData = strData.getBytes();

                int bytesWritten = 0;
                int bytesToWrite = 1024;
                int totalBytes = printData.length;
                int remainingBytes = totalBytes;
                while (bytesWritten < totalBytes) {
                    if (remainingBytes < bytesToWrite)
                        bytesToWrite = remainingBytes;
                    //Send data, 1024 bytes at a time until all data sent
                    _conn.write(printData, bytesWritten, bytesToWrite);
                    bytesWritten += bytesToWrite;
                    remainingBytes = remainingBytes - bytesToWrite;
                    Thread.sleep(100);
                }
                callbackContext.success("发送结束");
            } catch (Exception e) {
                callbackContext.error(e.getMessage());
            }
        } else {
            callbackContext.error("蓝牙地址或打印内容不能为空!");
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        _conn.close();
    }

    @Override
    public void onResume(boolean multitasking) {
        if (!_conn.getIsOpen()) {
            try {
                _conn.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        _conn.close();
    }


}
