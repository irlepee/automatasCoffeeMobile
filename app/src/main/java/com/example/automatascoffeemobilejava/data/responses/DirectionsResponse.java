package com.example.automatascoffeemobilejava.data.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionsResponse {
    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public static class Route {
        @SerializedName("overview_polyline")
        private OverviewPolyline overviewPolyline;

        @SerializedName("legs")
        private List<Leg> legs;

        public OverviewPolyline getOverviewPolyline() {
            return overviewPolyline;
        }

        public List<Leg> getLegs() {
            return legs;
        }
    }

    public static class Leg {
        @SerializedName("distance")
        private Distance distance;

        @SerializedName("duration")
        private Duration duration;

        @SerializedName("end_address")
        private String endAddress;

        public Distance getDistance() {
            return distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public String getEndAddress() {
            return endAddress;
        }
    }

    public static class Distance {
        @SerializedName("text")
        private String text;

        public String getText() {
            return text;
        }
    }

    public static class Duration {
        @SerializedName("text")
        private String text;

        public String getText() {
            return text;
        }
    }

    public static class OverviewPolyline {
        @SerializedName("points")
        private String points;

        public String getPoints() {
            return points;
        }
    }
}