package com.lxy.printerdemo;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.pos.sdk.printer.POIPrinterManager;
import com.pos.sdk.printer.PosPrinter;
import com.pos.sdk.printer.PosPrinterInfo;
import com.pos.sdk.printer.models.BitmapPrintLine;
import com.pos.sdk.printer.models.PrintLine;
import com.pos.sdk.printer.models.TextPrintLine;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttton1;
    private HandlerThread mHandlerThread;
    public MainHandler mMainHandler;
    private POIPrinterManager printerManager;
    private PrinterListener printer_callback = new PrinterListener();
    public static String Elemo = "ABCDEFGHIJKLMNOPQRSTABCDEFGHIJKLMNOPQRSTABCDEFGHIJKLMNOPQRSTABCDEFGHIJKLMNOP";
    private boolean isSuspend = false;

    private int mTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        buttton1 = (Button) findViewById(R.id.buttton1);
        buttton1.setOnClickListener(this);
        initMainHandlerThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimes = 0;
        printerManager.close();
        isSuspend = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        printerManager = new POIPrinterManager(this);
        printerManager.open();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttton1) {
            mMainHandler.sendEmptyMessage(1);
        }
    }

    private void initMainHandlerThread() {
        mHandlerThread = new HandlerThread("PrinterAgingTest");
        mHandlerThread.start();
        mMainHandler = new MainHandler(mHandlerThread.getLooper());
    }

    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mMainHandler.removeMessages(1);
                printPart1();
                isSuspend = true;
            }
            if (msg.what == 2) {
                mMainHandler.removeMessages(2);
                printPart2();
            }
            if (msg.what == 3) {
                mTimes ++;
                getPrintInfo();
                Log.d(TAG,"mTimes = " + mTimes);
                if (mTimes < 10) {
                    mMainHandler.removeMessages(3);
                    mMainHandler.sendEmptyMessageDelayed(3,500);
                } else {
                    mTimes = 0;
                    mMainHandler.removeMessages(3);
                    mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    private void getPrintInfo() {
        PosPrinterInfo info = new PosPrinterInfo();
        PosPrinter.getPrinterInfo(0, info);
        Log.d(TAG,"PaPer exist = " + (info.mHavePaper==1 ? "true" : "false") + " ; mTemperature = " + info.mTemperature);
        mMainHandler.sendEmptyMessage(3);
    }

    public void printPart1() {
        printerManager.setPrintGray(Integer.valueOf(1600));
        printerManager.setLineSpace(Integer.valueOf(2));
        printerManager.cleanCache();
        TextPrintLine textPrintLine = new TextPrintLine();
        textPrintLine.setType(PrintLine.TEXT);
        textPrintLine.setSize(TextPrintLine.FONT_NORMAL);
        textPrintLine.setPosition(PrintLine.CENTER);
        textPrintLine.setContent(Elemo);
        printerManager.addPrintLine(textPrintLine);
        BitmapPrintLine bitmapPrintLine = new BitmapPrintLine();
        bitmapPrintLine.setType(PrintLine.BITMAP);
        bitmapPrintLine.setPosition(PrintLine.CENTER);
        //create QR code(max width and height is 384px)
        Bitmap bitmap = QRUtil.getRQBMP("www.baidu.com", 180);
        bitmapPrintLine.setBitmap(bitmap);
        printerManager.addPrintLine(bitmapPrintLine);
        printerManager.lineWrap(5);
        printerManager.beginPrint(printer_callback);
    }

    public void printPart2() {
        printerManager.setPrintGray(Integer.valueOf(1600));
        printerManager.setLineSpace(Integer.valueOf(2));
        printerManager.cleanCache();
        BitmapPrintLine bitmapPrintLine = new BitmapPrintLine();
        bitmapPrintLine.setType(PrintLine.BITMAP);
        bitmapPrintLine.setPosition(PrintLine.CENTER);
        //create QR code(max width and height is 384px)
        Bitmap bitmap = QRUtil.getRQBMP("www.baidu.com", 400);
        bitmapPrintLine.setBitmap(bitmap);
        printerManager.addPrintLine(bitmapPrintLine);
        printerManager.lineWrap(5);
        printerManager.beginPrint(printer_callback);
    }

    private class PrinterListener implements POIPrinterManager.IPrinterListener{
        @Override
        public void onStart() {
            Log.d(TAG, "start print");
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "pint success");
            if (isSuspend) {
                isSuspend = false;
                mMainHandler.sendEmptyMessage(3);
            }
        }
        @Override
        public void onError(int i, String detail) {
            Log.d(TAG, "pint onError");
            Toast.makeText(MainActivity.this, detail, Toast.LENGTH_SHORT).show();
        }
    }
}