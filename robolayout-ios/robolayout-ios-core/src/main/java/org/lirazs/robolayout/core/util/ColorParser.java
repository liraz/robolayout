package org.lirazs.robolayout.core.util;

import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIColor;

import java.util.Scanner;

/**
 * Created by mac on 7/29/15.
 */
public final class ColorParser {

    public static UIColor getColorFromColorString(String string) {
        UIColor result = null;

        if(string == null)
            return null;

        if(string.indexOf('#') == 0) { // making sure this is a HEX string
            boolean valid = false;
            int length = string.length() - 1;

            long color = Long.parseLong(string.substring(1), 16);

            double alpha = 1.f;
            double red   = 0.f;
            double green = 0.f;
            double blue  = 0.f;

            switch (length) {
                case 5:
                case 4:
                    alpha = ((color & 0xF000) >> 12) / 15.0;
                case 3:
                    red   = ((color & 0xF00) >> 8) / 15.0f;
                    green = ((color & 0x0F0) >> 4) / 15.0f;
                    blue  =  (color & 0x00F) / 15.0f;
                    valid = true;
                    break;
                case 8:
                    alpha = ((color & 0xFF000000) >> 24) / 255.0f;
                case 7:
                case 6:
                    red   = ((color & 0xFF0000) >> 16) / 255.0f;
                    green = ((color & 0x00FF00) >> 8) / 255.0f;
                    blue  =  (color & 0x0000FF) / 255.0f;
                    valid = true;
                    break;
                default:
                    break;
            }

            if(valid) {
                result = new UIColor(red, green, blue, alpha);
            }
        }

        return result;
    }
}
