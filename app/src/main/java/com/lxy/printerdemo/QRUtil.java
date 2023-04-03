package com.lxy.printerdemo;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;


import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


public class QRUtil {
    private static final String CODE = "utf-8";
    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;
    private static final String type = "png";

    /**
     * 生成RQ二维码
     *
     * @param str    内容
     * @param height 高度（px）
     * @author liudeyu
     */
    public static Bitmap getRQBMP(String str, Integer height) {
        if (height == null) {
            height = 240;
        }
        if (height > 400) {
            height = 400;
        }
        height = height - height % 8;
        try {
            // 文字编码
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, CODE);
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
//			LogUtil.si(QRUtil.class, "生成二维码height = " + height);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, height, height, hints);

            return toBufferedImageBMP(bitMatrix);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成一维码（128）
     *
     * @param str
     * @param width
     * @param height
     * @return
     * @author liudeyu
     */
    public static Bitmap getBarcodeBMP(String str, Integer width, Integer height) {

        if (width == null) {
            width = 240;
        }
        if (width > 400) {
            width = 400;
        }
        width = width - width % 8;
        try {
            // 文字编码
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, CODE);
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//			LogUtil.si(QRUtil.class, "生成一维码width = " + width + " height = " + height);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(str, BarcodeFormat.CODE_128, width, height, hints);

            return toBufferedImageBMP(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap createQRCode(String str, int widthAndHeight) {
        if (widthAndHeight > 400) {
            widthAndHeight = 400;
        }
        widthAndHeight = widthAndHeight - widthAndHeight % 8;
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                } else {
                    pixels[y * width + x] = WHITE;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap createQRCode(String str, int widthAndHeight, BarcodeFormat format) {
        if (widthAndHeight > 400) {
            widthAndHeight = 400;
        }
        widthAndHeight = widthAndHeight - widthAndHeight % 8;
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(str, format, widthAndHeight, widthAndHeight);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                } else {
                    pixels[y * width + x] = WHITE;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static Bitmap toBufferedImageBMP(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        if (width % 8 != 0) {
            // 如果不是8的倍数，打印肯定异常，直接返回空
            return null;
        }
        int[] pixels = new int[width * height];
        Bitmap bitmap;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}
