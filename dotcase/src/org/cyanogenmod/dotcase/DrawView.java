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
    private float dotratio = 40;
    private Paint paint = new Paint();

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
        int plugged = batteryIntent.getIntExtra("plugged", -1);
        double level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = rawlevel / scale;
        }

        paint.setARGB(255, 255, 255, 255);
        dotcaseDrawRect(1, 35, 25, 36, paint, canvas);   // top line
        dotcaseDrawRect(24, 35, 25, 39, paint, canvas);  // upper right line
        dotcaseDrawRect(24, 38, 26, 39, paint, canvas);  // nub top
        dotcaseDrawRect(25, 38, 26, 44, paint, canvas);  // nub right
        dotcaseDrawRect(24, 43, 26, 44, paint, canvas);  // nub bottom
        dotcaseDrawRect(24, 43, 25, 47, paint, canvas);  // lower right line
        dotcaseDrawRect(1, 46, 25, 47, paint, canvas);   // bottom line
        dotcaseDrawRect(1, 35, 2, 47, paint, canvas);    // left line

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
            dotcaseDrawRect(2 + i, 36, 3 + i, 46, paint, canvas);
        }

        if (plugged > 0) {
            paint.setARGB(255, 0, 0, 0);
            dotcaseDrawRect(13, 36, 15, 37, paint, canvas);
            dotcaseDrawRect(12, 37, 15, 38, paint, canvas);
            dotcaseDrawRect(11, 38, 15, 39, paint, canvas);
            dotcaseDrawRect(10, 39, 15, 40, paint, canvas);
            dotcaseDrawRect(9, 40, 17, 41, paint, canvas);
            dotcaseDrawRect(9, 41, 17, 42, paint, canvas);
            dotcaseDrawRect(11, 42, 16, 43, paint, canvas);
            dotcaseDrawRect(11, 43, 15, 44, paint, canvas);
            dotcaseDrawRect(11, 44, 14, 45, paint, canvas);
            dotcaseDrawRect(11, 45, 13, 46, paint, canvas);

            paint.setARGB(255, 255, 255, 0);
            dotcaseDrawRect(13, 37, 14, 38, paint, canvas);
            dotcaseDrawRect(12, 38, 14, 39, paint, canvas);
            dotcaseDrawRect(11, 39, 14, 40, paint, canvas);
            dotcaseDrawRect(10, 40, 14, 41, paint, canvas);
            dotcaseDrawRect(12, 41, 16, 42, paint, canvas);
            dotcaseDrawRect(12, 42, 15, 43, paint, canvas);
            dotcaseDrawRect(12, 43, 14, 44, paint, canvas);
            dotcaseDrawRect(12, 44, 13, 45, paint, canvas);
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

        dotcaseDrawPixel(13, 9, paint, canvas);
        dotcaseDrawPixel(13, 12, paint, canvas);

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

                    dotcaseDrawPixel(left, top, paint, canvas);
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

    private void dotcaseDrawPixel(int x, int y, Paint paint, Canvas canvas) {
        canvas.drawRect((float)(x * dotratio + 2),
                        (float)(y * dotratio + 2),
                        (float)((x + 1) * dotratio -2),
                        (float)((y + 1) * dotratio -2),
                        paint);
    }

    private void dotcaseDrawRect(int left, int top, int right, int bottom, Paint paint, Canvas canvas) {
        canvas.drawRect((float)(left * dotratio + 2),
                        (float)(top * dotratio + 2),
                        (float)(right * dotratio - 2),
                        (float)(bottom * dotratio - 2),
                        paint);
    }

}
