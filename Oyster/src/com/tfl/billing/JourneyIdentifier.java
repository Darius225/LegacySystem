package com.tfl.billing;

public interface JourneyIdentifier {
    boolean isLong(Journey journey);

    boolean isPeak(Journey journey);
}
