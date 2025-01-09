package org.purushotham.abstractcomponents;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AbstractComponent {

    private WebDriver driver;
    protected WebDriverWait wait;

    public AbstractComponent(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10L));
    }

    public void waitForWebElementToAppear(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForWebElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void clearAndEnterText(WebElement element, String text) {
        element.clear();
//        element.sendKeys(Keys.CONTROL, Keys.chord("A"), Keys.BACK_SPACE);
        element.sendKeys(Keys.CONTROL + "A");
        element.sendKeys(Keys.BACK_SPACE);

        element.sendKeys(text);
    }

    public void waitForTextToPresentInElement(WebElement element, String text) {
        wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public void waitForWebElementToDisappear(WebElement element) {
        try {
            wait.until(ExpectedConditions.invisibilityOf(element));
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println(e.getMessage());
        }
    }


}
