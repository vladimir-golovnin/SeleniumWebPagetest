package ru.optimax.testautomation;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class App
{
    private static final String TEST_PROPERTIES_FILE = "/test.properties";
    private static final String MAIL_PROPERTIES_FILE = "/mail.properties";
    private static final String WEBDRIVER_PROPERTIES_FILE = "/webdriver.properties";

    public static void main( String[] args ) {

        try {
            WebDriver webDriver = initWebDriver(readPropertiesFromFile(WEBDRIVER_PROPERTIES_FILE));
            WebPagetest webPagetest = new WebPagetest(webDriver);

            try {
                List<TestResult> testResults = SiteTest.run(webPagetest, getTestProperties());

                try {
                    ReportsSender reportsSender = new ReportsSender(MAIL_PROPERTIES_FILE);
                    try {
                        reportsSender.sendReport(testResults);
                    } catch (MessagingException | FileNotFoundException e) {
                        System.out.println("Unable to sent report: " + e.getMessage());
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Unable to open mail properties file");
                }
            }catch (SiteTestProcessException e){
                System.out.print(e.getMessage());
            }
        }catch (WebDriverException e){
            System.out.println("Unable to connect to webdriver.");
        }catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    private static WebDriver initWebDriver(Properties properties){

        DesiredCapabilities webDriverCapabilities = new DesiredCapabilities();
        String browserName = properties.getProperty("browser");
        URL driverUrl;
        try {
            driverUrl = new URL(properties.getProperty("url"));
        } catch (MalformedURLException e) {
            throw new WebDriverException();
        }
        webDriverCapabilities.setBrowserName(browserName);
        return new RemoteWebDriver(driverUrl, webDriverCapabilities);
    }

    private static Properties getTestProperties(){
        Properties properties = null;
        try {
            properties = readPropertiesFromFile(TEST_PROPERTIES_FILE);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return properties;
    }

    private static Properties readPropertiesFromFile(String path) throws FileNotFoundException {
        Properties props = new Properties();
        try(InputStream propertiesStream = App.class.getResourceAsStream(path)){
            props.load(propertiesStream);
        } catch (IOException | NullPointerException e) {
            throw new FileNotFoundException("Can't open properties file " + path);
        }
        return props;
    }


}
