package android.study.leer.mobilerobot.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by Leer on 2017/3/8.
 */

public class AppInfo {
    private String appPackage;
    private String appName;
    private Drawable appIcon;
    private boolean isSystem;
    private boolean isInstallSystem;

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public void setInstallSystem(boolean installSystem) {
        isInstallSystem = installSystem;
    }

    public String getAppPackage() {

        return appPackage;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public boolean isInstallSystem() {
        return isInstallSystem;
    }
}
