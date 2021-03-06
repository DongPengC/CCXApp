package com.example.jasper.ccxapp.ui.application;

import android.app.Application;
import android.content.Context;

import com.example.jasper.ccxapp.db.CreateMessageDB;
import com.example.jasper.ccxapp.util.InitSourceFolderUtil;
import cn.bmob.v3.Bmob;
import cn.jpush.im.android.api.JMessageClient;


/**
 * Created by Jasper on 2017/3/21.
 */

public class BaseApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "11c7be8b5fad9ba573d2641f495dbc48");//初始化Bmob

        // You can enable debug mode in developing state. You should close debug mode when release.
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this,true);

        //创建资源文件目录
        InitSourceFolderUtil.createFileDirs();

        //创建本地数据库
        CreateMessageDB createMessageDB = new CreateMessageDB(getApplicationContext());
        createMessageDB.open();

        mContext = getApplicationContext();
    }



    public static Context getContext(){
        return mContext;
    }
}
