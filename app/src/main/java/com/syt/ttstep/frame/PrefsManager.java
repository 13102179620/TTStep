package com.syt.ttstep.frame;

import android.content.Context;

public class PrefsManager  {
    private  Context mContext;
    private static final String PERFERENCE_NAME ="tt_step";
    public PrefsManager(final Context context){
        this.mContext = context;
    }



    //清理配置信息
    public void clear(){
        mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();
    }



    //检查文件是否存在
    public boolean contains(){
        return  mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE)
                .contains(PrefsManager.PERFERENCE_NAME);
    }

    public boolean getBoolean(String key){
        return mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE )
                .getBoolean(key ,false);
    }

    public boolean getBooleanDefaultTrue(String key){
        return mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE )
                .getBoolean(key ,true);
    }


    public Float getFloat(final String key)
    {
        return this.mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE).getFloat(key, 0.0f);
    }

    public int getInt(final String key)
    {
        return this.mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE).getInt(key, 0);
    }

    public long getLong(final String key)
    {
        return this.mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE).getLong(key, 0L);
    }

    public String getString(final String key)
    {
        return this.mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE).getString(key, "");
    }

    public boolean putBoolean(final String key, final boolean value)
    {
        return this.mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value)
                .commit();
    }

    public boolean putFloat(final String key, final Float value)
    {
        return this.mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE).edit().putFloat(key, value).commit();
    }

    public boolean putInt(final String key, final int value)
    {
        return this.mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    public boolean putLong(final String key, final Long value)
    {
        return this.mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE).edit().putLong(key, value).commit();
    }

    public boolean putString(final String key, final String value)
    {
        return this.mContext.getSharedPreferences(PrefsManager.PERFERENCE_NAME, Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }

}
