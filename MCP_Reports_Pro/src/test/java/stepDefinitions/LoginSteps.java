package stepDefinitions;

import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import utils.DriverFactory;
import hooks.Hooks;
import java.time.Duration;

public class LoginSteps {

    WebDriver driver;

    @Given("user opens demoqa login page")
    public void openLoginPage() {
        driver = DriverFactory.getDriver();
        driver.get("https://demoqa.com/login");
        Hooks.getTest().info("Opened login page");
    }

    @When("user enters credentials from excel")
    public void enterCredentials() {

        String[] data = Hooks.getCurrentRowData();
        String username = data[0];
        String password = data[1];

        // Wait for username field to appear first
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement userField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("userName"))
        );

        // Clear fields first then type
        userField.clear();
        userField.sendKeys(username);

        WebElement passField = driver.findElement(By.id("password"));
        passField.clear();
        passField.sendKeys(password);

        // Wait for login button then click
        WebElement loginBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("login"))
        );
        loginBtn.click();

        Hooks.getTest().info("Entered Username: " + username);
    }

   @Then("verify login result from excel")
public void verifyResult() {

    String[] data = Hooks.getCurrentRowData();
    String expected = data[2].trim();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    try {
        // Wait for either success OR failure
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("profile"),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//p[@id='name' and string-length(text())>0]")
                )
        ));

        String currentUrl = driver.getCurrentUrl();
        boolean isErrorVisible = driver.findElements(
                By.xpath("//p[@id='name' and string-length(text())>0]")
        ).size() > 0;

        if (expected.equalsIgnoreCase("pass")) {

            if (currentUrl.contains("profile")) {
                Hooks.getTest().pass("✅ Login passed");
            } else {
                throw new Exception("Expected PASS but login failed");
            }

        } else {

            if (isErrorVisible) {
                Hooks.getTest().pass("✅ Login failed as expected");
            } else {
                throw new Exception("Expected FAIL but login passed");
            }
        }

    } catch (Exception e) {
        String currentUrl = driver.getCurrentUrl();
        Hooks.getTest().fail("❌ Test failed. Current URL: " + currentUrl);
        assert false : "Test failed. URL: " + currentUrl;
    }
}
}