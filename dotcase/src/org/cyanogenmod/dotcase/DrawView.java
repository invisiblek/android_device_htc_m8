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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

public class DrawView extends View {
    // 1080 wide (27 dots)
    // 1920 high (48 dots)
    // 40pixels per dot
    private static final String TAG = "Dotcase";
    private final Context mContext;
    Paint paint = new Paint();

    public DrawView(Context context) {
        super(context);
        mContext = context;
        paint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawTime(canvas);
        drawBattery(canvas);
    }

    private void drawBattery(Canvas canvas) {
        Intent batteryIntent = mContext.getApplicationContext().registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int rawlevel = batteryIntent.getIntExtra("level", -1);
        double scale = batteryIntent.getIntExtra("scale", -1);
        double level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = rawlevel / scale;
        }

        paint.setARGB(255, 255, 255, 255);
        dotcaseDrawRect(1, 36, 25, 37, paint, canvas);   // top line
        dotcaseDrawRect(24, 36, 25, 40, paint, canvas);  // upper right line
        dotcaseDrawRect(24, 39, 26, 40, paint, canvas);  // nub top
        dotcaseDrawRect(25, 39, 26, 44, paint, canvas);  // nub right
        dotcaseDrawRect(24, 43, 26, 44, paint, canvas);  // nub bottom
        dotcaseDrawRect(24, 43, 25, 47, paint, canvas);  // lower right line
        dotcaseDrawRect(1, 46, 25, 47, paint, canvas);   // bottom line
        dotcaseDrawRect(1, 36, 2, 47, paint, canvas);    // right line

        // 4.34 percents per dot
        int fillDots = (int)Math.round((level*100)/4.34);

        if (level >= .75) {
            paint.setARGB(255, 0, 255, 0);
        } else if (level >= .30) {
            paint.setARGB(255, 255, 165, 0);
        } else {
            paint.setARGB(255, 255, 0, 0);
        }

        for (int i = 0; i < fillDots; i++) {
            dotcaseDrawRect(2 + i, 37, 3 + i, 46, paint, canvas);
        }
    }

    private void drawTime(Canvas canvas) {
        paint.setARGB(255, 51, 181, 229);
        String time = ((Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10) ?
                       "0" + Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) :
                       Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)))
                      +
                      ((Calendar.getInstance().get(Calendar.MINUTE) < 10) ?
                       "0" + Integer.toString(Calendar.getInstance().get(Calendar.MINUTE)) :
                       Integer.toString(Calendar.getInstance().get(Calendar.MINUTE)));
        int[] sprite;
        int col, row;
        int left, top, right, bottom;

        dotcaseDrawRect(13, 9, 14, 10, paint, canvas);
        dotcaseDrawRect(13, 12, 14, 13, paint, canvas);

        for (int i = 0; i < time.length(); i++) {
            sprite = getSprite(time.charAt(i));
            col = 0;
            row = 0;

            for (int j = 0; j < 44; j++) {
                if (col > 3) {
                    col = 0;
                    row++;
                }

                if (sprite[j] == 1) {
                    left = ((col + i * 5) + 4);
                    top = (row + 6);
                    right = ((col + i * 5) + 5);
                    bottom = (row + 7);

                if (i < 2) {
                    left = left - 1;
                    right = right - 1;
                } else {
                    left = left + 1;
                    right = right + 1;
                }

                    dotcaseDrawRect(left, top, right, bottom, paint, canvas);
                }

                col++;
            }
        }
    }

    public int[] getSprite(char c) {
        int[] sprite;
        switch (c) {
            case '0': sprite = new int[]
                               {0, 1, 1, 0,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                0, 1, 1, 0 };
                      break;
            case '1': sprite = new int[]
                               {0, 0, 1, 0,
                                0, 1, 1, 0,
                                1, 1, 1, 0,
                                0, 0, 1, 0,
                                0, 0, 1, 0,
                                0, 0, 1, 0,
                                0, 0, 1, 0,
                                0, 0, 1, 0,
                                0, 0, 1, 0,
                                0, 0, 1, 0,
                                1, 1, 1, 1 };
                      break;
            case '2': sprite = new int[]
                               {0, 1, 1, 0,
                                1, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 1, 0,
                                0, 0, 1, 0,
                                0, 1, 0, 0,
                                0, 1, 0, 0,
                                1, 0, 0, 0,
                                1, 0, 0, 0,
                                1, 1, 1, 1 };
                      break;
            case '3': sprite = new int[]
                               {0, 1, 1, 0,
                                1, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 1, 1, 0,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                1, 0, 0, 1,
                                0, 1, 1, 0 };
                      break;
            case '4': sprite = new int[]
                               {1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 1, 1, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1 };
                      break;
            case '5': sprite = new int[]
                               {1, 1, 1, 1,
                                1, 0, 0, 0,
                                1, 0, 0, 0,
                                1, 0, 0, 0,
                                1, 0, 0, 0,
                                1, 1, 1, 0,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                1, 0, 0, 1,
                                0, 1, 1, 0 };
                      break;
            case '6': sprite = new int[]
                               {0, 1, 1, 0,
                                1, 0, 0, 1,
                                1, 0, 0, 0,
                                1, 0, 0, 0,
                                1, 0, 0, 0,
                                1, 1, 1, 0,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                0, 1, 1, 0 };
                      break;
            case '7': sprite = new int[]
                               {1, 1, 1, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1 };
                      break;
            case '8': sprite = new int[]
                               {0, 1, 1, 0,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                0, 1, 1, 0,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                0, 1, 1, 0 };
                      break;
            case '9': sprite = new int[]
                               {0, 1, 1, 0,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                0, 1, 1, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                1, 0, 0, 1,
                                0, 1, 1, 0 };
                      break;
            default: sprite = new int[]
                               {0, 0, 0, 0,
                                0, 0, 0, 0,
                                0, 0, 0, 0,
                                0, 0, 0, 0,
                                0, 0, 0, 0,
                                0, 0, 0, 0,
                                0, 0, 0, 0,
                                0, 0, 0, 0,
                                0, 0, 0, 0,
                                0, 0, 0, 0,
                                0, 0, 0, 0};
                     break;
        }

        return sprite;
    }

    private void dotcaseDrawRect(int left, int top, int right, int bottom, Paint paint, Canvas canvas) {
        float dotratio = 40;
        canvas.drawRect((float)(left * dotratio + 2),
                        (float)(top * dotratio + 2),
                        (float)(right * dotratio - 2),
                        (float)(bottom * dotratio - 2),
                        paint);
    }

}
