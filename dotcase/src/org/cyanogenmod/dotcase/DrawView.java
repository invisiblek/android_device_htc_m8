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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class DrawView extends View {
    // 1920x1080 = 48 x 27 dots @ 40 pixels per dot
    private final Context mContext;
    private final IntentFilter filter = new IntentFilter();
    private int heartbeat = 0;

    public static int ringCounter;

    public DrawView(Context context) {
        super(context);
        Paint paint = new Paint();
        mContext = context;
        paint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (Dotcase.alarm_clock) {
            drawAlarm(canvas);
        } else if (!Dotcase.ringing) {
            drawTime(canvas);
            Dotcase.checkNotifications();
            if (Dotcase.gmail || Dotcase.hangouts || Dotcase.mms || Dotcase.missed_call
                              || Dotcase.twitter  || Dotcase.voicemail) {
                if (heartbeat < 3) {
                    drawNotifications(canvas);
                } else {
                    drawBattery(canvas);
                }
                heartbeat++;
                if (heartbeat > 5) {
                    heartbeat = 0;
                }
            } else {
                drawBattery(canvas);
                heartbeat = 0;
            }
        } else {
            drawNumber(canvas);
            drawRinger(canvas);
        }

        filter.addAction(DotcaseConstants.ACTION_REDRAW);
        mContext.getApplicationContext().registerReceiver(receiver, filter);
    }

    private timeObject getTimeString() {
        timeObject timeObj = new timeObject();
        timeObj.hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        timeObj.min = (Calendar.getInstance().get(Calendar.MINUTE));

        if (timeObj.hour > 11) {
            timeObj.hour = timeObj.hour - 12;
            timeObj.am = false;
        } else {
            timeObj.am = true;
        }

        if (timeObj.hour == 0) {
            timeObj.hour = timeObj.hour + 12;
        }

        timeObj.timeString = (timeObj.hour < 10 ? " " + Integer.toString(timeObj.hour) : Integer.toString(timeObj.hour))
                           + ((timeObj.min < 10) ? "0" + Integer.toString(timeObj.min) : Integer.toString(timeObj.min));
        return timeObj;
    }

    private void drawAlarm(Canvas canvas) {
        int light = 7;
        int dark = 12;
        int clockLength = DotcaseConstants.clockSprite.length;
        int clockElementLength = DotcaseConstants.clockSprite[0].length;
        int ringerLength = DotcaseConstants.ringerSprite.length;
        int ringerElementLength = DotcaseConstants.ringerSprite[0].length;
        timeObject time = getTimeString();

        int[][] mClockSprite =
                new int[clockLength][clockElementLength];
        int[][] mRingerSprite =
                new int[ringerLength][ringerElementLength];
        int[][] mWordArray;

        for (int i = 0; i < ringerLength; i++) {
            for (int j = 0; j < ringerElementLength; j++) {
                if (DotcaseConstants.ringerSprite[i][j] > 0) {
                    mRingerSprite[i][j] =
                            DotcaseConstants.ringerSprite[i][j] == 3 - (ringCounter % 3) ? light : dark;
                }
            }
        }

        for (int i = 0; i < clockLength; i++) {
            for (int j = 0; j < clockElementLength; j++) {
                mClockSprite[i][j] = DotcaseConstants.clockSprite[i][j] > 0 ? light : 0;
            }
        }

        if (ringCounter / 6 > 0) {
            mWordArray = DotcaseConstants.alarmCancelArray;
            Collections.reverse(Arrays.asList(mRingerSprite));
        } else {
            mWordArray = DotcaseConstants.snoozeArray;
        }

        if (time.am) {
            dotcaseDrawSprite(DotcaseConstants.amSprite, 18, 0, canvas);
        } else {
            dotcaseDrawSprite(DotcaseConstants.pmSprite, 18, 0, canvas);
        }

        if (time.hour < 10) {
            time.timeString = " " + time.timeString;
        }

        for (int i = 0; i < time.timeString.length(); i++) {
            if (i < 2) {
                dotcaseDrawSprite(DotcaseConstants.getSmallSprite(time.timeString.charAt(i)), i * 4, 0, canvas);
            } else {
                dotcaseDrawSprite(DotcaseConstants.getSmallSprite(time.timeString.charAt(i)), i * 4 + 3, 0, canvas);
            }
        }

        dotcaseDrawSprite(DotcaseConstants.smallTimeColon, 8, 1, canvas);
        dotcaseDrawSprite(mClockSprite, 7, 7, canvas);
        dotcaseDrawSprite(mWordArray, 2, 21, canvas);
        dotcaseDrawSprite(mRingerSprite, 7, 28, canvas);

        ringCounter++;
        if (ringCounter > 11) {
            ringCounter = 0;
        }
    }

    private void drawNotifications(Canvas canvas) {
        int count = 0;
        int x = 1;
        int y = 30;
        if (Dotcase.missed_call) {
            dotcaseDrawSprite(DotcaseConstants.missedCallSprite, x + ((count % 3) * 9), y + ((count / 3) * 9),
                    canvas);
            count++;
        }

        if (Dotcase.voicemail) {
            dotcaseDrawSprite(DotcaseConstants.voicemailSprite, x + ((count % 3) * 9), y + ((count / 3) * 9),
                    canvas);
            count++;
        }

        if (Dotcase.gmail) {
            dotcaseDrawSprite(DotcaseConstants.gmailSprite, x + ((count % 3) * 9), y + ((count / 3) * 9),
                    canvas);
            count++;
        }

        if (Dotcase.hangouts) {
            dotcaseDrawSprite(DotcaseConstants.hangoutsSprite, x + ((count % 3) * 9), y + ((count / 3) * 9),
                    canvas);
            count++;
        }

        if (Dotcase.mms) {
            dotcaseDrawSprite(DotcaseConstants.mmsSprite, x + ((count % 3) * 9), y + ((count / 3) * 9),
                    canvas);
            count++;
        }

        if (Dotcase.twitter) {
            dotcaseDrawSprite(DotcaseConstants.twitterSprite, x + ((count % 3) * 9), y + ((count / 3) * 9),
                    canvas);
        }
    }

    private void drawRinger(Canvas canvas) {
        int light;
        int dark;
        int handsetLength = DotcaseConstants.handsetSprite.length;
        int handsetElementLength = DotcaseConstants.handsetSprite[0].length;
        int ringerLength = DotcaseConstants.ringerSprite.length;
        int ringerElementLength = DotcaseConstants.ringerSprite[0].length;

        int[][] mHandsetSprite =
                new int[handsetLength][handsetElementLength];
        int[][] mRingerSprite =
                new int[ringerLength][ringerElementLength];

        if (ringCounter /3 > 0) {
            light = 2;
            dark = 11;
        } else {
            light = 3;
            dark = 10;
        }

        for (int i = 0; i < ringerLength; i++) {
            for (int j = 0; j < ringerElementLength; j++) {
                if (DotcaseConstants.ringerSprite[i][j] > 0) {
                    mRingerSprite[i][j] =
                            DotcaseConstants.ringerSprite[i][j] == 3 - (ringCounter % 3) ? light : dark;
                }
            }
        }

        for (int i = 0; i < handsetLength; i++) {
            for (int j = 0; j < handsetElementLength; j++) {
                mHandsetSprite[i][j] = DotcaseConstants.handsetSprite[i][j] > 0 ? light : 0;
            }
        }

        if (ringCounter / 3 > 0) {
            Collections.reverse(Arrays.asList(mRingerSprite));
            Collections.reverse(Arrays.asList(mHandsetSprite));
        }

        dotcaseDrawSprite(mHandsetSprite, 6, 21, canvas);
        dotcaseDrawSprite(mRingerSprite, 7, 28, canvas);

        ringCounter++;
        if (ringCounter > 5) {
            ringCounter = 0;
        }
    }

    private void drawBattery(Canvas canvas) {
        Intent batteryIntent = mContext.getApplicationContext().registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int rawlevel = batteryIntent.getIntExtra("level", -1);
        double scale = batteryIntent.getIntExtra("scale", -1);
        int plugged = batteryIntent.getIntExtra("plugged", -1);
        double level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = rawlevel / scale;
        }

        dotcaseDrawRect(1, 35, 25, 36, 1, canvas);   // top line
        dotcaseDrawRect(24, 35, 25, 39, 1, canvas);  // upper right line
        dotcaseDrawRect(25, 38, 26, 44, 1, canvas);  // nub right
        dotcaseDrawRect(24, 43, 25, 47, 1, canvas);  // lower right line
        dotcaseDrawRect(1, 46, 25, 47, 1, canvas);   // bottom line
        dotcaseDrawRect(1, 35, 2, 47, 1, canvas);    // left line

        // 4.34 percents per dot
        int fillDots = (int)Math.round((level * 100) / 4.34);
        int color;

        if (level >= .50) {
            color = 3;
        } else if (level >= .25) {
            color = 5;
        } else {
            color = 2;
        }

        for (int i = 0; i < fillDots; i++) {
            if (i == 22) {
                dotcaseDrawRect(2 + i, 39, 3 + i, 43, color, canvas);
            } else {
                dotcaseDrawRect(2 + i, 36, 3 + i, 46, color, canvas);
            }
        }

        if (plugged > 0) {
            dotcaseDrawSprite(DotcaseConstants.lightningSprite, 9, 36, canvas);
        }
    }

    private void drawTime(Canvas canvas) {
        timeObject time = getTimeString();

        int x, y = 5;
        int starter = 0;

        if (time.hour > 9) {
            starter = 3;
        }

        dotcaseDrawSprite(DotcaseConstants.timeColon, starter + 10, y + 4, canvas);

        if (time.am) {
            dotcaseDrawSprite(DotcaseConstants.amSprite, 3, 18, canvas);
        } else {
            dotcaseDrawSprite(DotcaseConstants.pmSprite, 3, 18, canvas);
        }

        for (int i = 0; i < time.timeString.length(); i++) {
            if (i == 0) {
                x = starter;
            } else if (i == 1) {
                x = starter + 5;
            } else if (i == 2) {
                x = starter + 12;
            } else {
                x = starter + 17;
            }

            dotcaseDrawSprite(DotcaseConstants.getSprite(time.timeString.charAt(i)), x, y, canvas);

        }
    }

    private void dotcaseDrawPixel(int x, int y, Paint paint, Canvas canvas) {
        float dotratio = 40;
        canvas.drawRect((x * dotratio + 5),
                        (y * dotratio + 5),
                        ((x + 1) * dotratio -5),
                        ((y + 1) * dotratio -5),
                        paint);
    }

    private void dotcaseDrawRect(int left, int top, int right, int bottom, int color, Canvas canvas) {
        for (int x=left; x < right; x++) {
            for (int y=top; y < bottom; y++) {
                dotcaseDrawPixel(x, y, DotcaseConstants.getPaintFromNumber(color), canvas);
            }
        }
    }

    private void dotcaseDrawSprite(int[][] sprite, int x, int y, Canvas canvas) {
        for (int i=0; i < sprite.length; i++) {
            for (int j=0; j < sprite[0].length; j++) {
                dotcaseDrawPixel(x + j, y + i, DotcaseConstants.getPaintFromNumber(sprite[i][j]), canvas);
            }
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DotcaseConstants.ACTION_REDRAW)) {
                postInvalidate();
            }
        }
    };

    private void drawNumber(Canvas canvas) {
        int[][] sprite;
        int x = 0, y = 5;
        if (Dotcase.ringing) {
            for (int i = 3; i < Dotcase.phoneNumber.length(); i++) {
                sprite = DotcaseConstants.getSmallSprite(Dotcase.phoneNumber.charAt(i));
                dotcaseDrawSprite(sprite, x + (i - 3) * 4, y, canvas);
            }
        }
    }

    private class timeObject {
        String timeString;
        int hour;
        int min;
        boolean am;
    }
}
