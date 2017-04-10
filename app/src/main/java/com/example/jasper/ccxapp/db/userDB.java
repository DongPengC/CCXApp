package com.example.jasper.ccxapp.db;

import android.util.Log;

import com.example.jasper.ccxapp.entities.User;
import com.example.jasper.ccxapp.interfaces.userBackListener;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/3/25 0025.
 */

public class userDB {

    //根据用户名删除信息
    public static void deleteUser(String userName, final userBackListener ubl) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", userName);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    for(User user : list){
                        user.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Log.d("user", "删除信息成功");
                                    ubl.showResult(true, null);
                                }else{
                                    Log.e("user", "删除信息失败"+e.getMessage());
                                    ubl.showResult(false, e.getMessage());
                                }
                            }
                        });
                    }
                }else{
                    Log.e("user", "删除信息失败"+e.getMessage());
                    ubl.showResult(false, e.getMessage());
                }
            }
        });
    }

    //需输入用户名，密码新建用户
    public static void addNewUser(String userName, String pwd, final userBackListener ubl) {
        User user = getUser(userName, pwd);
        user.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    Log.d("user", "添加用户成功");
                    ubl.showResult(true, null);
                }else{
                    Log.e("user", "创建数据失败：" + e.getMessage());
                    ubl.showResult(false, e.getMessage());
                }
            }
        });
    }

    //更改用户信息，需输入原有的用户名，新的密码不需修改则为空
    public static void chaUser(String userName, final String password, final userBackListener ubl) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", userName);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    for(User user : list){
                        if(password != null && !password.equals("")) {
                            user.setPassword(password);
                        }
                        user.update(user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Log.d("user", "修改信息成功");
                                    ubl.showResult(true, null);
                                }else{
                                    Log.e("user", "修改信息失败"+e.getMessage());
                                    ubl.showResult(false, e.getMessage());
                                }
                            }
                        });
                    }
                }else{
                    Log.e("user", "修改信息失败"+e.getMessage());
                    ubl.showResult(false, e.getMessage());
                }
            }
        });
    }

    //查询用户，通过用户名查询用户信息
    public static void seaUser(String userName, final userBackListener ubl) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", userName);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    for(User user : list) {
                        Log.d("user", "查询信息成功");
                        ubl.showResult(true, "用户名："+user.getUsername());
                    }
                }else {
                    Log.e("user", "查询信息失败" + e.getMessage());
                    ubl.showResult(false, e.getMessage());
                }
            }
        });
    }

    //判断是否成功登陆
    public static void forUserLogin(String userName, final String password, final userBackListener ubl) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", userName);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    for(User user : list) {
                        if(user.getPassword().equals(password)) {
                            Log.d("user", "登陆成功");
                            ubl.showResult(true, "");
                        }else{
                            Log.d("user", "登陆失败,密码错误");
                            ubl.showResult(false, e.getMessage());
                        }
                    }
                    if(list.size() == 0){
                        Log.d("user", "登陆失败,不存在此用户");
                        ubl.showResult(false, e.getMessage());
                    }
                }else {
                    Log.e("user", "登陆失败" + e.getMessage());
                    ubl.showResult(false, e.getMessage());
                }
            }
        });
    }

    private static User getUser(String userName, String password) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        return user;
    }
}
