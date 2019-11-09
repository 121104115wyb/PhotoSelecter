package com.renogy.photolibrary;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.ImageEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wyb
 * Date :2019/11/8 0008 14:29
 * Description: 选择图片器
 */
public class SelectHelper {
    //是否使用相机
    private Boolean isCapture = false;
    //provider配置权限
    private String authorities = "com.renogy.photo.provider";
    //最大选择图片个数
    private int maxSelectable = 9;
    //选择图片方向
    private int restrictOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    //缩放比
    private float thumbnailScale = 0.8f;
    //最大上传的图片的大小，单位M
    private int maxOriginalSize = 5;

    //预览是否支持自动隐藏标题栏
    private boolean autoHideToolbarOnSingleTap = true;
    //图片加载引擎
    private ImageEngine imageEngine;
    //返回的标识
    private int resultCode = 9999;
    //主题风格
    private int themeId = R.style.Matisse_Zhihu;
    //是否支持只显示一种类型的资源
    private boolean showSingleMediaType = true;

    private Set<MimeType> mimeTypes = MimeType.ofImage();

    public void selectPic(Activity context) {
        Matisse.from(context)
                //设置选择的类型
                .choose(mimeTypes, false)
                //限制只显示一种类型的数据
                .showSingleMediaType(showSingleMediaType)
                // 使用相机，和 captureStrategy 一起使用
                .capture(isCapture)
                //适配authority 与清单文件中provider中保持一致
                .captureStrategy(new CaptureStrategy(true, authorities))
//        R.style.Matisse_Zhihu (light mode)
//        R.style.Matisse_Dracula (dark mode)
                //风格，支持自定义
                .theme(themeId)
                //使用顺序自增计数器
                .countable(true)
                //最大能选几个
                .maxSelectable(maxSelectable)
                .addFilter(new Filter() {
                    @Override
                    protected Set<MimeType> constraintTypes() {
                        return new HashSet<MimeType>() {{
                            add(MimeType.PNG);
                        }};
                    }

                    @Override
                    public IncapableCause filter(Context context, Item item) {
//                        try {
//                            InputStream inputStream = context.getContentResolver().openInputStream(item.getContentUri());
//                            BitmapFactory.Options options = new BitmapFactory.Options();
//                            options.inJustDecodeBounds = true;
//                            BitmapFactory.decodeStream(inputStream, null, options);
//                            int width = options.outWidth;
//                            int height = options.outHeight;
//
////                            if (width >= 500)
////                                return new IncapableCause("宽度超过500px");
//
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }


                        return null;
                    }
                })
//                .gridExpectedSize((int) getResources().getDimension(R.dimen.imageSelectDimen))
                //选择器的方向
                .restrictOrientation(restrictOrientation)
                //选择图片时，的图片的预览图，与原来图片的比例
                .thumbnailScale(thumbnailScale)
                //使用Glide加载器
                .imageEngine(new GlideLoadEngine())
                .originalEnable(true)
                .maxOriginalSize(maxOriginalSize)
                .autoHideToolbarOnSingleTap(autoHideToolbarOnSingleTap)
                //设置返回值标记
                .forResult(resultCode);
    }

    public Boolean getCapture() {
        return isCapture;
    }

    public void setCapture(Boolean capture) {
        isCapture = capture;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public int getMaxSelectable() {
        return maxSelectable;
    }

    public void setMaxSelectable(int maxSelectable) {
        this.maxSelectable = maxSelectable;
    }

    public int getRestrictOrientation() {
        return restrictOrientation;
    }

    public void setRestrictOrientation(int restrictOrientation) {
        this.restrictOrientation = restrictOrientation;
    }

    public float getThumbnailScale() {
        return thumbnailScale;
    }

    public void setThumbnailScale(float thumbnailScale) {
        this.thumbnailScale = thumbnailScale;
    }

    public int getMaxOriginalSize() {
        return maxOriginalSize;
    }

    public void setMaxOriginalSize(int maxOriginalSize) {
        this.maxOriginalSize = maxOriginalSize;
    }

    public boolean isAutoHideToolbarOnSingleTap() {
        return autoHideToolbarOnSingleTap;
    }

    public void setAutoHideToolbarOnSingleTap(boolean autoHideToolbarOnSingleTap) {
        this.autoHideToolbarOnSingleTap = autoHideToolbarOnSingleTap;
    }

    public ImageEngine getImageEngine() {
        return imageEngine;
    }

    public void setImageEngine(ImageEngine imageEngine) {
        this.imageEngine = imageEngine;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public boolean isShowSingleMediaType() {
        return showSingleMediaType;
    }

    public void setShowSingleMediaType(boolean showSingleMediaType) {
        this.showSingleMediaType = showSingleMediaType;
    }


}
