package com.example.leshik.moviedb.data;

/**
 * Created by alex on 3/5/17.
 */

public enum MovieListType {

    Popular(0) {
        @Override
        boolean isFromNetwork() {
            return true;
        }

        @Override
        boolean isLocalOnly() {
            return false;
        }

        @Override
        String getModelColumnName() {
            return "popularPosition";
        }

    },
    Toprated(1) {
        @Override
        boolean isFromNetwork() {
            return true;
        }

        @Override
        boolean isLocalOnly() {
            return false;
        }

        @Override
        String getModelColumnName() {
            return "topratedPosition";
        }
    },
    Upcoming(2) {
        @Override
        boolean isFromNetwork() {
            return true;
        }

        @Override
        boolean isLocalOnly() {
            return false;
        }

        @Override
        String getModelColumnName() {
            return "upcomingPosition";
        }
    },
    Favorite(3) {
        @Override
        boolean isFromNetwork() {
            return false;
        }

        @Override
        boolean isLocalOnly() {
            return true;
        }

        @Override
        String getModelColumnName() {
            return "favoritePosition";
        }
    };

    private int index;

    MovieListType(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }

    abstract boolean isFromNetwork();

    abstract boolean isLocalOnly();

    abstract String getModelColumnName();

    String getTotalPagesKey() {
        return name() + "total_pages";
    }

    String getTotalItemsKey() {
        return name() + "total_items";
    }

    String getUpdateTimestampKey() {
        return name() + "last_update";
    }
}
