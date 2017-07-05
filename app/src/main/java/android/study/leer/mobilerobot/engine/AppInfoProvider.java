package android.study.leer.mobilerobot.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.study.leer.mobilerobot.utils.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leer on 2017/3/8.
 */

public class AppInfoProvider {
    public static List<AppInfo> getAppInfoList(Context context){
        //包装好了的已安装应用的列表
        List<AppInfo> appInfoList = new ArrayList<>();

        //已安装应用的包名列表
        PackageManager pm = context.getPackageManager();

        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        for(PackageInfo packageInfo:packageInfoList){
            AppInfo appInfo = new AppInfo();

            appInfo.setAppPackage(packageInfo.packageName);
            appInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
            appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
            //判断是否为系统应用
            if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)
                    == ApplicationInfo.FLAG_SYSTEM){
                appInfo.setSystem(true);
            }else{
                appInfo.setSystem(false);
            }

            //判断是否安装在sd卡中
            if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)
                == ApplicationInfo.FLAG_EXTERNAL_STORAGE){
                appInfo.setInstallSystem(false);
            }else{
                appInfo.setInstallSystem(true);
            }

            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
