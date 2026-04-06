package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class DriverFactory {

    // Each parallel test gets its OWN browser
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // Track which browser to use next (rotates between tests)
    private static ThreadLocal<String> browserName = new ThreadLocal<>();

    // Grid Hub URL
    private static final String GRID_URL = "http://localhost:4444/wd/hub";

    // Rotate browsers for each test
    private static final String[] BROWSERS = {
            "chrome",   // test 1
            "firefox",  // test 2
            "edge",     // test 3
            "chrome",   // test 4
            "firefox",  // test 5
            "edge",     // test 6
            "chrome",   // test 7
            "firefox",  // test 8
            "edge",     // test 9
            "chrome"    // test 10
    };

    private static int browserIndex = 0;

    public static WebDriver initDriver() {

        // Pick browser for this test
        String browser;
        synchronized (DriverFactory.class) {
            browser = BROWSERS[browserIndex % BROWSERS.length];
            browserIndex++;
        }

        browserName.set(browser);

        try {
            WebDriver remoteDriver;

            switch (browser.toLowerCase()) {

                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    remoteDriver = new RemoteWebDriver(
                            new URL(GRID_URL), firefoxOptions
                    );
                    break;

                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    remoteDriver = new RemoteWebDriver(
                            new URL(GRID_URL), edgeOptions
                    );
                    break;

                case "chrome":
                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    remoteDriver = new RemoteWebDriver(
                            new URL(GRID_URL), chromeOptions
                    );
                    break;
            }

            driver.set(remoteDriver);
            driver.get().manage().window().maximize();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "❌ Could not connect to Selenium Grid at: " + GRID_URL +
                            "\nMake sure Grid is running!"
            );
        }

        return driver.get();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static String getBrowserName() {
        return browserName.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}