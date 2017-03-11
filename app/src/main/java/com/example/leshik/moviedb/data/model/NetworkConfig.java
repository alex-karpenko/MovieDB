package com.example.leshik.moviedb.data.model;

/**
 * Created by alex on 3/11/17.
 */

public class NetworkConfig {
    public String basePosterUrl;
    public String basePosterSecureUrl;

    public NetworkConfig() {
        basePosterUrl = null;
        basePosterSecureUrl = null;
    }

    public NetworkConfig(String basePosterUrl, String basePosterSecureUrl) {
        this.basePosterUrl = basePosterUrl;
        this.basePosterSecureUrl = basePosterSecureUrl;
    }
}
