package com.licp.library;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName Oaid
 * @Description 基础类
 * @Author lchinali
 * @Date 3/8/21 11:42 PM
 * @Version 1.0
 */
public abstract class Oaid {
    private static String TAG = Oaid.class.getName();


    public interface AppIdsUpdater {
        void onIdsAvalid(boolean isSupport, String oaid, String vaid, String aaid);
    }

    abstract void loadOaid(Context context);

    abstract AppIdsUpdater getListener();


    /**
     * 通过动态代理创建回调接口
     *
     * @param listenerClsStr
     * @param classLoader
     * @return
     */
    protected Object createIdentifierListener(String listenerClsStr, ClassLoader classLoader) {
        Class cls = null;
        try {
            cls = Class.forName(listenerClsStr, true, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (cls == null) return null;
        //动态代理
        return Proxy.newProxyInstance(classLoader, new Class[]{cls}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = "OnSupport";
                //找到对应的方法，拦截取值
                if (name.equals(method.getName())) {
                    for (Object arg : args) {
                        if (!"java.lang.Boolean".equals(arg.getClass().getName())) {
                            getOaidFromObject(arg);
                        }
                    }
                    return null;
                }
                return method.invoke(proxy, args);
            }
        });
    }

    protected void getOaidFromObject(Object object) {
        try {
            Method oaidMethod = object.getClass().getDeclaredMethod("getOAID");
            String oaid = oaidMethod.invoke(object).toString();
            Method vaidMethod = object.getClass().getDeclaredMethod("getVAID");
            String vaid = vaidMethod.invoke(object).toString();
            Method aaidMethod = object.getClass().getDeclaredMethod("getAAID");
            String aaid = aaidMethod.invoke(object).toString();
//            Method shutDownMethod = object.getClass().getDeclaredMethod("shutDown");
//            shutDownMethod.invoke(object);
            if (getListener() != null) {
                getListener().onIdsAvalid(true, oaid, vaid, aaid);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public void logd(boolean z, String str) {
        if (z) {
            Log.d(TAG, str);
        }
    }

}
