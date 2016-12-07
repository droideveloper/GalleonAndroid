/*
 * Copyright (C) 2014 Robert Theis
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

public class GrayQuant {
    static {
        System.loadLibrary("jpgt");
        System.loadLibrary("pngt");
        System.loadLibrary("lept");
    }
    
    private final static float GAMMA_DEFAULT = 1.0f;
    private final static int BLACK_DEFAULT = 170;
    private final static int WHITE_DEFAULT = 245;

  /**
   * Default GammaTRC
   *
   * @param pixs A source image.
   * @return A source image returned from operation.
   */
  public static Pix pixGammaRTC(Pix pixs) {
        return pixGammaRTC(pixs, GAMMA_DEFAULT, BLACK_DEFAULT, WHITE_DEFAULT);
    }

  /**
   * GammaTRC with given parameters.
   *
   * @param pixs A source image.
   * @param gamma desired gamma value
   * @param bColor blackColor to start
   * @param wColor whiteColor start
   * @return A source image returned from operation.
   */
    public static Pix pixGammaRTC(Pix pixs, float gamma, int bColor, int wColor) {
        if (pixs == null) {
            throw new IllegalArgumentException("Source pix must be non-null");
        }
        nativePixGammaRTC(pixs.getNativePix(), gamma, bColor, wColor);
        return pixs;
    }

    /**
     * Perform simple (pixelwise) binarization with fixed threshold
     * <p>
     * Notes:
     * <ol>
     * <li> If the source pixel is less than the threshold value, the dest will 
     * be 1; otherwise, it will be 0
     * </ol>
     *
     * @param pixs Source pix (4 or 8 bpp)
     * @param thresh Threshold value
     * @return a new Pix image, 1 bpp
     */
    public static Pix pixThresholdToBinary(Pix pixs, int thresh) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
        int depth = pixs.getDepth();
        if (depth != 4 && depth != 8)
            throw new IllegalArgumentException("Source pix depth must be 4 or 8 bpp");
        if (depth == 4 && thresh > 16)
            throw new IllegalArgumentException("4 bpp thresh not in {0-16}");
        if (depth == 8 && thresh > 256)
            throw new IllegalArgumentException("8 bpp thresh not in {0-256}");

        long nativePix = nativePixThresholdToBinary(pixs.getNativePix(), 
                thresh);

        if (nativePix == 0)
            throw new RuntimeException("Failed to perform binarization");

        return new Pix(nativePix);         
    }

  /**
   * Binary of image with default parameter.
   *
   * @param pixs A source image.
   * @return the source image to binary.
   */
    public static Pix pixThresholdToBinary(Pix pixs) {
        return pixThresholdToBinary(pixs, 35);
    }

    // ***************
    // * NATIVE CODE *
    // ***************

    private static native long nativePixThresholdToBinary(long nativePix, int thresh);
    private static native void nativePixGammaRTC(long nativePix, float gamma, int blackColor, int whiteColor);
}
