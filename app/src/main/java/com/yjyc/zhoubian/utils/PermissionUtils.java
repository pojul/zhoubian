package com.yjyc.zhoubian.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


/**
 * Created by Administrator on 2018/7/24/024.
 */

public class PermissionUtils {

    public static void openSysSetting(Context context) {
        //设置界面
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }

    /**
     * 判断是不是Android 6.0 以上的版本
     *
     * @return
     */
    public static boolean isAndroidM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }
        return false;
    }

    public interface PermissionCallBack {
        void onGranted();

        void onDenied();
    }

    public static void checkLocationPermission(Context context, final PermissionCallBack permissionCallBack) {
        //6.0及以上
        if (isAndroidM()) {
            //请求权限
            AndPermission.with(context)
                    .permission(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION, Permission.WRITE_EXTERNAL_STORAGE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            permissionCallBack.onGranted();
                        }
                    })
                    .onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            permissionCallBack.onDenied();
                        }
                    })
                    .start();
        } else {
            permissionCallBack.onGranted();
        }
    }

    public static void checkPhonePermission(Context context, final PermissionCallBack permissionCallBack) {
        //6.0及以上
        if (isAndroidM()) {
            //请求权限
            AndPermission.with(context)
                    .permission(Permission.READ_PHONE_STATE, Permission.CALL_PHONE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            permissionCallBack.onGranted();
                        }
                    })
                    .onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            permissionCallBack.onDenied();
                        }
                    })
                    .start();
        } else {
            permissionCallBack.onGranted();
        }
    }

    public static void checkWritePermission(Context context, final PermissionCallBack permissionCallBack) {
        //6.0及以上
        if (isAndroidM()) {
            //请求权限
            AndPermission.with(context)
                    .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            permissionCallBack.onGranted();
                        }
                    })
                    .onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            permissionCallBack.onDenied();
                        }
                    })
                    .start();
        } else {
            if (isWritePermission()) {
                permissionCallBack.onGranted();
            } else {
                permissionCallBack.onDenied();
            }
        }
    }

    /**
     * 检查相机权限
     *
     * @param context
     * @param permissionCallBack
     */
    public static void checkCameraPermission(final Context context, final PermissionCallBack permissionCallBack) {
        //请求权限
        if (isAndroidM()) {
            AndPermission.with(context)
                    .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            permissionCallBack.onGranted();
                        }
                    })
                    .onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            permissionCallBack.onDenied();
                        }
                    })
                    .start();
        } else {
            boolean cameraPermission = isCameraPermission();
            boolean writePermission = isWritePermission();
            if (!cameraPermission) {
                permissionCallBack.onDenied();
                return;
            }
            if (!writePermission) {
                permissionCallBack.onDenied();
                return;
            }
            //permissionCallBack.onDenied();
            permissionCallBack.onGranted();
        }
    }

    /**
     * 相机是否可用
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    private static boolean isCameraPermission() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }
        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    /**
     * 录音是否可用
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    private static boolean isVoicePermission() {
        AudioRecord record = null;
        try {
            record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(22050,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
            //第一次  为true时，先释放资源，在进行一次判定
            //************
            record.release();
            record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(22050,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState1 = record.getRecordingState();
            if (recordingState1 == AudioRecord.RECORDSTATE_STOPPED) {
            }
            //**************
            //如果两次都是true， 就返回true  原因未知
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (record != null) {
                record.release();
            }
        }

    }

    /**
     * 联系人列表是否可用
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    private static boolean isContactsListPermission(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 是否有读写权限
     *
     * @return
     */
    private static boolean isWritePermission() {
        boolean isCanUser = true;
        File file = Environment.getExternalStorageDirectory();
        File newfile = new File(file, "1.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(newfile);
            fw.flush();
            fw.write("123");
            isCanUser = true;
        } catch (Exception e) {
            isCanUser = false;
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                    isCanUser = true;
                } catch (IOException e) {
                    isCanUser = false;
                }
            }
        }
        return isCanUser;
    }

    private static boolean isSmsPermission(Context context) {
        String SMS_URI_ALL = "content://sms/";
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type"};
            cursor = context.getContentResolver().query(uri, projection, null,
                    null, "date desc"); // 获取手机内部短信
            //TODO://这里需要注意，当没有权限时拿到的count可能是0，也许记录被删除了，这里需要注意下！
            if (cursor != null && cursor.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static boolean isCallRecordsPermission(Context context) {
        Cursor cursor = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            // uri的写法需要查看源码JB\packages\providers\ContactsProvider\AndroidManifest.xml中内容提供者的授权
            // 从清单文件可知该提供者是CallLogProvider，且通话记录相关操作被封装到了Calls类中
            Uri uri = CallLog.Calls.CONTENT_URI;
            String[] projection = new String[]{
                    CallLog.Calls.NUMBER, // 号码
                    CallLog.Calls.DATE,   // 日期
                    CallLog.Calls.TYPE    // 类型：来电、去电、未接
            };
            //TODO://这里需要注意，当没有权限时拿到的count可能是0，也许记录被删除了，这里需要注意下！
            cursor = resolver.query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

}