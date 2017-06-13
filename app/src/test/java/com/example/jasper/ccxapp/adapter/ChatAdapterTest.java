package com.example.jasper.ccxapp.adapter;

import android.content.Context;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import com.example.jasper.ccxapp.adapter.ChatAdapter;
import com.example.jasper.ccxapp.ui.ChatActivity;

/**
 * Created by DPC on 2017/6/8.
 */
public class ChatAdapterTest {
    private ArrayList groupInfos;

    public ArrayList Ing(){
        groupInfos = new ArrayList();
        groupInfos.add(0,"a");
        groupInfos.add(1,"b");
        return groupInfos;
    }
    @Test
    public void getCountTest() throws Exception {
        this.groupInfos=Ing();
        assertEquals(2,groupInfos.size());//正向
        System.out.println(groupInfos.size());
    }
    @Ignore
    @Test
    public void getItem() throws Exception {

    }

    @Test
    public void getItemIdTest() throws Exception {
        int two= 2;
        long two2 = 2;
        assertEquals(2,two2);
        System.out.println("two2的实际结果是："+two2);
    }
    @Ignore
    @Test
    public void getView() throws Exception {

    }

}