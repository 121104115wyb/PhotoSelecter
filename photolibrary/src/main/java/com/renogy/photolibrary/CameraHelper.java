package com.renogy.photolibrary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author wyb
 * Date :2019/11/8 0008 9:52
 * Description: 打开相机，拍照的工具类 打开相机器
 * 适配 android 10
 */
public class CameraHelper {
    /**
     * 是否是Android 10以上手机
     */
    private Activity mContext;

    private int code = 10000;
    /**
     * your provider android:authorities
     * 你的清单文件中的provider中的authorities
     */
    private String authorities = "com.renogy.photo.provider";
    /**
     * 图片名称
     */
    private String imgName = "";
    /**
     * 压缩宽度
     */
    private int compressWidth = 480;
    /**
     * 压缩高度
     */
    private int compressHeight = 720;
    /**
     * 质量压缩
     */
    private int quality = 90;

    /**
     * 原始的返回的uri
     */
    private Uri mCameraUri = null;
    /**
     * 临时缓存的图片路径
     */
    private String filePath = "";
    /**
     * 最终保存的图片地址
     */
    private String resultFilePath = "";

    private SaveImgCallBack callBack;

    public void setCallBack(SaveImgCallBack callBack) {
        this.callBack = callBack;
    }

    public int getCode() {
        return code;
    }

    public String getFilePath() {
        return filePath;
    }


    public void setCode(int code) {
        this.code = code;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public CameraHelper(@NonNull Activity context) {
        this.mContext = context;
    }

    public void setCompressWidth(int compressWidth) {
        this.compressWidth = compressWidth;
    }

    public void setCompressHeight(int compressHeight) {
        this.compressHeight = compressHeight;
    }

    public void setQuality(@IntRange(from = 0, to = 100) int quality) {
        this.quality = quality;
    }

    public CameraHelper(@NonNull Activity context, String authorities, String filePath, int code) {
        this.mContext = context;
        if (!TextUtils.isEmpty(authorities)) {
            this.authorities = authorities;
        }
        if (!TextUtils.isEmpty(filePath)) {
            this.filePath = filePath;
        }
        if (code != 0) {
            this.code = code;
        }
    }

    //打开相机
    public void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;
            if (Build.VERSION.SDK_INT >= 29) {
                // 适配android 10
                photoUri = createImageUri();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    filePath = photoFile.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(mContext, authorities, photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }
            mCameraUri = photoUri;
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                mContext.startActivityForResult(captureIntent, code);
            }
        }
    }


    /**
     * 创建保存图片的文件
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()));
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     *
     * @return 图片的uri
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return mContext.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }


    /**
     * 线程保存到本地，并返回filePath 和Uri
     */
    public void saveAndCompress() {

        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Uri>() {
            @Override
            public Uri doInBackground() throws Throwable {
                // 首先保存图片
                File appDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath());
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                File file;
                if (TextUtils.isEmpty(imgName)) {
                    file = new File(appDir, System.currentTimeMillis() + ".jpg");
                } else {
                    file = new File(appDir, imgName);
                }
                Bitmap bitmap = ImageUtils.compressBySampleSize(BitmapFactory.decodeFile(filePath), compressWidth, compressHeight);
                Bitmap qualityBitmap = ImageUtils.compressByQuality(bitmap, quality);
                ImageUtils.save(qualityBitmap, file, Bitmap.CompressFormat.PNG);
                FileUtils.delete(filePath);
                resultFilePath = file.toString();
                return Uri.fromFile(file);
            }

            @Override
            public void onSuccess(Uri result) {
                if (callBack != null) {
                    callBack.onSuccess(resultFilePath, result);
                }
            }
        });
    }


//    private Handler saveHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            if (msg.what == code) {
//
//            }
//            return false;
//        }
//    });


    public interface SaveImgCallBack {

        void onSuccess(String filePath, Uri uri);

    }


    void recycle() {
        if (mContext != null) {
            mContext = null;
        }
//        if (saveHandler != null) {
//            saveHandler.removeMessages(code);
//        }
//        ThreadUtils.cancel();
    }

}
