package net.llc.byqfy.rw.launcher.load;
import android.content.*;
import net.llc.byqfy.rw.launcher.*;
import android.content.pm.*;
import android.content.pm.PackageManager.*;
import android.os.*;
import dalvik.system.*;

public class RWLoader
{
	private Context c = null;
	
	private static RWLoader INSTANCE = new RWLoader(RWLauncher.get());
	
	private RWLoader(Context c) {
		this.c = c;
	}
	
	public static RWLoader getRWLoader() {
		return INSTANCE;
	}
	
	public void loadAndStartRW() throws PackageManager.NameNotFoundException, ClassNotFoundException {
		PackageManager pm = c.getPackageManager();
		PackageInfo info = pm.getPackageInfo("com.corrodinggames.rts", 0);
		String packageName = info.packageName;
		String dexPath = info.applicationInfo.sourceDir;
		DexClassLoader dcl = new DexClassLoader(dexPath,RWLauncher.PATH,null,c.getClassLoader());
	}
}
