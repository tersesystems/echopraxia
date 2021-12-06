package com.tersesystems.echopraxia;

import org.slf4j.Marker;

public class Markers {

    private final Marker[] markers;

    public Markers(Marker[] markers) {
        this.markers = markers;
    }

    static Markers of(org.slf4j.Marker... markers) {
        return new Markers(markers);
    }

    public Marker asMarker() {
        return markers[0]; // XXX Return all the markers
    }
}
