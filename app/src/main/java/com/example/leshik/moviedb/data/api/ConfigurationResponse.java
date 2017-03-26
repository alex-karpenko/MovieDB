package com.example.leshik.moviedb.data.api;

/**
 * Created by Leshik on 19.12.2016.
 */

import com.example.leshik.moviedb.data.model.NetworkConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * template class to convert JSON answer with configuration from API to data class
 */


class Images {

    @SerializedName("base_url")
    @Expose
    public String baseUrl;
    @SerializedName("secure_base_url")
    @Expose
    public String secureBaseUrl;
    @SerializedName("backdrop_sizes")
    @Expose
    public List<String> backdropSizes = null;
    @SerializedName("logo_sizes")
    @Expose
    public List<String> logoSizes = null;
    @SerializedName("poster_sizes")
    @Expose
    public List<String> posterSizes = null;
    @SerializedName("profile_sizes")
    @Expose
    public List<String> profileSizes = null;
    @SerializedName("still_sizes")
    @Expose
    public List<String> stillSizes = null;

}

public class ConfigurationResponse {

    @SerializedName("images")
    @Expose
    public Images images;
    @SerializedName("change_keys")
    @Expose
    public List<String> changeKeys = null;

    public String getImagesBaseUrl() {
        return images.baseUrl;
    }

    public String getImagesBaseSecureUrl() {
        return images.secureBaseUrl;
    }

    public List<String> getImageSizes(NetworkConfig.ImageType type) {
        switch (type) {
            case Poster:
                return images.posterSizes;
            case Backdrop:
                return images.backdropSizes;
            case Logo:
                return images.logoSizes;
            case Profile:
                return images.profileSizes;
            case Still:
                return images.stillSizes;
        }

        return null;
    }
}
