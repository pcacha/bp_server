package cz.zcu.students.cacha.bp_server.selenium_tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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
        // redirect to signup page
        driver.get("http://localhost:8080");
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
        List<WebElement> found = driver.findElements(By.xpath("//button[contains(text(), '" + text + "')]"));
        // click on all elements with given text
        for(WebElement we : found) {
            we.click();
        }
    }

    /**
     * clicks on element with given class
     * @param text class name
     */
    public void clickElementWithClass(String text) {
        // find elements
        List<WebElement> found = driver.findElements(By.cssSelector(text));
        // click on all elements with given text
        for(WebElement we : found) {
            if(we.isDisplayed()) {
                we.click();
            }
        }
    }

    /**
     * clicks on anchor with given text
     * @param text anchor text
     */
    public void clickAnchorWithText(String text) {
        // find elements
        List<WebElement> found = driver.findElements(By.xpath("//a[contains(text(), '" + text + "')]"));
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
        w.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h1")));
    }

    /**
     * changes text in editor
     * @param text text to insert
     */
    public void changeEditorText(String text) {
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/section/div/form/div/div[2]/div[2]/div/p")).sendKeys(text);
    }

    /**
     * waits until page for translating is visible
     */
    public void waitUntilTranslationPage() {
        WebDriverWait w = new WebDriverWait(driver, 5);
        w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//h2[contains(text(), 'New Translation')]")));
    }

    /**
     * waits utnil page with exhibits is visible
     */
    public void waitUntilExhibitPage() {
        WebDriverWait w = new WebDriverWait(driver, 5);
        w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//button[contains(text(), 'Rate')]")));
    }

    /**
     * Selects first value in select
     */
    public void selectLanguageInSelect() {
        driver.findElement(By.tagName("input")).sendKeys("Czech");
        driver.findElement(By.tagName("input")).sendKeys(Keys.RETURN);
    }

    /**
     * waits until page with institutions is visible
     */
    public void waitUntilInstitutionPage() {
        WebDriverWait w = new WebDriverWait(driver, 5);
        w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(), 'Exhibits')]")));
    }

    /**
     * clicks on translate button in exhibits page
     */
    public void clickTranslateBtn() {
        // find elements
        List<WebElement> found = driver.findElements(By.xpath("//button[contains(text(), 'Translate')]"));
        // click on all elements with given text
        for(WebElement we : found) {
            // skip breadcrumb button
            if(!we.getText().contains("Institutions")) {
                we.click();
            }
        }
    }
}
