package ru.optimax.testautomation;


public class SiteTestProcessException extends Exception {
    public SiteTestProcessException(String message) {
        super(message);
    }

    public SiteTestProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
