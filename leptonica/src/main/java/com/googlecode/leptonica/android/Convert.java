/*
 * Copyright (C) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.googlecode.leptonica.android;

/**
 * Image bit-depth conversion methods.
 * 
 * @author alanv@google.com (Alan Viverette)
 */
public class Convert {
    static {
        System.loadLibrary("jpgt");
        System.loadLibrary("pngt");
        System.loadLibrary("lept");
    }
    //add documentation
    private final static float COLOR_R_DEFAULT = 0.5f;
    private final static float COLOR_G_DEFAULT = 0.3f;
    private final static float COLOR_B_DEFAULT = 0.2f;
    
    /**
     * Converts an image of any bit depth to 8-bit grayscale.
     *
     * @param pixs Source pix of any bit-depth.
     * @return a new Pix image or <code>null</code> on error
     */
    public static Pix convertTo8(Pix pixs) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");

        long nativePix = nativeConvertTo8(pixs.getNativePix());

        if (nativePix == 0)
            throw new RuntimeException("Failed to natively convert pix");

        return new Pix(nativePix);
    }

  /**
   * Convert an image of rgb to gray with default parameters.
   *
   * @param pixs A source image to convert
   * @return the source image with gray
   */
  public static Pix convertRGBToGray(Pix pixs) {
        return convertRGBToGray(pixs, COLOR_R_DEFAULT, COLOR_G_DEFAULT, COLOR_B_DEFAULT);
    }

  /**
   * Converts an image of rgb to gray
   *
   * @param pixs A source image to convert
   * @param r red parameters in RGB
   * @param g green parameters in RGB
   * @param b blue parameters in RGB
   * @return the source image with gray
   */
    public static Pix convertRGBToGray(Pix pixs, float r, float g, float b) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
        
        long nativePix = nativeConvertRGBToGray(pixs.getNativePix(), r, g, b);
        
        if (nativePix == 0)
            throw new RuntimeException("Failed to natively convert pix");
        
        return new Pix(nativePix);
    }

    // ***************
    // * NATIVE CODE *
    // ***************

    private static native long nativeConvertTo8(long nativePix);
    private static native long nativeConvertRGBToGray(long nativePix, float r, float g, float b);
}
