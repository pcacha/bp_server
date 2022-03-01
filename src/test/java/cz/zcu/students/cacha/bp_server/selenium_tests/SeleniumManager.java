package cz.zcu.students.cacha.bp_server.selenium_tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class for managing selenium actions
 */
public class SeleniumManager {
    /**
     * selenium web driver
     */
    private WebDriver driver;

    /**
     * initializes selenium web driver
     */
    public SeleniumManager() {
        // set path to chrome driver
        System.setProperty("webdriver.chrome.driver", new File("chromedriver.exe").getAbsolutePath());
        driver =  new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        // redirect to singup page
        driver.get("http://localhost:3000/signup");
    }

    /**
     * closes browser window
     */
    public void closeWindow() {
        driver.close();
    }

    /**
     * fills text input with given placeholder
     * @param placeholder input placeholder
     * @param text filled text
     */
    public void fillInput(String placeholder, String text) {
        WebElement input = driver.findElement(By.xpath("//input[@placeholder='" + placeholder + "']"));
        input.sendKeys(text);
    }

    /**
     * clicks on button with given text
     * @param text button text
     */
    public void clickBtnWithText(String text) {
        // find elements
        List<WebElement> found = driver.findElements(By.xpath("//*[contains(text(), '" + text + "')]"));
        // click on all elements with given text
        for(WebElement we : found) {
            we.click();
        }
    }

    /**
     * waits until h1 element is visible
     */
    public void waitUntilH1Appears() {
        WebDriverWait w = new WebDriverWait(driver, 5);
        w.until(ExpectedConditions.presenceOfElementLocated (By.cssSelector("h1")));
    }

    /**
     * redirects to given url
     * @param url url
     */
    public void getURLAndWait(String url) {
        driver.get(url);
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.urlToBe(url));
    }

    /**
     * changes text in editor
     * @param text text to insert
     */
    public void changeEditorText(String text) {
        driver.findElement(By.tagName("p")).sendKeys(text);
    }

    /**
     * waits until page for translating is visible
     */
    public void waitUntilTranslationPage() {
        WebDriverWait w = new WebDriverWait(driver, 5);
        w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//h2[contains(text(), 'Exhibits')]")));
    }
}
