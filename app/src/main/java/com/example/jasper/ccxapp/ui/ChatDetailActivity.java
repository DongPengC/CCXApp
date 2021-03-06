package com.example.jasper.ccxapp.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jasper.ccxapp.R;
import com.example.jasper.ccxapp.adapter.ChatMemberAdapter;
import com.example.jasper.ccxapp.db.chatDB;
import com.example.jasper.ccxapp.interfaces.userBackListUserInfo;
import com.example.jasper.ccxapp.interfaces.userBackListener;

import java.io.File;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

public class ChatDetailActivity extends AppCompatActivity {

    private Button add_new_member;
    private Button delete_some_member;
    private Button quit_chat;
    List<UserInfo> userInfos;
    long groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        add_new_member = (Button)findViewById(R.id.add_new_member);
        delete_some_member = (Button)findViewById(R.id.delete_some_member);
        quit_chat = (Button)findViewById(R.id.quit_chat);

        groupId = getIntent().getLongExtra("groupId", groupId);
        boolean ifOwn = getIntent().getBooleanExtra("ifOwn", false);
        if(!ifOwn){
            delete_some_member.setVisibility(View.GONE);
        }

        getChatMember(groupId);
        setListeners();
    }

    private void setListeners() {
        add_new_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] userNames = new String[userInfos.size()];
                for(int i = 0; i < userNames.length; i++){
                    userNames[i] = userInfos.get(i).getUserName();
                }
                Intent intent = new Intent(ChatDetailActivity.this, ChatChangeMemberActivity.class);
                intent.putExtra("userNames", userNames);
                intent.putExtra("type", "add");
                intent.putExtra("ifOwn", getIntent().getBooleanExtra("ifOwn", false));
                intent.putExtra("groupId", groupId);
                startActivity(intent);
                finish();
            }
        });
        delete_some_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] userNames = new String[userInfos.size()-1];
                Bitmap[] bitmaps = new Bitmap[userInfos.size()-1];
                for(int i = 0, j = 0; i < userInfos.size(); i++){
                    if(!userInfos.get(i).getUserName().equals(JMessageClient.getMyInfo().getUserName())){
                        userNames[j] = userInfos.get(i).getUserName();
                        File file = userInfos.get(i).getAvatarFile();
                        bitmaps[j] = BitmapFactory.decodeFile(String.valueOf(file));
                        j++;
                    }
                }
                Intent intent = new Intent(ChatDetailActivity.this, ChatChangeMemberActivity.class);
                intent.putExtra("userNames", userNames);
                intent.putExtra("type", "delete");
                intent.putExtra("groupId", groupId);
                intent.putExtra("bitmaps", bitmaps.length);
                for(int i=0;i<bitmaps.length;i++){
                    intent.putExtra("bitmap"+i, bitmaps[i]);
                }
                startActivity(intent);
                finish();
            }
        });
        quit_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ChatDetailActivity.this).setTitle("系统提示").setMessage("确认退出该群聊？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chatDB.quitChat(groupId, new userBackListener() {
                                    @Override
                                    public void showResult(boolean result, String message) {
                                        if(result){
                                            Toast.makeText(ChatDetailActivity.this, "已退出该群聊", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ChatDetailActivity.this, ChatActivity.class));
                                            finish();
                                        }else{
                                            showDialog("退出群聊失败！");
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
    }

    private void getChatMember(long groupId) {
        chatDB.getChatMember(groupId, new userBackListUserInfo() {
            @Override
            public void showResult(boolean result, List<UserInfo> message) {
                if(result){
                    userInfos = message;
                    showChatMember(message);
                }else{
                    showDialog("查询群聊成员失败");
                }
            }
        });
    }

    private void showChatMember(List<UserInfo> groupMembers) {
        ListView lv = (ListView)findViewById(R.id.show_chat_member);

        ChatMemberAdapter adaptar = new ChatMemberAdapter(ChatDetailActivity.this, groupMembers);
        lv.setAdapter(adaptar);
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this).setTitle("系统提示").setMessage(message)
                .setPositiveButton("确定", null).show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            super.onKeyDown(keyCode, event);
            startActivity(new Intent(ChatDetailActivity.this, ChatActivity.class));
            this.finish();
        }
        return false;
    }
}
