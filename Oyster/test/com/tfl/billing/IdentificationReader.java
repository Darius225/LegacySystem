package com.tfl.billing;

import com.oyster.ScanListener;

import java.util.UUID;

public interface IdentificationReader {

    void register(ScanListener scanListener);

    UUID id();

    void touch(Identification card);
}
