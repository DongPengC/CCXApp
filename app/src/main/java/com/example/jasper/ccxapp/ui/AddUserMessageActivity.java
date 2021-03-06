package com.example.jasper.ccxapp.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.jasper.ccxapp.R;
import com.example.jasper.ccxapp.db.userDB;
import com.example.jasper.ccxapp.interfaces.userBackListener;
import com.example.jasper.ccxapp.util.ImageUtil;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

import static com.example.jasper.ccxapp.util.ImageUtil.ACTIVITY_RESULT_ALBUM;
import static com.example.jasper.ccxapp.util.ImageUtil.ACTIVITY_RESULT_IMAGE;

public class AddUserMessageActivity extends AppCompatActivity {

    private ImageView message_image;
    private Button btn_image;
    private EditText userName;
    private EditText nickName;
    private RadioGroup message_sex;
//    private EditText message_birthday;
    private EditText message_address;
    private EditText message_explain;
    private Button add_message;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String oriNickName;
    private UserInfo.Gender oriSex;
    private Long oriBirthday;
    private String oriAddress;
    private String oriExplain;
    private ImageUtil imageUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_message);

        Toast.makeText(this, "注册完成，请填写详细信息", Toast.LENGTH_SHORT).show();

        message_image = (ImageView)findViewById(R.id.add_message_image);
        btn_image = (Button)findViewById(R.id.add_message_image_btn);
        userName = (EditText)findViewById(R.id.message_userName);
        nickName = (EditText)findViewById(R.id.message_nickname);
        message_sex = (RadioGroup)findViewById(R.id.message_sex);
//        message_birthday = (EditText)findViewById(R.id.message_birthday);
        message_address = (EditText)findViewById(R.id.message_address);
        message_explain = (EditText)findViewById(R.id.message_explain);
        add_message = (Button)findViewById(R.id.add_message_btn);
        imageUtils = new ImageUtil(AddUserMessageActivity.this);

        userName.setText(getIntent().getStringExtra("userName"));
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDialog();
            }
        });

        add_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("确认添加信息?");
            }
        });

        addOriMessage();
    }

    private void chooseDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择头像")
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        imageUtils.byAlbum();
                    }
                })
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String status = Environment.getExternalStorageState();
                        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否存在SD卡
                            imageUtils.byCamera();
                        }
                    }
                }).show();

    }

    // 这里需要注意resultCode，正常情况返回值为 -1 没有任何操作直接后退则返回 0
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("-->requestCode:" + requestCode + "-->resultCode:" + resultCode);

        switch (requestCode) {
            case ImageUtil.ACTIVITY_RESULT_CAMERA: // 拍照
                try {
                    if (resultCode == -1) {
                        imageUtils.cutImageByCamera();
                    } else {
                        // 因为在无任何操作返回时，系统依然会创建一个文件，这里就是删除那个产生的文件
                        if (imageUtils.picFile != null) {
                            imageUtils.picFile.delete();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACTIVITY_RESULT_ALBUM:
                try {
                    if (resultCode == -1) {
                        Uri selectedImage = data.getData();
                        imageUtils.cutImageByAlbumIntent(selectedImage);
                    } else {
                        // 因为在无任何操作返回时，系统依然会创建一个文件，这里就是删除那个产生的文件
                        if (imageUtils.picFile != null) {
                            imageUtils.picFile.delete();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACTIVITY_RESULT_IMAGE:
                try {
                    if (resultCode == -1) {
                        Bitmap bm_icon = imageUtils.decodeBitmap();
                        if (bm_icon != null) {
                            message_image.setImageBitmap(bm_icon);
                        }
                    } else {
                        // 因为在无任何操作返回时，系统依然会创建一个文件，这里就是删除那个产生的文件
                        if (imageUtils.picFile != null) {
                            imageUtils.picFile.delete();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void addOriMessage() {
        //获得读取本地数据权限
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        }
        oriNickName = getIntent().getStringExtra("userName");
        oriSex = UserInfo.Gender.male;
        oriBirthday = Long.valueOf(19900101);
        oriAddress = "北京";
        oriExplain = "";
        String password = getIntent().getStringExtra("password");
        userDB.forUserLogin(oriNickName, password, new userBackListener() {
            @Override
            public void showResult(boolean result, String message) {
                if(!result){
                    showDialog2("联网错误！");
                }else{
                    userDB.addUserMessage(null, oriNickName, oriSex, oriBirthday, oriAddress,
                            oriExplain, new userBackListener() {
                                @Override
                                public void showResult(boolean result, String message) {
                                    if(!result){
                                        showDialog2("联网错误！");
                                    }
                                }
                            });
                }
            }
        });
    }

    private void saveUserMessage() {
        boolean flag = false;
        File imagePath = imageUtils.picFile;
        String nickname = nickName.getText().toString().trim();
        int sexid = message_sex.getCheckedRadioButtonId();
        UserInfo.Gender sex;
        if(sexid == R.id.female){
            sex = UserInfo.Gender.female;
        }else{
            sex = UserInfo.Gender.male;
        }
//        Long birthday = Long.valueOf(message_birthday.getText().toString().trim());
        String address = message_address.getText().toString().trim();
        String explain = message_explain.getText().toString().trim();

        if(nickname.equals(oriNickName) || nickname.equals("")){
            nickname = null;
        }else{
            flag = true;
        }
        if(oriSex == sex){
            sex = null;
        }else{
            flag = true;
        }
//        if(birthday.equals(oriBirthday)){
//            birthday = null;
//        }else{
//            flag = true;
//        }
        if(address.equals(oriAddress)){
            address = null;
        }else{
            flag = true;
        }
        if(explain.equals(oriExplain)){
            explain = null;
        }else{
            flag = true;
        }
        if(imagePath != null && imagePath.exists()){
            flag = true;
        }
        if(flag) {
            userDB.addUserMessage(imagePath, nickname, sex, null, address, explain, new userBackListener() {
                @Override
                public void showResult(boolean result, String message) {
                    if (result) {
                        Toast.makeText(AddUserMessageActivity.this, "添加信息成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddUserMessageActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AddUserMessageActivity.this, "添加信息失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(AddUserMessageActivity.this, "添加信息成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddUserMessageActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this).setTitle("系统提示").setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveUserMessage();
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void showDialog2(String message) {
        new AlertDialog.Builder(this).setTitle("系统提示").setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(AddUserMessageActivity.this, LoginActivity.class));
                        finish();
                    }
                }).show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            JMessageClient.logout();
            Intent myIntent;
            myIntent = new Intent(AddUserMessageActivity.this, LoginActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return false;
    }
}
