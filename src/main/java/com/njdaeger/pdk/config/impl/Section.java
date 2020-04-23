package com.njdaeger.pdk.config.impl;

import com.njdaeger.pdk.config.IConfig;
import com.njdaeger.pdk.config.ISection;

public class Section implements ISection {

    private final IConfig config;
    private final String path;

    public Section(String path, IConfig config) {
        this.config = config;
        this.path = path;
    }

    @Override
    public String getCurrentPath() {
        return path;
    }

    @Override
    public IConfig getConfig() {
        return config;
    }
}
