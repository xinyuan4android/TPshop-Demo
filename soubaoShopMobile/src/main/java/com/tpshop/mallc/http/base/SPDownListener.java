package com.tpshop.mallc.http.base;

/**
 * Created by admin on 2016/8/4.
 */
public interface SPDownListener {
    public void onRespone(int statusCode , byte[] binaryData);
    public void onProgress(long currSize, long totalSize);

}
