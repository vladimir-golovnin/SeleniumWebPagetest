package ru.optimax.testautomation;


public class PageControlException extends Exception {
    public PageControlException(String message) {
        super(message);
    }

    public PageControlException(String message, Throwable cause) {
        super(message, cause);
    }
}
