package net.llc.byqfy.rw.launcher;

import android.app.*;
import android.os.*;
import android.util.*;
import dalvik.system.*;
import java.io.*;
import java.lang.reflect.*;
import java.lang.ref.*;
import android.preference.*;

/**
 * Created by hp on 2016/4/6.
 */
public class RWLauncher extends Application
{
	
	private static WeakReference<Application> referen = null;
	public static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RWLauncher/";
	
	
	@Override
	public void onCreate()
	{
		
		super.onCreate();
		File dir = new File(PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(getApplicationContext());  
		// 获取补丁，如果存在就执行注入操作
		String dexPath = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/rustedWarfare/patch.dex");
		File file = new File(dexPath);
		if (file.exists())
		{
			inject(dexPath);
		}
		else
		{
			Log.e("BugFixApplication", dexPath + "不存在");
		}
		
		referen = new WeakReference<Application>(this);
	}

	/**
	 * 要注入的dex的路径
	 *
	 * @param path
	 */
	private void inject(String path)
	{
		try
		{
			// 获取classes的dexElements
			Class<?> cl = Class.forName("dalvik.system.BaseDexClassLoader");
			Object pathList = getField(cl, "pathList", getClassLoader());
			Object baseElements = getField(pathList.getClass(), "dexElements", pathList);

			// 获取patch_dex的dexElements（需要先加载dex）
			String dexopt = getDir("dexopt", 0).getAbsolutePath();
			DexClassLoader dexClassLoader = new DexClassLoader(path, dexopt, dexopt, getClassLoader());
			Object obj = getField(cl, "pathList", dexClassLoader);
			Object dexElements = getField(obj.getClass(), "dexElements", obj);

			// 合并两个Elements
			Object combineElements = combineArray(dexElements, baseElements);

			// 将合并后的Element数组重新赋值给app的classLoader
			setField(pathList.getClass(), "dexElements", pathList, combineElements);

			//======== 以下是测试是否成功注入 =================
			Object object = getField(pathList.getClass(), "dexElements", pathList);
			int length = Array.getLength(object);
			
			Log.e("BugFixApplication", "length = " + length);
			
			
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static Application get(){
		return referen.get();
	}

	/**
	 * 通过反射获取对象的属性值
	 */
	private Object getField(Class<?> cl, String fieldName, Object object) throws NoSuchFieldException, IllegalAccessException
	{
		Field field = cl.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(object);
	}

	/**
	 * 通过反射设置对象的属性值
	 */
	private void setField(Class<?> cl, String fieldName, Object object, Object value) throws NoSuchFieldException, IllegalAccessException
	{
		Field field = cl.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(object, value);
	}

	/**
	 * 通过反射合并两个数组
	 */
	private Object combineArray(Object firstArr, Object secondArr)
	{
		int firstLength = Array.getLength(firstArr);
		int secondLength = Array.getLength(secondArr);
		int length = firstLength + secondLength;

		Class<?> componentType = firstArr.getClass().getComponentType();
		Object newArr = Array.newInstance(componentType, length);
		for (int i = 0; i < length; i++)
		{
			if (i < firstLength)
			{
				Array.set(newArr, i, Array.get(firstArr, i));
			}
			else
			{
				Array.set(newArr, i, Array.get(secondArr, i - firstLength));
			}
		}
		return newArr;
	}
	
	
}
