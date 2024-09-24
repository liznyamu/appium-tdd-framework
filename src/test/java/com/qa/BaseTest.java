package com.qa;

import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

public class BaseTest {
    protected static AppiumDriver driver;
    protected static Properties props;
    protected static Properties strings;
    protected static String platform;
    InputStream inputStream;

    @Parameters({"platformName", "udid", "deviceName"})
    @BeforeTest
    public void beforeTest(String platformName, String udid, String deviceName) {
        System.out.println("platformName: " + platformName);
        platform = platformName;
        String propFileName = "config.properties";
        String stringsFileName = "strings/strings.properties";

        try(InputStream propsInputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            InputStream stringsInputStream = getClass().getClassLoader().getResourceAsStream(stringsFileName)) {
            props = new Properties();
            props.load(propsInputStream);

            strings = new Properties();
            strings.load(stringsInputStream);

            URL url = new URL(props.getProperty("appiumURL"));
            switch (platformName){
                case "Android":
                    String androidAppUrl = Objects.requireNonNull(getClass().getClassLoader().getResource(props.getProperty("androidAppLocation"))).getFile();
                    UiAutomator2Options androidOptions = new UiAutomator2Options()
                            .setUdid(udid)
                            .setDeviceName(deviceName)
//                            .setApp(androidAppUrl)
                            .setAppPackage(props.getProperty("androidAppPackage"))
                            .setAppActivity(props.getProperty("androidAppActivity"))
                            .setNewCommandTimeout(Duration.ofMinutes(5));

                    driver = new AndroidDriver(url, androidOptions);
                    break;
                case "iOS":
                    String iOSAppUrl = Objects.requireNonNull(getClass().getClassLoader().getResource(props.getProperty("iOSAppLocation"))).getFile();
                    XCUITestOptions iOSOptions = new XCUITestOptions()
                            .setUdid(udid)
                            .setDeviceName(deviceName)
//                            .setApp(iOSAppUrl)
                            .setBundleId(props.getProperty("iOSBundleId"))
                            .setNewCommandTimeout(Duration.ofMinutes(5));
                    driver = new IOSDriver(url, iOSOptions);
                    break;
                default:
                    throw new Exception("Invalid platform! "+ platformName);

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void waitForVisibility(WebElement e){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        wait.until(ExpectedConditions.visibilityOf(e));
    }

    public void click(WebElement e){
        waitForVisibility(e);
        e.click();
    }

    public void clear(WebElement e){
        waitForVisibility(e);
        e.clear();
    }

    public void sendKeys(WebElement e, String txt){
        waitForVisibility(e);
        e.sendKeys(txt);
    }

    public String getAttribute(WebElement e, String attribute){
        waitForVisibility(e);
        return e.getAttribute(attribute);
    }

    public String getText(WebElement e){
        switch (platform){
            case "Android":
                return getAttribute(e, "text");
            case "iOS":
                return getAttribute(e, "label");
        }
        return null;
    }


    @AfterTest
    public void afterTest(){
        driver.quit();
    }

}
