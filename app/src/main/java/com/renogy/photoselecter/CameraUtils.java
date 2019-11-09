//package com.renogy.photoselecter;
//
//import android.app.Activity;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.v4.content.FileProvider;
//import android.support.v4.os.EnvironmentCompat;
//import android.text.TextUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
///**
// * @author wyb
// * Date :2019/11/8 0008 9:52
// * Description: 打开相机，拍照的工具类
// * 适配 android 10
// */
//public class CameraHelper {
//    /**
//     * 是否是Android 10以上手机
//     */
//    private Activity mContext;
//
//    private int code = 0x321;
//    /**
//     * your provider android:authorities
//     * 你的清单文件中的provider中的authorities
//     */
//    private String authorities = "com.renogy.photo.provider";
//    /**
//     * 图片路径
//     */
//    private String imgName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//    ;
//    private Uri mCameraUri = null;
//    private String filePath = "";
//
//    private int getCode() {
//        return code;
//    }
//
//    private Uri getmCameraUri() {
//        return mCameraUri;
//    }
//
//    private String getImgName() {
//        return imgName;
//    }
//
//    private String getFilePath() {
//        return filePath;
//    }
//
//    private void init(Activity context, ImageBean imageBean) {
//        if (context == null) {
//            throw new NullPointerException("context 参数不能为空");
//        }
//        mContext = context;
//        if (imageBean == null) return;
//        if (!TextUtils.isEmpty(imageBean.getAuthorities())) {
//            authorities = imageBean.getAuthorities();
//        }
//        if (!TextUtils.isEmpty(imageBean.getFilePath())) {
//            filePath = imageBean.getFilePath();
//        }
//        if (imageBean.getCode() != 0) {
//            code = imageBean.getCode();
//        }
//        if (!TextUtils.isEmpty(imageBean.getImgName())) {
//            imgName = imageBean.getImgName();
//        }
//    }
//
//    //打开相机
//    private void openCamera() {
//        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // 判断是否有相机
//        if (captureIntent.resolveActivity(mContext.getPackageManager()) != null) {
//            File photoFile = null;
//            Uri photoUri = null;
//            if (Build.VERSION.SDK_INT >= 29) {
//                // 适配android 10
//                photoUri = createImageUri();
//            } else {
//                try {
//                    photoFile = createImageFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (photoFile != null) {
//                    filePath = photoFile.getAbsolutePath();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
//                        photoUri = FileProvider.getUriForFile(mContext, authorities, photoFile);
//                    } else {
//                        photoUri = Uri.fromFile(photoFile);
//                    }
//                }
//            }
//            mCameraUri = photoUri;
//            if (photoUri != null) {
//                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                mContext.startActivityForResult(captureIntent, code);
//            }
//        }
//    }
//
//
//    /**
//     * 创建保存图片的文件
//     *
//     * @return
//     * @throws IOException
//     */
//    private File createImageFile() throws IOException {
//        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        if (storageDir != null && !storageDir.exists()) {
//            storageDir.mkdir();
//        }
//        File tempFile = new File(storageDir, imgName);
//        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
//            return null;
//        }
//        return tempFile;
//    }
//
//    /**
//     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
//     *
//     * @return 图片的uri
//     */
//    private Uri createImageUri() {
//        String status = Environment.getExternalStorageState();
//        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
//        if (status.equals(Environment.MEDIA_MOUNTED)) {
//            return mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
//        } else {
//            return mContext.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
//        }
//    }
//
//}
