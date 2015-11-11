package org.lirazs.robolayout.core.util;

import org.apache.commons.io.FilenameUtils;
import org.robovm.apple.coregraphics.*;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.*;
import org.robovm.objc.ObjCObject;
import org.robovm.objc.ObjCRuntime;
import org.robovm.objc.Selector;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by mac on 7/29/15.
 */
public class UIImageUtil extends NSObject {

    private static String[] ninePatchSuffixes = new String[] { ".9.png", ".9@2x.png", "@2x.9.png" };
    private static String NINE_PATCH_PADDINGS_KEY = "NINE_PATCH_PADDINGS_KEY";


    public static UIImage createImageFromColor(UIColor color, CGSize size) {
        UIImage image = null;
        NSAutoreleasePool pool = new NSAutoreleasePool();

        CGRect rect = CGRect.Zero();
        rect.setSize(size);

        UIGraphics.beginImageContext(rect.getSize(), false, 0.f);
        CGContext context = UIGraphics.getCurrentContext();

        context.setFillColor(color.getCGColor());
        context.fillRect(rect);

        image = UIGraphics.getImageFromCurrentImageContext();
        UIGraphics.endImageContext();

        //TODO: API got deprecated - FIX!!
        //if(image.respondsToSelector(Selector.register("resizableImageWithCapInsets:resizingMode:"))) {
            //image = image.createResizable(UIEdgeInsets.Zero(), UIImageResizingMode.Stretch);

        /*} else if(image.respondsToSelector(Selector.register("resizableImageWithCapInsets:"))) {
            image = image.createResizable(UIEdgeInsets.Zero());

        } else if(image.respondsToSelector(Selector.register("stretchableImageWithLeftCapWidth:topCapHeight:"))) {
            image = image.createStretchable(0, 0);
        }*/

        pool.drain();

        return image;
    }


    public static UIImage createNinePatchImage(UIImage image) {
        UIEdgeInsets capInsets = UIEdgeInsets.Zero();
        UIEdgeInsets padding = UIEdgeInsets.Zero();

        boolean hasPadding = applyNinePatchCapInsets(image, capInsets, padding);
        UIImage ninePatchImage = createResizableImageFromNinePatchImage(image, capInsets);

        if(hasPadding) {
            ninePatchImage.setAssociatedObject(NINE_PATCH_PADDINGS_KEY, NSValue.valueOf(padding));
        }

        return ninePatchImage;
    }

    public static boolean hasNinePatchPaddings(UIImage uiImage) {
        return getNinePatchPaddings(uiImage) != null;
    }

    public static UIEdgeInsets getNinePatchPaddings(UIImage uiImage) {
        NSValue nsValue = (NSValue) uiImage.getAssociatedObject(NINE_PATCH_PADDINGS_KEY);
        return nsValue != null && nsValue.edgeInsetsValue() != null ? nsValue.edgeInsetsValue() : null;
    }

    public static boolean isNinePatchImageFile(String fileName) {
        for (String suffix : ninePatchSuffixes) {
            if(fileName.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean applyNinePatchCapInsets(UIImage image, UIEdgeInsets capInsets, UIEdgeInsets padding) {
        boolean hasPadding = false;

        // CapInsets rects
        CGRect leftRect = new CGRect(0.f, 0.f, 1.f, image.getSize().getHeight() * image.getScale());
        CGRect topRect = new CGRect(0.f, 0.f, image.getSize().getWidth() * image.getScale(), 1.f);

        double verticalBufferSize = Math.ceil(leftRect.getSize().getWidth()) * Math.ceil(leftRect.getSize().getHeight());
        double horizontalBufferSize = Math.ceil(topRect.getSize().getWidth()) * Math.ceil(topRect.getSize().getHeight());

        // Padding rects
        CGRect rightRect = new CGRect(image.getSize().getWidth() * image.getScale() - 1.f,
                0.f, 1.f, image.getSize().getHeight() * image.getScale());
        CGRect bottomRect = new CGRect(0, image.getSize().getHeight() * image.getScale() - 1.f,
                image.getSize().getWidth() * image.getScale(), 1.f);

        // We allocate one single buffer containing all the data:
        // | leftCapInsetLine | topCapInsetLine | rightPaddingLine | bottomPaddingLine |
        double totalBufferSize = 0;

        if(capInsets != null) {
            totalBufferSize += verticalBufferSize + horizontalBufferSize;
        }
        if(padding != null) {
            totalBufferSize += verticalBufferSize + horizontalBufferSize;
        }

        if(totalBufferSize > 0) {
            byte[] buffer = new byte[(int) totalBufferSize];
            byte[] leftBuffer = null;
            byte[] topBuffer = null;
            byte[] rightBuffer;
            byte[] bottomBuffer;

            if(capInsets != null) {
                leftBuffer = buffer;
                topBuffer = ByteBuffer.wrap(leftBuffer).putDouble(verticalBufferSize).array();
                rightBuffer = ByteBuffer.wrap(topBuffer).putDouble(horizontalBufferSize).array();
                bottomBuffer = ByteBuffer.wrap(rightBuffer).putDouble(verticalBufferSize).array();
            } else {
                rightBuffer = buffer;
                bottomBuffer = ByteBuffer.wrap(rightBuffer).putDouble(verticalBufferSize).array();;
            }

            // First write the pixel lines to the buffer...
            if(capInsets != null) {
                writePixelMaskForRect(image, leftRect, leftBuffer);
                writePixelMaskForRect(image, topRect, topBuffer);
            }
            if(padding != null) {
                writePixelMaskForRect(image, rightRect, rightBuffer);
                writePixelMaskForRect(image, bottomRect, bottomBuffer);
            }

            // ...then find start and end pixel of the nine patch indicator lines
            if(capInsets != null) {
                int[] topBottomInts = new int[] {(int) capInsets.getTop(), (int) capInsets.getBottom()};
                startAndEndFromBuffer(leftBuffer, verticalBufferSize, topBottomInts);

                capInsets.setTop(topBottomInts[0]);
                capInsets.setBottom(topBottomInts[1]);

                int[] leftRightInts = new int[] {(int) capInsets.getLeft(), (int) capInsets.getRight()};
                startAndEndFromBuffer(topBuffer, horizontalBufferSize, leftRightInts);

                capInsets.setLeft(leftRightInts[0]);
                capInsets.setRight(leftRightInts[1]);

                capInsets.setTop(capInsets.getTop() / image.getScale());
                capInsets.setLeft(capInsets.getLeft() / image.getScale());
                capInsets.setBottom(capInsets.getBottom() / image.getScale());
                capInsets.setRight(capInsets.getRight() / image.getScale());
            }

            if(padding != null) {
                int[] topBottomInts = new int[] {(int) padding.getTop(), (int) padding.getBottom()};
                boolean paddingIndicator = startAndEndFromBuffer(rightBuffer, verticalBufferSize, topBottomInts);

                padding.setTop(topBottomInts[0]);
                padding.setBottom(topBottomInts[1]);

                int[] leftRightInts = new int[] {(int) padding.getLeft(), (int) padding.getRight()};
                paddingIndicator |= startAndEndFromBuffer(bottomBuffer, horizontalBufferSize, leftRightInts);

                padding.setLeft(leftRightInts[0]);
                padding.setRight(leftRightInts[1]);

                padding.setTop(padding.getTop() / image.getScale());
                padding.setLeft(padding.getLeft() / image.getScale());
                padding.setBottom(padding.getBottom() / image.getScale());
                padding.setRight(padding.getRight() / image.getScale());

                hasPadding = paddingIndicator;
            }
        }

        return hasPadding;
    }


    public static UIImage createResizableImageFromNinePatchImage(UIImage image, UIEdgeInsets insets) {
        CGRect imageRect = new CGRect(1, 1, image.getSize().getWidth() * image.getScale() - 2, image.getSize().getHeight() * image.getScale() - 2);
        CGImage cgImage = CGImage.createWithImageInRect(image.getCGImage(), imageRect);

        UIImage nonScaledImage = new UIImage(cgImage, image.getScale(), image.getOrientation());
        cgImage.release();

        UIImage resizableImage = null;
        //TODO: API got deprecated - FIX!!
        //if(nonScaledImage.respondsToSelector(Selector.register("resizableImageWithCapInsets:resizingMode:"))) {
            //resizableImage = nonScaledImage.createResizable(insets, UIImageResizingMode.Stretch);
        /*} else if(nonScaledImage.respondsToSelector(Selector.register("resizableImageWithCapInsets:"))) {
            resizableImage = nonScaledImage.createResizable(insets);
        } else if(nonScaledImage.respondsToSelector(Selector.register("stretchableImageWithLeftCapWidth:topCapHeight:"))) {
            resizableImage = nonScaledImage.createStretchable((long)insets.getLeft(), (long)insets.getTop());
        }*/
        return resizableImage;
    }

    public static UIImage createImage(String name, NSBundle bundle) {
        UIImage result = null;
        String extension = FilenameUtils.getExtension(name);

        if(extension.isEmpty()) {
            extension = "png";
        }
        String fileName = FilenameUtils.getBaseName(name);

        UIScreen mainScreen = UIScreen.getMainScreen();
        if(/*mainScreen.respondsToSelector(Selector.register("scale")) && */mainScreen.getScale() >= 2.f) {
            result = createNinePatchRetinaImage(fileName, extension, bundle);
        }

        if(result == null) {
            NSURL imageURL = bundle.findResourceURL(fileName, extension);
            if(imageURL != null) {
                UIImage image = new UIImage(new File(imageURL.getPath()));
                if(isNinePatchImageFile(imageURL.getLastPathComponent())) {
                    result = createNinePatchImage(image);
                } else {
                    result = image;
                }
            } else {
                result = createNinePatchRetinaImage(fileName, extension, bundle);
            }
        }
        return result;
    }

    public static UIImage createNinePatchRetinaImage(String fileName, String extension, NSBundle bundle) {
        UIImage result = null;

        String retinaFileName = fileName + "@2x";
        NSURL retinaImageURL = bundle.findResourceURL(retinaFileName, extension);

        if(retinaImageURL != null) {
            File file = new File(retinaImageURL.getPath());

            UIImage nonScaledImage = new UIImage(file);
            UIImage retinaImage = null;

            if(nonScaledImage.getScale() >= 2.f) {
                retinaImage = nonScaledImage;
            } else {
                //TODO: API got deprecated - FIX!!
                //retinaImage = UIImage.create(nonScaledImage.getCGImage(), 2.f, nonScaledImage.getOrientation());
            }
            if(retinaImage != null) {
                if(isNinePatchImageFile(retinaImageURL.getLastPathComponent())) {
                    result = createNinePatchImage(retinaImage);
                } else {
                    result = retinaImage;
                }
            }
        }

        return result;
    }

    public static void writePixelMaskForRect(UIImage image, CGRect rect, byte[] buffer) {
        CGImage croppedImage = CGImage.createWithImageInRect(image.getCGImage(), rect);

        CGContext context = CGBitmapContext.create(buffer, (long)rect.getSize().getWidth(),
                (long)rect.getSize().getHeight(), 8l, (long)rect.getSize().getWidth(), null,
                new CGBitmapInfo(CGImageAlphaInfo.Only.value()));

        context.drawImage(new CGRect(0, 0, croppedImage.getWidth(), croppedImage.getHeight()), croppedImage);

        croppedImage.release();
        context.release();
    }


    public static boolean startAndEndFromBuffer(byte[] buffer, double length, int[] startEndInts) {
        byte threshold = 0x7f;
        boolean searchForEnd = false;

        for (int i = 0; i < length; i++) {
            byte color = buffer[i];
            if(color > threshold) {
                startEndInts[0] = i;
                searchForEnd = true;
                break;
            }
        }

        if(searchForEnd) {
            for (int i = (int)length - 1; i >= 0; i--) {
                byte color = buffer[i];
                if(color > threshold) {
                    startEndInts[1] = (int) (length - i - 1);
                    break;
                }
            }
        }

        return searchForEnd;
    }
}
