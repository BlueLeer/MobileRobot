package android.study.leer.mobilerobot.engine;

import android.graphics.drawable.Drawable;

/**
 * Created by Leer on 2017/3/10.
 */

public class ProcessInfo {
    private String appName;
    private String appPackage;
    private Drawable appIcon;
    private String appMemory;
    private boolean isSystem;
    private boolean isCheck = false;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getAppMemory() {
        return appMemory;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public void setAppMemory(String appMemory) {
        this.appMemory = appMemory;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}
