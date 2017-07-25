package net.llc.byqfy.rw.launcher.ui;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import net.llc.byqfy.rw.launcher.*;
import android.content.*;
import java.util.*;
import android.content.pm.*;
import android.content.pm.PackageManager.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		PackageManager pm = getPackageManager();
		PackageInfo info = null;
		try
		{
			info = pm.getPackageInfo("com.corrodinggames.rts", 0);
			Toast.makeText(this, info.applicationInfo.sourceDir,0).show();
		}
		catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}

    }
}
