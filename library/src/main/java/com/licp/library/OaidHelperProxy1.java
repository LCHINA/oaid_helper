package com.licp.library;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName OaidHelperProxy1
 * @Description 针对1.0.13以下版本获取Oaid
 * @Author lchinali
 * @Date 3/8/21 11:30 PM
 * @Version 1.0
 */
class OaidHelperProxy1 extends Oaid {

    public static String TAG = OaidHelperProxy1.class.getName();
    String listenerClsStr = "com.bun.miitmdid.core.IIdentifierListener";
    String mdidSdkStr = "com.bun.miitmdid.core.MdidSdk";

    private OaidHelper.AppIdsUpdater _listener;
    private boolean canLoadOaid = true;
    private ClassLoader classLoader;


    public OaidHelperProxy1(OaidHelper.AppIdsUpdater callback, ClassLoader loader) {
        this.classLoader = loader;
        _listener = callback;
        try {
            Class.forName(mdidSdkStr, true, this.classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            canLoadOaid = false;
        }

    }


    @Override
    public void loadOaid(Context cxt) {
        if (!canLoadOaid) {
            if (_listener != null) {
                _listener.onIdsAvalid(false, null, null, null);
            }
            return;
        }
        long timeb = System.currentTimeMillis();
        int nres = DirectCall(cxt);
        long timee = System.currentTimeMillis();
        long offset = timee - timeb;
//        public static final int INIT_ERROR_DEVICE_NOSUPPORT = 1008612;不支持的设备
//        public static final int INIT_ERROR_LOAD_CONFIGFILE = 1008613; 加载配置文件出错
//        public static final int INIT_ERROR_MANUFACTURER_NOSUPPORT = 1008611;不支持的设备厂商
//        public static final int INIT_ERROR_RESULT_DELAY = 1008614;获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
//        public static final int INIT_HELPER_CALL_ERROR = 1008615;反射调用出错

        if (nres == 1008612) {//不支持的设备

        } else if (nres == 1008613) {//加载配置文件出错

        } else if (nres == 1008611) {//不支持的设备厂商

        } else if (nres == 1008614) {//获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程

        } else if (nres == 1008615) {//反射调用出错

        }
        Log.d(TAG, "return value: " + String.valueOf(nres));

    }

    @Override
    AppIdsUpdater getListener() {
        return this._listener;
    }

    /*
     * 直接java调用，如果这样调用，在android 9以前没有题，在android 9以后会抛找不到so方法的异常
     * 解决办法是和JLibrary.InitEntry(cxt)，分开调用，比如在A类中调用JLibrary.InitEntry(cxt)，在B类中调用MdidSdk的方法
     * A和B不能存在直接和间接依赖关系，否则也会报错
     *
     * */
    private int DirectCall(Context cxt) {
        String str2 = "InitSdk";
        Class listenerCls = null;
        try {
            listenerCls = Class.forName(listenerClsStr, true, this.classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        boolean z = true;
        Class cls = null;
        try {
            cls = Class.forName(mdidSdkStr, true, this.classLoader);
            if (cls == null || listenerCls == null) {
                return 1008615;
            }
            Constructor constructor = cls.getConstructor(new Class[]{});
            if (constructor == null) {
                logd(z, "not found MdidSdk Constructor");
                return 1008615;
            }
            Object newInstance = constructor.newInstance();
            if (newInstance == null) {
                logd(z, "Create MdidSdk Instance failed");
                return 1008615;
            }
            Method declaredMethod = cls.getDeclaredMethod(str2, new Class[]{Context.class, listenerCls});
            if (declaredMethod == null) {
                logd(z, "not found MdidSdk " + str2 + " function");
                return 1008615;
            }
            Object identifierListener = createIdentifierListener(this.listenerClsStr, this.classLoader);
            if (identifierListener == null) {
                logd(z, "not found IdentifierListener " + str2 + " function");
                return 1008615;
            }
            int intValue = ((Integer) declaredMethod.invoke(newInstance, new Object[]{cxt, identifierListener})).intValue();
            logd(z, "call and retvalue:" + intValue);
            return intValue;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 1008615;
    }

}