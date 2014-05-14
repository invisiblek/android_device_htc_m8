/*
 * Copyright (c) 2014 The CyanogenMod Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * Also add information on how to contact you by electronic and paper mail.
 *
 */

package org.cyanogenmod.dotcase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.WindowManager;

public class DotcaseActivity extends Activity
{
    private static final String TAG = "DotcaseActivity";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Settings.Secure.putString(getContentResolver(),
                                  Settings.Secure.IMMERSIVE_MODE_CONFIRMATIONS,
                                  "org.cyanogenmod.dotcase");
        getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE);
        final DrawView drawView = new DrawView(this);
        setContentView(drawView);
    }
}
