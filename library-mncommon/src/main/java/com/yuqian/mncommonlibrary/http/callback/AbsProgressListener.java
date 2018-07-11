package com.yuqian.mncommonlibrary.http.callback;

import com.orhanobut.logger.Logger;

import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.body.ProgressInfo;

/**
 * @author : maning
 * @desc :  上传下载监听
 */
public abstract class AbsProgressListener implements ProgressListener {

    @Override
    public void onProgress(ProgressInfo progressInfo) {
        Logger.i("onProgress:" + progressInfo.toString());
        onProgress(progressInfo.getCurrentbytes(), progressInfo.getContentLength());
        if (progressInfo.isFinish()) {
            onFinish();
        }
    }

    @Override
    public void onError(long id, Exception e) {
        onFailure(e);
        onFinish();
    }

    public abstract void onProgress(long currentBytes, long contentLength);

    public abstract void onFailure(Exception e);

    /**
     * 开始
     */
    public abstract void onStart();

    /**
     * 完成
     */
    public abstract void onFinish();

}
