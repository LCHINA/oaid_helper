package com.licp.library;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName OaidHelperProxy1_0_13
 * @Description 获取Oaid入口类
 * @Author lchinali
 * @Date 3/8/21 11:30 PM
 * @Version 1.0
 */
public class OaidHelper extends Oaid {
    public static String TAG = OaidHelper.class.getName();
    private AppIdsUpdater _listener;
    private String iIdentifierListenerCls = "";
    private ClassLoader loader;
    String[] cls = new String[]{"com.bun.supplier.IIdentifierListener", "com.bun.miitmdid.core.IIdentifierListener", "com.bun.miitmdid.interfaces.IIdentifierListener"};//第一个是1.0.13版本的,第三个为1.0.23
    static String miitInitCls = "com.bun.miitmdid.core.JLibrary";

    public OaidHelper(AppIdsUpdater callback, ClassLoader loader) {
        _listener = callback;
        this.loader = loader;
        for (String s : cls) {
            try {
                //反射找到存在的类文件，以此判断当前手机的miit版本是多少
                Class.forName(s);
                iIdentifierListenerCls = s;
                break;
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "miit helper can not load :" + e.getMessage());
            }
        }
    }

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        try {
            Class<?> aClass = Class.forName(miitInitCls);
            Method initEntry = aClass.getDeclaredMethod("InitEntry", Context.class);
            Constructor constructor = aClass.getConstructor(new Class[]{});
            Object newInstance = constructor.newInstance(new Object[]{});
            initEntry.invoke(newInstance, context);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Log.d(TAG, "当前版本JLibrary已经不存在");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取设备oaid
     *
     * @param cxt
     */
    @Override
    public void loadOaid(Context cxt) {
        if (!TextUtils.isEmpty(iIdentifierListenerCls)) {
            //执行对应的代理类
            if ("com.bun.supplier.IIdentifierListener".equals(iIdentifierListenerCls)) {
                OaidHelperProxy1_0_13 miitHelperProxy1013 = new OaidHelperProxy1_0_13(this._listener, loader);
                miitHelperProxy1013.loadOaid(cxt);
            } else if ("com.bun.miitmdid.interfaces.IIdentifierListener".equals(iIdentifierListenerCls)) {
                OaidHelperProxy1_0_23 miitHelperProxy1023 = new OaidHelperProxy1_0_23(this._listener, loader);
                miitHelperProxy1023.loadOaid(cxt);
            } else {
                OaidHelperProxy1 miitHelperProxy1 = new OaidHelperProxy1(this._listener, loader);
                miitHelperProxy1.loadOaid(cxt);
            }
        } else {
            this._listener.onIdsAvalid(false, null, null, null);
        }
    }

    @Override
    AppIdsUpdater getListener() {
        return null;
    }

}
