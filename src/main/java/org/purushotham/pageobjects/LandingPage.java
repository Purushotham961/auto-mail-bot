package org.purushotham.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.purushotham.abstractcomponents.AbstractComponent;

import java.sql.Driver;

public class LandingPage extends AbstractComponent {
    private WebDriver driver;
    public LandingPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void goToUrl(String url) {
        if (driver != null) {
            driver.get(url);
        } else {
            throw new IllegalStateException("driver is null");
        }
    }
}
