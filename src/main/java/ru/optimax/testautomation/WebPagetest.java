package ru.optimax.testautomation;


import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WebPagetest {
    private final WebDriver driver;
    private final HomePage homePage;
    private final TestStartingPage testStartingPage;
    private final TestResultPage testResultPage;
    private static final String HOME_URL = "https://www.webpagetest.org";

    public WebPagetest(WebDriver driver) {
        this.driver = driver;
        homePage = new HomePage();
        testStartingPage = new TestStartingPage();
        testResultPage = new TestResultPage();
    }

    public HomePage home(){
        homePage.go();
        return homePage;
    }

    public void close(){
        driver.quit();
    }

    private static final String LOGIN_URL = HOME_URL;
    private static final String LOGIN_LINK_TEXT = "Login";
    private static final String USERNAME_FIELD_NAME = "username";
    private static final String PASSWORD_FIELD_NAME = "password";

    public void login(String username, String pass) throws PageControlException {
        driver.get(LOGIN_URL);
        try {
            WebElement loginLink = driver.findElement(By.linkText(LOGIN_LINK_TEXT));
            loginLink.click();
            WebElement usernameField = driver.findElement(By.name(USERNAME_FIELD_NAME));
            WebElement passwordField = driver.findElement(By.name(PASSWORD_FIELD_NAME));

            usernameField.sendKeys(username);
            passwordField.sendKeys(pass);
            passwordField.submit();
        }catch (WebDriverException e){
            throw new PageControlException("Unable to login", e);
        }
    }

    public class HomePage{
        private static final String PAGE_URL = "https://www.webpagetest.org";

        private HomePage(){
        }

        public void go(){
            driver.get(PAGE_URL);
        }

        private static final String URL_FIELD_NAME = "url";

        public void setUrl(String url) throws PageControlException {
            try {
                WebElement siteUrl = driver.findElement(By.name(URL_FIELD_NAME));
                siteUrl.sendKeys(url);
            }catch (WebDriverException e){
                throw new PageControlException("Can't find URL field", e);
            }
        }

        public void setLocation(String location) throws PageControlException {
            try {
                WebElement locationOption = driver.findElement(By.cssSelector("option[value*=" + location + "]"));
                locationOption.click();
            }catch (WebDriverException e){
                throw new PageControlException("Can't find such location", e);
            }
        }

        private static final String ADVANCED_SETTINGS = "advanced_settings";

        public void openAdvancedSettings() throws PageControlException {
            try {
                WebElement settingsLink = driver.findElement(By.id(ADVANCED_SETTINGS));
                if(!settingsLink.getAttribute("class").equals("extended")) {
                    settingsLink.click();
                }
            }catch (WebDriverException e){
                throw new PageControlException("Can't find advanced settings", e);
            }
        }

        private static final String NUMBER_OF_TESTS_BOX_ID = "number_of_tests";

        public void setTestsNumber(int num) throws IllegalArgumentException, PageControlException {
            final int MAX_TESTS_NUM = 9;
            final int MIN_TESTS_NUM = 1;
            if(num > MAX_TESTS_NUM || num < MIN_TESTS_NUM)
                throw new IllegalArgumentException("Illegal number of tests:" + num);
            try {
                openAdvancedSettings();
                WebElement testNumberInputBox = driver.findElement(By.id(NUMBER_OF_TESTS_BOX_ID));
                testNumberInputBox.clear();
                testNumberInputBox.sendKeys(Integer.toString(num));
            }catch (WebDriverException e){
                throw new PageControlException("Can't find test number input box", e);
            }
        }

        private static final String VIDEOCAPTURE_CHECKBOX_ID = "videoCheck";

        public void enableVideoCapture(boolean enable) throws PageControlException {
            try {
                openAdvancedSettings();
                setCheckbox(VIDEOCAPTURE_CHECKBOX_ID, enable);
            }catch (WebDriverException e){
                throw new PageControlException("Can't find video capture checkbox", e);
            }
        }

        private static final String KEEPTESTPRIVATE_CHECKBOX_ID = "keep_test_private";

        public void setKeepTestPrivate(boolean enable) throws PageControlException {
            try {
                openAdvancedSettings();
                setCheckbox(KEEPTESTPRIVATE_CHECKBOX_ID, enable);
            }catch (WebDriverException e){
                throw new PageControlException("Can't find keep test private checkbox", e);
            }
        }

        private void setCheckbox(String checkboxId, boolean state){
            WebElement checkbox = driver.findElement(By.id(checkboxId));
            if(checkbox.isSelected() != state){
                checkbox.click();
            }
        }

        private static final String START_BUTTON_NAME = "submit";

        public TestStartingPage startTest() throws PageControlException {
            try {
                WebElement startButton = driver.findElement(By.name(START_BUTTON_NAME));
                startButton.click();
            }catch (WebDriverException e){
                throw new PageControlException("Cant't find startbutton element", e);
            }
            if(testStartingPage.isActive()) return testStartingPage;
            else throw new PageControlException("Cannot start page test");

        }
    }

    public class TestStartingPage{
        private TestStartingPage(){

        }

        private static final String TEST_START_PAGE_TITLE = "WebPagetest - Running web page performance and optimization test";

        public boolean isActive(){
            return driver.getTitle().contains(TEST_START_PAGE_TITLE);
        }

        public TestResultPage waitTestStart(int waitDurationMinutes) throws java.util.concurrent.TimeoutException {
            try {
                new WebDriverWait(driver, TimeUnit.MINUTES.toSeconds(waitDurationMinutes))
                        .until(webDriver -> !webDriver.getTitle().contains(TEST_START_PAGE_TITLE));
                if(testResultPage.isActive()) return testResultPage;
                else throw new TimeoutException();
            }catch (TimeoutException e){
                throw new java.util.concurrent.TimeoutException("Page test start timeout");
            }
        }
    }

    public class TestResultPage{
        private TestResultPage(){

        }

        private static final String TEST_RESULT_PAGE_TITLE = "WebPagetest Test Result";

        public boolean isActive(){
            return driver.getTitle().contains(TEST_RESULT_PAGE_TITLE);
        }

        private static final String TEST_FINISHED_ELEMENT_ID = "average";
        private final By RESULT_PRESENT_ATTRIBUTE = By.id(TEST_FINISHED_ELEMENT_ID);

        public void waitResult(int waitDurationMinutes) throws java.util.concurrent.TimeoutException {
            try {
                new WebDriverWait(driver, TimeUnit.MINUTES.toSeconds(waitDurationMinutes))
                        .until(ExpectedConditions.presenceOfElementLocated(RESULT_PRESENT_ATTRIBUTE));
            }catch (TimeoutException e){
                throw new java.util.concurrent.TimeoutException("Page test result wait timeout");
            }
        }

        private static final String LOAD_TIME_FIELD_ID = "LoadTime";

        public String getLoadTime() throws PageControlException {
            try {
                WebElement loadTimeField = driver.findElement(By.id(LOAD_TIME_FIELD_ID));
                return loadTimeField.getText();
            }catch (WebDriverException e){
                throw new PageControlException("Unable to find load time field", e);
            }
        }

        public File getScreenshot() throws PageControlException {
            driver.manage().window().fullscreen();
            BufferedImage screenshot = Shutterbug.shootPage(driver, ScrollStrategy.BOTH_DIRECTIONS).getImage();
            File screenshotFile;
            try {
                screenshotFile = File.createTempFile("screenshot", ".png");
                ImageIO.write(screenshot, "PNG", screenshotFile);
                return screenshotFile;
            } catch (IOException e) {
                throw new PageControlException("Unable to save screenshot for test result page", e);
            }
        }
    }
}
