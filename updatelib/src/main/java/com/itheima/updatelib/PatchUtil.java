package com.itheima.updatelib;

public class PatchUtil {

    static {
        System.loadLibrary("bspatch");
    }

    /**
     * 合并生成新版本apk
     * <p>
     * 返回：0，说明操作成功
     *
     * @param oldApkPath 旧版本apk路径
     * @param newApkPath 新版本apk路径
     * @param patchPath  差分包/更新包 路径
     * @return
     */
    public static native int patch(String oldApkPath, String newApkPath,
                                   String patchPath);
}
