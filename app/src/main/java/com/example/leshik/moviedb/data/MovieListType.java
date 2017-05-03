package com.example.leshik.moviedb.data;

/**
 * Created by alex on 3/5/17.
 */

public enum MovieListType {

    Popular(0) {
        @Override
        public boolean isFromNetwork() {
            return true;
        }

        @Override
        public boolean isLocalOnly() {
            return false;
        }

        @Override
        public String getModelColumnName() {
            return "popularPosition";
        }

    },
    Toprated(1) {
        @Override
        public boolean isFromNetwork() {
            return true;
        }

        @Override
        public boolean isLocalOnly() {
            return false;
        }

        @Override
        public String getModelColumnName() {
            return "topratedPosition";
        }
    },
    Upcoming(2) {
        @Override
        public boolean isFromNetwork() {
            return true;
        }

        @Override
        public boolean isLocalOnly() {
            return false;
        }

        @Override
        public String getModelColumnName() {
            return "upcomingPosition";
        }
    },
    Favorite(3) {
        @Override
        public boolean isFromNetwork() {
            return false;
        }

        @Override
        public boolean isLocalOnly() {
            return true;
        }

        @Override
        public String getModelColumnName() {
            return "favoriteTimestamp";
        }
    };

    private int index;

    MovieListType(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }

    abstract public boolean isFromNetwork();

    abstract public boolean isLocalOnly();

    abstract public String getModelColumnName();

    public String getTotalPagesKey() {
        return name() + "total_pages";
    }

    public String getTotalItemsKey() {
        return name() + "total_items";
    }

    public String getUpdateTimestampKey() {
        return name() + "last_update";
    }
}
