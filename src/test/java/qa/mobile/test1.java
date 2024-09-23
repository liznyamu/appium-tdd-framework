package qa.mobile;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class test1 {

    AppiumDriver driver;

    @Test
    public void invalidUserName(){
        WebElement usernameTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Username"));
        WebElement passwordTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        WebElement loginBtn = driver.findElement(AppiumBy.accessibilityId("test-LOGIN"));

        usernameTxtFld.sendKeys("invalidusername");
        passwordTxtFld.sendKeys("secret_sauce");
        loginBtn.click();

        WebElement errTxt = driver.findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView"));
        String expectedErrTxt = "Username and password do not match any user in this service.";
        Assert.assertEquals(errTxt.getAttribute("text"), expectedErrTxt);
    }

    @Test
    public void invalidPassword(){
        WebElement usernameTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Username"));
        WebElement passwordTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        WebElement loginBtn = driver.findElement(AppiumBy.accessibilityId("test-LOGIN"));

        usernameTxtFld.sendKeys("standard_user");
        passwordTxtFld.sendKeys("invalidPassword");
        loginBtn.click();

        WebElement errTxt = driver.findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView"));
        String expectedErrTxt = "Username and password do not match any user in this service.";
        Assert.assertEquals(errTxt.getAttribute("text"), expectedErrTxt);
    }

    @Test
    public void successfulLogin(){
        WebElement usernameTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Username"));
        WebElement passwordTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        WebElement loginBtn = driver.findElement(AppiumBy.accessibilityId("test-LOGIN"));

        usernameTxtFld.sendKeys("standard_user");
        passwordTxtFld.sendKeys("secret_sauce");
        loginBtn.click();

        WebElement productTitleTxt = driver.findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-Toggle\"]/preceding-sibling::android.widget.TextView"));
        String expectedErrTxt = "PRODUCTS";
        Assert.assertEquals(productTitleTxt.getAttribute("text"), expectedErrTxt);
    }


    @BeforeClass
    public void beforeClass() throws MalformedURLException {

        /* String appUrl = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator + "saucelabs_app/Android.SauceLabs.Mobile.Sample.app.2.7.1.apk";
     */
        UiAutomator2Options options = new UiAutomator2Options()
                .setUdid("emulator-5554")
//                .setApp(appUrl)
                .setAppPackage("com.swaglabsmobileapp")
                .setAppActivity(".MainActivity")
                .setNewCommandTimeout(Duration.ofMinutes(5));

        URL url = new URL("http://127.0.0.1:4723");

        driver = new AndroidDriver(url, options);
        // implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));



    }

    @AfterClass
    public void afterClass(){
        driver.quit();
    }
}
