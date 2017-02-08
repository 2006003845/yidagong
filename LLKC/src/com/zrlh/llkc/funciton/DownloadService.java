package com.zrlh.llkc.funciton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.GameDetailActivity;
import com.zrlh.llkc.activity.MainActivity;
import com.zzl.zl_app.util.Tools;

/*
 * 从服务上下载
 * */
public class DownloadService extends Service {
    private static final int NOTIFY_DOW_ID = 0;
    private static final int NOTIFY_OK_ID = 1;

    private Context mContext = this;
    private boolean cancelled;
    private int progress;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private DownloadBinder binder = new DownloadBinder();

    private String serverUrl = ""; // 安装包下载地址

    // private final String fileName = LlkcBody.APP_NAME; // 显示的文件名称
    private String apkName = "default.apk";// 下载文件的名称

    private int fileSize; // 文件大小
    private int readSize; // 读取长度
    private int downSize; // 已下载大小
    private File downFile; // 下载的文件

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 0:
                // 更新进度
                RemoteViews contentView = mNotification.contentView;
                contentView.setTextViewText(R.id.rate, (readSize < 0 ? 0 : readSize) + "b/s   " + msg.arg1 + "%");
                contentView.setProgressBar(R.id.progress, 100, msg.arg1, false);

                // 更新UI
                mNotificationManager.notify(NOTIFY_DOW_ID, mNotification);

                Intent i = new Intent(GameDetailActivity.MESSAGE_RECEIVED_ACTION);
                i.putExtra("progress", msg.arg1 + "%");
                i.putExtra("isfinish", false);
                mContext.sendBroadcast(i);

                break;
            case 1:
                mNotificationManager.cancel(NOTIFY_DOW_ID);
                createNotification(NOTIFY_OK_ID);

                /* 打开文件进行安装 */
                openFile(downFile);
                Intent i2 = new Intent(GameDetailActivity.MESSAGE_RECEIVED_ACTION);
                i2.putExtra("progress", "100%");
                i2.putExtra("isfinish", true);
                mContext.sendBroadcast(i2);
                break;
            case 2:
                mNotificationManager.cancel(NOTIFY_DOW_ID);
                break;
            }
        };
    };

    private Handler handMessage = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                Toast.makeText(mContext, Tools.getStringFromRes(mContext, R.string.server_nonet), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(mContext, Tools.getStringFromRes(mContext, R.string.server_nofile), Toast.LENGTH_SHORT).show();
                break;
            }

            handler.sendEmptyMessage(2);
        }
    };

    boolean other = false;
    String name = "易打工";

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent == null)
            return;
        serverUrl = intent.getStringExtra("updateURL");
        other = intent.getBooleanExtra("other", false);
        name = (String) intent.getStringExtra("name");
        apkName = name + ".apk";
        if (name == null)
            name = Tools.getStringFromRes(mContext, R.string.app_name);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        cancelled = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 返回自定义的DownloadBinder实例
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelled = true; // 取消下载线程
    }

    /**
     * 创建通知
     */
    private void createNotification(int notifyId) {
        int icon = R.drawable.ic_launcher;
        if (other)
            icon = R.drawable.game_default;
        switch (notifyId) {
        case NOTIFY_DOW_ID:

            CharSequence tickerText = name + Tools.getStringFromRes(mContext, R.string.download);
            long when = System.currentTimeMillis();
            mNotification = new Notification(icon, tickerText, when);
            // 放置在"正在运行"栏目中
            mNotification.flags = Notification.FLAG_ONGOING_EVENT;
            RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.download_notification_layout);
            contentView.setTextViewText(R.id.fileName, Tools.getStringFromRes(mContext, R.string.download_now) + name);
            if (other)
                contentView.setImageViewResource(R.id.imageView, R.drawable.game_default);

            // 指定个性化视图
            mNotification.contentView = contentView;

            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 指定内容意图
            mNotification.contentIntent = contentIntent;

            break;
        case NOTIFY_OK_ID:

            CharSequence tickerText2 = name + Tools.getStringFromRes(mContext, R.string.download_finish);
            long when2 = System.currentTimeMillis();
            mNotification = new Notification(icon, tickerText2, when2);

            // 放置在"通知形"栏目中
            mNotification.flags = Notification.FLAG_AUTO_CANCEL;
            intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);

            // 调用getMIMEType()来取得MimeType
            String type = getMIMEType(downFile);
            // 设定intent的file与MimeType
            intent.setDataAndType(Uri.fromFile(downFile), type);
            PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
            mNotification.contentIntent = pendingintent;
            mNotification.setLatestEventInfo(mContext, name, name + Tools.getStringFromRes(mContext, R.string.download_finish2), pendingintent);
            stopSelf();// 停掉服务自身
            // Toast.makeText(DownloadService.this, "下载完成",
            // Toast.LENGTH_SHORT).show();

            cancelled = true;
            break;
        }

        // 最后别忘了通知一下,否则不会更新
        mNotificationManager.notify(notifyId, mNotification);
    }

    /**
     * 下载模块
     */
    private void startDownload() {

        // 初始化数据
        fileSize = 0;
        readSize = 0;
        downSize = 0;
        progress = 0;

        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL myURL = new URL(serverUrl); // 取得URL
            URLConnection conn = myURL.openConnection(); // 建立联机
            conn.connect();
            fileSize = conn.getContentLength(); // 获取文件长度
            is = conn.getInputStream(); // InputStream 下载文件
            if (is == null) {
                throw new RuntimeException("stream is null");
            }

            if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {// 判断是否有sd卡
                // 创建目录
                File apkDir = new File(LlkcBody.APK_SD_PATH);
                if (!apkDir.exists()) {
                    apkDir.mkdir();
                }
                downFile = new File(apkDir + apkName);
                if (downFile.exists())
                    downFile.delete();
                downFile = new File(apkDir + apkName);
            } else {
                // 未挂载sd卡，保存至手机内置存储空间
                String apkPath = LlkcBody.APK_DATA_PATH + mContext.getPackageName() + "/" + apkName;
                // 修改apk权限
                BaseHelper.chmod("777", apkPath);
                downFile = new File(apkPath);
                if (downFile.exists())
                    downFile.delete();
                downFile = new File(apkPath);
            }
            // 将文件写入临时盘
            fos = new FileOutputStream(downFile);
            byte buf[] = new byte[1024 * 1024];
            while (!cancelled && (readSize = is.read(buf)) > 0) {
                fos.write(buf, 0, readSize);
                downSize += readSize;
                sendMessage(0);
            }
            if (cancelled) {
                handler.sendEmptyMessage(2);
                downFile.delete();
            } else {
                handler.sendEmptyMessage(1);
            }
        } catch (MalformedURLException e) {
            handMessage.sendEmptyMessage(0);
        } catch (IOException e) {
            handMessage.sendEmptyMessage(1);
        } catch (Exception e) {
            handMessage.sendEmptyMessage(0);
        } finally {
            try {
                if (null != fos)
                    fos.close();
                if (null != is)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(int what) {
        int num = (int) ((double) downSize / (double) fileSize * 100);

        if (num > progress + 1) {
            progress = num;

            Message msg0 = handler.obtainMessage();
            msg0.what = what;
            msg0.arg1 = progress;
            handler.sendMessage(msg0);
        }
    }

    // 在手机上打开文件的method
    private void openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);

        // 调用getMIMEType()来取得MimeType
        String type = getMIMEType(f);
        // 设定intent的file与MimeType
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);

    }

    // 判断文件MimeType的method
    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        // 取得扩展名
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

        // 按扩展名的类型决定MimeType
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            // android.permission.INSTALL_PACKAGES
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        // 如果无法直接打开，就跳出软件清单给使用者选择
        if (!end.equals("apk")) {
            type += "/*";
        }

        return type;
    }

    /**
     * DownloadBinder中定义了一些实用的方法
     * 
     * @author user
     */
    public class DownloadBinder extends Binder {
        /**
         * 开始
         */
        public void start() {
            cancelled = false;
            new Thread() {
                public void run() {
                    createNotification(NOTIFY_DOW_ID);
                    startDownload(); // 下载
                    cancelled = true;
                };
            }.start();
        }

        /**
         * 获取进度
         * 
         * @return
         */
        public int getProgress() {
            return progress;
        }

        /**
         * 取消下载
         */
        public void cancel() {
            cancelled = true;
        }

        /**
         * 是否已被取消
         * 
         * @return
         */
        public boolean isCancelled() {
            return cancelled;
        }

    }
}
