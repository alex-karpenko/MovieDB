package com.example.leshik.moviedb.data.model;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by alex on 3/11/17.
 */

public class NetworkConfig {
    private static final String TAG = "NetworkConfig";
    private static final String ORIGINAL_SIZE_STR = "original";
    public static final int OriginalSize = Integer.MAX_VALUE;
    public String basePosterUrl;
    public String basePosterSecureUrl;
    private Map<Integer, String>[] imageSizes = new Map[ImageType.getNumberOfTypes()];

    public NetworkConfig() {
    }

    public NetworkConfig(String basePosterUrl, String basePosterSecureUrl) {
        this.basePosterUrl = basePosterUrl;
        this.basePosterSecureUrl = basePosterSecureUrl;
    }

    public void setupBaseUrls(String basePosterUrl, String basePosterSecureUrl) {
        this.basePosterUrl = basePosterUrl;
        this.basePosterSecureUrl = basePosterSecureUrl;
    }

    public void setupImageSizes(ImageType type, List<String> imagesSizesStr) {
        if (imagesSizesStr == null)
            throw new IllegalArgumentException("Null pointer to list isn't acceptable.");

        imageSizes[type.getIndex()] = parseSizesStringList(imagesSizesStr);
        Log.i(TAG, "setupImageSizes: index=" + type + ", source_list=" + imagesSizesStr + ", result_map=" + imageSizes[type.getIndex()]);
    }

    private Map<Integer, String> parseSizesStringList(List<String> sizesList) {
        Map<Integer, String> newMap = new TreeMap<>();

        for (String s : sizesList) {
            if (ORIGINAL_SIZE_STR.equals(s)) {
                newMap.put(OriginalSize, s);
            } else if (s.startsWith("w")) {
                try {
                    int width = Integer.valueOf(s.substring(1));
                    newMap.put(width, s);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Bad value '" + s + "' in the list.");
                }
            } else if (s.startsWith("h"))
                continue; // TODO: 3/26/17 Now is ignoring h---- values, but we have to deal with it...
            else throw new IllegalArgumentException("Bad value '" + s + "' in the list.");
        }

        return newMap;
    }

    public void setImageSizesArray(Map<Integer, String>[] newMap) {
        Log.i(TAG, "setImageSizesArray: array=" + Arrays.toString(newMap));
        imageSizes = newMap;
    }

    public Map<Integer, String>[] getImageSizesArray() {
        return imageSizes;
    }

    public String getOptimalWidthString(ImageType type, int viewWidth) {
        TreeSet<Integer> widthsSet = new TreeSet<>();
        Map<Integer, String> configSizes = imageSizes[type.getIndex()];

        widthsSet.addAll(configSizes.keySet());

        for (Integer w : widthsSet) {
            if (viewWidth <= w) return configSizes.get(w);
        }

        return configSizes.get(OriginalSize);
    }

    public boolean isValid() {
        return basePosterSecureUrl != null & basePosterUrl != null;
    }

    public enum ImageType {
        Poster(0),
        Backdrop(1),
        Logo(2),
        Profile(3),
        Still(4);

        private int index;

        ImageType(int i) {
            index = i;
        }

        public int getIndex() {
            return index;
        }

        static int getNumberOfTypes() {
            return values().length;
        }
    }
}
