package ru.optimax.testautomation;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class SiteTest {

    public static List<TestResult> run(WebPagetest webpagetest, Properties properties) throws SiteTestProcessException {
        List<TestResult> resultsList;

        try {

            String login = properties.getProperty("login");
            String pass = properties.getProperty("pass");
            webpagetest.login(login, pass);

            String[] urlsToTest = properties.getProperty("pages").split(",");

            resultsList = new LinkedList<>();

            for (String testUrl : urlsToTest
                 ) {

                WebPagetest.HomePage homePage = webpagetest.home();
                homePage.setUrl(testUrl);
                homePage.setLocation(properties.getProperty("location"));
                homePage.setTestsNumber(Integer.parseInt(properties.getProperty("subtestNumber")));
                homePage.enableVideoCapture(Boolean.parseBoolean(properties.getProperty("videocaptureEnable")));
                homePage.setKeepTestPrivate(Boolean.parseBoolean(properties.getProperty("keepTestPrivate")));

                WebPagetest.TestStartingPage testStartingPage = homePage.startTest();
                WebPagetest.TestResultPage testResultPage = testStartingPage
                        .waitTestStart(Integer.parseInt(properties.getProperty("startWaitTimeout")));
                testResultPage.waitResult(Integer.parseInt(properties.getProperty("resultWaitTimeout")));
                TestResult testResult = new TestResult(testUrl.trim());
                testResult.setLoadTime(testResultPage.getLoadTime());
                testResult.setScreenshot(testResultPage.getScreenshot());
                resultsList.add(testResult);
            }

            webpagetest.close();

        } catch (PageControlException | TimeoutException e) {
            throw new SiteTestProcessException("Unable to finish test.", e);
        }
        return resultsList;
    }
}
