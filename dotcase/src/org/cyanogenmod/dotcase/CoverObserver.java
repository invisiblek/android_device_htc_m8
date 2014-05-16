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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.os.UEventObserver;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;

class CoverObserver extends UEventObserver {
    private static final String TAG = "CoverObserver";
    private static final String COVER_UEVENT_MATCH = "DEVPATH=/devices/virtual/switch/cover";
    private static final String COVER_STATE_PATH = "/sys/class/switch/cover/state";

    private final Context mContext;
    private final WakeLock mWakeLock;
    private final IntentFilter filter = new IntentFilter();
    private PowerManager manager;

    public CoverObserver(Context context) {
        mContext = context;
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CoverObserver");
        mWakeLock.setReferenceCounted(false);
    }

    public synchronized final void init() {
        char[] buffer = new char[1024];
        try {
            BufferedReader closed = new BufferedReader(new FileReader(COVER_STATE_PATH));
            String value = closed.readLine();
            closed.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        filter.addAction(Intent.ACTION_SCREEN_ON);

        manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        startObserving(COVER_UEVENT_MATCH);
    }

    @Override
    public void onUEvent(UEventObserver.UEvent event) {
        try {
            int state = Integer.parseInt(event.get("SWITCH_STATE"));
            Log.e(TAG, "Cover " + ((state == 1) ? "closed" : "opened"));
            boolean screenOn = manager.isScreenOn();

            if (state == 1) {
                if (screenOn) {
                    manager.goToSleep(SystemClock.uptimeMillis());
                }
            } else {
                killActivity();
                if (!screenOn) {
                    manager.wakeUp(SystemClock.uptimeMillis());
                }
            }

            mWakeLock.acquire();
            mHandler.sendMessageDelayed(mHandler.obtainMessage(state), 0);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mContext.getApplicationContext().registerReceiver(receiver, filter);
            } else {
                try {
                    mContext.getApplicationContext().unregisterReceiver(receiver);
                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
                }
            }
            mWakeLock.release();
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent i = new Intent();

            if (intent.getAction() == "android.intent.action.SCREEN_ON") {
                i.setClassName("org.cyanogenmod.dotcase", "org.cyanogenmod.dotcase.DotcaseActivity");
            } else {
                Log.e(TAG, "Unhandled intent: " + intent.getAction());
                return;
            }

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }
    };

    private void killActivity() {
        try {
            Intent i = new Intent();
            i.setAction("org.cyanogenmod.dotcase.KILL_ACTIVITY");
            mContext.sendBroadcast(i);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }
}
