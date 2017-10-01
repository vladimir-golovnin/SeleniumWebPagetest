package ru.optimax.testautomation;


import java.io.File;

public class TestResult {
    private final String url;
    private String loadTime;
    private File screenshot;

    public TestResult(String url) {
        this.url = url;
    }

    public void setScreenshot(File screenshot) {
        this.screenshot = screenshot;
    }

    public File getScreenshot() {
        return screenshot;
    }


    public String getUrl() {
        return url;
    }

    public String getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }
}
