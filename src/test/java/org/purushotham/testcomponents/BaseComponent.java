package org.purushotham.testcomponents;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.purushotham.pageobjects.LandingPage;
import org.purushotham.utils.ConfigParser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.util.List;

public class BaseComponent {
    private String browser;
    protected WebDriver driver;
    protected LandingPage landingPage;
    protected ConfigParser configParser;
    protected String url;
    protected String userEmail;
    protected String userPassword;

    protected BaseComponent() {
        configParser = new ConfigParser();
        browser = configParser.getString("browser");
        url = configParser.getString("url");
        userEmail = configParser.getString("email");
        userPassword = configParser.getString("password");
    }


    private WebDriver initializeDriver() {
        ChromeOptions options = new ChromeOptions();
//        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
//        options.setExperimentalOption("excludeSwitches", List.of("disable-popup-blocking"));
        options.setBinary("C:\\Users\\Admin\\AppData\\Local\\Google\\Chrome SxS\\Application\\chrome.exe");
        options.setExperimentalOption("debuggerAddress", "localhost:9222");
        if (browser.contains("chrome")) {
            if (browser.contains("headless")) {
                options.addArguments("--headless=new");
            }
            driver = new ChromeDriver(options);
        }
        driver.manage().window().maximize();
        return driver;
    }

    @BeforeClass
    public void launchApplication() {
        driver = initializeDriver();
//        driver.get("https://mail.google.com/mail/");
        landingPage = new LandingPage(driver);
//        landingPage.goToUrl(url);
    }

    @AfterClass
    public void tearBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
