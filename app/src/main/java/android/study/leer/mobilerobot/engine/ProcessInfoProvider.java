package android.study.leer.mobilerobot.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.AppInfo;
import android.text.format.Formatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Leer on 2017/3/10.
 */

public class ProcessInfoProvider {
    /**
     * @param context 上下文环境
     * @return 运行内存的总数
     */
    public static int processCount(Context context){
        //1.获取activitymanager管理者对象
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取进程总数的列表
        List<ActivityManager.RunningAppProcessInfo> runningAppList = am.getRunningAppProcesses();
        int appCount = runningAppList.size();
        return appCount;
    }

    /**
     * @param context 上下文环境
     * @return 剩余内存的总数  bytes
     */
    public static long getSpaceMemory(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        long availSpace = memoryInfo.availMem;
        return availSpace;
    }

    /**
     * @param context 上下文环境
     * @return 运行的总内存数 bytes
     */
    public static long getTotalMemory(Context context){
        //方法一:
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        long totalSpace = memoryInfo.totalMem;

//        方法二:
        //每一款手机的配置信息都会写入到本身的系统文件当中去
        //这个文件是:proc/meminfo
//        File file = new File("proc/meminfo");
//        StringBuilder stringBuilder = new StringBuilder();
//        try {
//            Scanner scanner = new Scanner(file);
//            String totalMemInfo = scanner.next();
//            char[] infochars = totalMemInfo.toCharArray();
//            for(char c:infochars){
//                if(c >= '0' && c <= '9'){
//                    stringBuilder.append(c);
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        totalSpace = Integer.parseInt(stringBuilder.toString());
        return totalSpace;
    }

    /** 获取封装好了的所有正在运行进程的列表信息
     * @param context 上下文环境
     * @return  正在运行进程的列表(已经被ProcessInfo封装好了的)
     */
    public static List<ProcessInfo> getAppInfos(Context context){
        List<ProcessInfo> processInfos = new ArrayList<>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info:runningAppProcessInfos){
            ProcessInfo processInfo = new ProcessInfo();
//            设置应用程序所在的包名
            processInfo.setAppPackage(info.processName);

//            应用程序占用内存的信息
            //1.获取进程占用的内存大小(传递一个进程对应的pid数组)
            android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            //2.返回数组中索引位置为0的对象,为当前进程的内存信息的对象
            android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //3.获取已使用的大小
            String memoryStr = Formatter.formatFileSize(context,memoryInfo.getTotalPrivateDirty()*1024);
            processInfo.setAppMemory(memoryStr);

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(info.processName,0);
//                设置应用的图标
                processInfo.setAppIcon(applicationInfo.loadIcon(pm));
//                设置应用的名称
                processInfo.setAppName(applicationInfo.loadLabel(pm).toString());

//                判断是否是系统进程
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
                    processInfo.setSystem(true);
                }else{
                    processInfo.setSystem(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                //有的应用没有设置应用名称和或者找不到应用图标需要处理
                processInfo.setAppName(info.processName);;
                processInfo.setAppIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                processInfo.setSystem(true);
                e.printStackTrace();
            }

            processInfos.add(processInfo);
        }
        return processInfos;
    }

    public static void killProcess(Context ctx ,ProcessInfo processInfo){
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(processInfo.getAppPackage());
    }

    /** 清理所有正在运行的进程
     * @param ctx 上下文环境
     */
    public static void killProcessAll(Context ctx){
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info:runningAppProcessInfoList){
            am.killBackgroundProcesses(info.processName);
        }
    }
}
