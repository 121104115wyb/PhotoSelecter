//package com.renogy.photoselecter;
//
//import java.io.Serializable;
//
///**
// * @author wyb
// * Date :2019/11/8 0008 10:02
// * Description: 打开相机的类
// */
//public class ImageBean implements Serializable {
//    /**
//     * your provider android:authorities
//     * 你的清单文件中的provider中的authorities
//     */
//    private String authorities="";
//    /**
//     * 图片路径
//     */
//    private String filePath="";
//
//    /**
//     * 打开相机请求码
//     */
//    private int code;
//
//    /**
//     * 图片名称
//     */
//    private String imgName="";
//
//    public ImageBean() {
//    }
//
//
//    public ImageBean(String authorities, String filePath, int code, String imgName) {
//        this.authorities = authorities;
//        this.filePath = filePath;
//        this.code = code;
//        this.imgName = imgName;
//    }
//
//    public String getAuthorities() {
//        return authorities;
//    }
//
//    public void setAuthorities(String authorities) {
//        this.authorities = authorities;
//    }
//
//    public String getFilePath() {
//        return filePath;
//    }
//
//    public void setFilePath(String filePath) {
//        this.filePath = filePath;
//    }
//
//    public int getCode() {
//        return code;
//    }
//
//    public String getImgName() {
//        return imgName;
//    }
//
//    public void setImgName(String imgName) {
//        this.imgName = imgName;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//}
