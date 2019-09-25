package com.wray2.Class;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;

enum ROMNameEnum
{
    MIUI("ro.miui.ui.version.name"),
    EMUI("ro.build.version.emui"),
    OPPO("ro.build.version.opporom"),
    SMARTISAN("ro.smartisan.version"),
    VIVO("ro.vivo.os.version");

    private String ROM_KEY_VERSION;

    ROMNameEnum(String romKey)
    {
        ROM_KEY_VERSION = romKey;
    }

    public String getKeyVersion()
    {
        return ROM_KEY_VERSION;
    }
}

public class ROMChecker
{
    public static boolean isMIUI()
    {
        return checkROM(ROMNameEnum.MIUI);
    }

    private static boolean checkROM(ROMNameEnum romName)
    {
        BufferedReader input = null;
        try
        {
            Process p = Runtime.getRuntime().exec("getprop " + romName.getKeyVersion());
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            String line = input.readLine();
            if(line.equals(romName.getKeyVersion()))
                return true;
            else
                return false;
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Unable to read sysprop " + romName.getKeyVersion(), ex);
            return false;
        }
        finally
        {
            if(input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
    }
}