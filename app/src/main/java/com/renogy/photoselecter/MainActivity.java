package com.renogy.photoselecter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.renogy.photolibrary.CameraHelper;
import com.renogy.photolibrary.GlideLoadEngine;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mView;
    private CameraHelper cameraHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_select_pic).setOnClickListener(this);
        findViewById(R.id.openCamera).setOnClickListener(this);
        cameraHelper = new CameraHelper(this);
        cameraHelper.setCallBack(callBack);
        mView = findViewById(R.id.iv_photo);
        initPermission();
    }

    private void initPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(
                Manifest.permission.CAMERA
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE
        ).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //申请的权限全部允许
                    Toast.makeText(MainActivity.this, "允许了权限!", Toast.LENGTH_SHORT).show();
                } else {
                    //只要有一个权限被拒绝，就会执行
                    Toast.makeText(MainActivity.this, "未授权权限，部分功能不能使用", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_pic:
                selectPic();
                break;
            case R.id.openCamera:
                cameraHelper.openCamera();

                break;
        }
    }

    private final int REQUEST_CODE_CHOOSE_PHOTO_ALBUM = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO_ALBUM && resultCode == RESULT_OK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            List<Uri> pathList = Matisse.obtainResult(data);
            for (Uri _Uri : pathList) {
                Glide.with(this).load(_Uri).into(mView);
                System.out.println(_Uri.getPath());
            }
        }
        if (requestCode == cameraHelper.getCode() && resultCode == RESULT_OK) {
            cameraHelper.saveAndCompress();
        }
    }

    public CameraHelper.SaveImgCallBack callBack = new CameraHelper.SaveImgCallBack() {
        @Override
        public void onSuccess(String filePath, Uri uri) {
            Log.d("test", "onActivityResult: ----file:" + filePath);
            Glide.with(MainActivity.this).load(filePath).into(mView);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });

        }
    };


    void selectPic() {
        Matisse.from(this)
                //设置选择的类型
                .choose(MimeType.ofImage(), false)
                //限制只显示一种类型的数据
                .showSingleMediaType(true)
                // 使用相机，和 captureStrategy 一起使用
                .capture(true)
                //适配authority 与清单文件中provider中保持一致
                .captureStrategy(new CaptureStrategy(true, "com.jsf.piccompresstest"))
//        R.style.Matisse_Zhihu (light mode)
//        R.style.Matisse_Dracula (dark mode)
                //风格，支持自定义
                .theme(R.style.Matisse_Dracula)
                //使用顺序自增计数器
                .countable(true)
                //最大能选几个
                .maxSelectable(3)
                .addFilter(new Filter() {
                    @Override
                    protected Set<MimeType> constraintTypes() {
                        return new HashSet<MimeType>() {{
                            add(MimeType.PNG);
                        }};
                    }

                    @Override
                    public IncapableCause filter(Context context, Item item) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(item.getContentUri());
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeStream(inputStream, null, options);
                            int width = options.outWidth;
                            int height = options.outHeight;

//                            if (width >= 500)
//                                return new IncapableCause("宽度超过500px");

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }


                        return null;
                    }
                })
//                .gridExpectedSize((int) getResources().getDimension(R.dimen.imageSelectDimen))
                //选择器的方向
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                //选择图片时，的图片的预览图，与原来图片的比例
                .thumbnailScale(0.8f)
                //使用Glide加载器
                .imageEngine(new GlideLoadEngine())
                .originalEnable(true)
                .maxOriginalSize(2)
                .autoHideToolbarOnSingleTap(true)
                //设置返回值标记
                .forResult(REQUEST_CODE_CHOOSE_PHOTO_ALBUM);
    }
}
