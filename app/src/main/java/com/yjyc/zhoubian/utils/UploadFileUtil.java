package com.yjyc.zhoubian.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.UploadModel;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class UploadFileUtil {

    private List<String> uploadResults = new ArrayList<>();
    private int uploadedFiles;
    private Context context;
    private UploadFileCallBack callBack;
    private List<String> files;
    private String uploadMsg;
    private Login login;

    public String File2StrByBase64(File f){
        ByteArrayOutputStream out= null;
        try {
            FileInputStream stream = new FileInputStream(f);
            out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            byte[] imgBytes = out.toByteArray();
            return "data:image/jpeg;base64," + Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过Base32将Bitmap转换成Base64字符串
     * @return
     */
    public String Bitmap2StrByBase64(String imgPath){
        Bitmap bitmap = null;
        if (imgPath !=null && imgPath.length() > 0) {
            bitmap =  BitmapFactory.decodeFile(imgPath);
        }
        if(bitmap == null){
            return null;
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 30, out);
            out.flush();
            out.close();

            byte[] imgBytes = out.toByteArray();
            return "data:image/jpeg;base64," + Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadFiles(List<String> files, Context context, UploadFileCallBack callBack){
        uploadResults = new ArrayList<>();
        if(files == null || files.size() <= 0){
            if(callBack != null){
                callBack.finish(uploadResults);
            }
            return;
        }
        login = Hawk.get("LoginModel");
        if(login == null){
            if(callBack == null){
                callBack.error("用户未登录");
            }
            return;
        }
        uploadedFiles = 0;
        this.context = context;
        this.callBack = callBack;
        this.files = files;
        uploadMsg = "NoError";
        uploadFile();
    }

    private void uploadFile(){
        if(!uploadMsg.equals("NoError")){
            if(callBack != null){
                callBack.error(uploadMsg);
            }
            return;
        }
        if(uploadedFiles == files.size()){
            if(callBack != null){
                callBack.finish(uploadResults);
            }
            return;
        }
        compressFile();

    }

    private void compressFile() {
        if(FileUtil.isNetUrl(files.get(uploadedFiles))){
            uploadResults.add( (files.get(uploadedFiles).replace(HttpUrl.BASE_URL_NOEND, "")) );
            uploadedFiles ++;
            uploadFile();
            return;
        }
        Luban.with(context)
                .load(new File(files.get(uploadedFiles)))
                .ignoreBy(30)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        uploadFile(File2StrByBase64(file));
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        uploadFile(Bitmap2StrByBase64(files.get(uploadedFiles)));
                    }
                }).launch();
    }

    private void uploadFile(String fileStr){
        String url =  HttpUrl.UPLOADSIMGFORSTRING;
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("file", fileStr);
        params.put("token", (login.token + ""));
        params.put("uid", (login.uid + ""));
        new Thread(() -> {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("charset", HTTP.UTF_8);
            httpPost.setHeader("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            HttpResponse response = null;
            if (params != null && params.size() > 0) {
                List<NameValuePair> nameValuepairs = new ArrayList<NameValuePair>();
                for (String key : params.keySet()) {
                    nameValuepairs.add(new BasicNameValuePair(key, (String) params
                            .get(key)));
                }
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuepairs,
                            HTTP.UTF_8));
                    response = client.execute(httpPost);
                    String token = EntityUtils.toString(response.getEntity());
                    jsonToObject(token);
                    Logger.i(token);
                } catch (Exception e) {
                    uploadMsg = e.getMessage();
                    uploadFile();
                }
            } else {
                try {
                    response = client.execute(httpPost);
                } catch (Exception e) {
                    uploadMsg = e.getMessage();
                    uploadFile();
                }
            }
        }).start();//这个start()方法不要忘记了
    }

    private void jsonToObject(String token) {
        try {
            UploadModel um= new Gson().fromJson(token, UploadModel.class);
            if(um.getHeader() != null && "success".equals(um.getHeader().getStatus()) && um.getBody() != null
                    && !StringUtils.isEmpty(um.getBody().url)){
                uploadedFiles ++;
                uploadResults.add(um.getBody().url);
            }else {
                uploadMsg = "上传图片失败";
            }
            uploadFile();
        }catch (Exception e){
            uploadMsg = e.getMessage();
            uploadFile();
        }
    }

    public interface UploadFileCallBack{
        void finish(List<String> strs);
        void error(String msg);
    }

}
