package com.qa;

import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.util.*;

public class BaseTest {
    protected static AppiumDriver driver;
    protected static Properties props;
    protected static Properties strings;
    protected static String platform;
    protected static String dateTime;
    TestUtils utils;

    @Parameters({"platformName", "udid", "deviceName", "emulator"})
    @BeforeTest
    public void beforeTest(String platformName, String udid, String deviceName, @Optional("Android") String emulator) {
        utils = new TestUtils();
        dateTime = utils.getDateTime();

        System.out.println("platformName: " + platformName);
        platform = platformName;
        String propFileName = "config.properties";
        String stringsFileName = "strings/strings.properties";

        try (InputStream propsInputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
             InputStream stringsInputStream = getClass().getClassLoader().getResourceAsStream(stringsFileName)) {
            props = new Properties();
            props.load(propsInputStream);

            strings = new Properties();
            strings.load(stringsInputStream);

            URL url = new URL(props.getProperty("appiumURL"));
            switch (platformName) {
                case "Android":
                    String androidAppUrl = Objects.requireNonNull(getClass().getClassLoader().getResource(props.getProperty("androidAppLocation"))).getFile();
                    UiAutomator2Options androidOptions = new UiAutomator2Options()
                            .setUdid(udid)
                            .setDeviceName(deviceName)
//                            .setApp(androidAppUrl)
                            .setAppPackage(props.getProperty("androidAppPackage"))
                            .setAppActivity(props.getProperty("androidAppActivity"))
                            .setNewCommandTimeout(Duration.ofMinutes(5));

                    if (emulator.equalsIgnoreCase("true")) {
                        androidOptions.setAvd(deviceName)
                                .setAvdLaunchTimeout(Duration.ofMinutes(10));
                    }

                    driver = new AndroidDriver(url, androidOptions);
                    break;
                case "iOS":
                    String iOSAppUrl = Objects.requireNonNull(getClass().getClassLoader().getResource(props.getProperty("iOSAppLocation"))).getFile();
                    XCUITestOptions iOSOptions = new XCUITestOptions()
                            .setUdid(udid)
                            .setDeviceName(deviceName)
//                            .setApp(iOSAppUrl)
                            .setBundleId(props.getProperty("iOSBundleId"))
                            .setNewCommandTimeout(Duration.ofMinutes(5))
                            .setSimulatorStartupTimeout(Duration.ofMinutes(5))
                            .setSimpleIsVisibleCheck(true);
                    driver = new IOSDriver(url, iOSOptions);
                    break;
                default:
                    throw new Exception("Invalid platform! " + platformName);

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeMethod
    public void beforeMethod() {
        System.out.println("super before method");
        // Docs - https://appium.readthedocs.io/en/latest/en/commands/device/recording-screen/start-recording-screen/

        //  Failure to record iOS videos Refer to https://github.com/appium/appium/issues/16372
//        ((CanRecordScreen) driver).startRecordingScreen(new IOSStartScreenRecordingOptions().withVideoScale("1080:1920"));
        ((CanRecordScreen) driver).startRecordingScreen();

    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        System.out.println("super after method");
        String media = ((CanRecordScreen) driver).stopRecordingScreen();

        if(result.getStatus() == 2){
            Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();

            String dir = "videos" + File.separator
                    + params.get("platformName") + "_" + params.get("deviceName") + File.separator
                    + dateTime + File.separator
                    + result.getTestClass().getRealClass().getSimpleName();

            // TODO do we need this ? - we didn't add the same on capturing screenshots
            File videoDir = new File(dir);
            if (!videoDir.exists()) {
                videoDir.mkdirs();
            }


            try (FileOutputStream stream = new FileOutputStream(videoDir + File.separator + result.getName() + ".mp4")) {
                stream.write(Base64.getDecoder().decode(media));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public AppiumDriver getDriver() {
        return driver;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void waitForVisibility(WebElement e) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        wait.until(ExpectedConditions.visibilityOf(e));
    }

    public void click(WebElement e) {
        waitForVisibility(e);
        e.click();
    }

    public void clear(WebElement e) {
        waitForVisibility(e);
        e.clear();
    }

    public void sendKeys(WebElement e, String txt) {
        waitForVisibility(e);
        e.sendKeys(txt);
    }

    public String getAttribute(WebElement e, String attribute) {
        waitForVisibility(e);
        return e.getAttribute(attribute);
    }

    public String getText(WebElement e) {
        switch (platform) {
            case "Android":
                return getAttribute(e, "text");
            case "iOS":
                return getAttribute(e, "label");
        }
        return null;
    }

    public void closeApp() {
        switch (platform) {
            case "Android":
                ((InteractsWithApps) driver).terminateApp(props.getProperty("androidAppPackage"));
                break;
            case "iOS":
                ((InteractsWithApps) driver).terminateApp(props.getProperty("iOSBundleId"));
                break;
        }
    }

    public void launchApp() {
        switch (platform) {
            case "Android":
                ((InteractsWithApps) driver).activateApp(props.getProperty("androidAppPackage"));
                break;
            case "iOS":
                ((InteractsWithApps) driver).activateApp(props.getProperty("iOSBundleId"));
                break;
        }
    }


    public WebElement androidScrollToElement() {
        /*https://developer.android.com/reference/androidx/test/uiautomator/UiScrollable
        Homework - check why and where we used UiSelector on Android*/

        // with multiple scrollable parent locators on the Android page
       /* return driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector()"
                + ".description(\"android.widget.ScrollView\")).scrollIntoView("
                + "new UiSelector().description(\"test-Price\"));"));*/

        // with 1 scrollable  element on the Android page
        return driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector()"
                        + ".scrollable(true)).scrollIntoView("
                        + "new UiSelector().description(\"test-Price\"));"));
    }

    public void iOSScrollToElement() {

        // scroll without the accessibility id ie use Scrollable Parent
       /* RemoteWebElement parent = (RemoteWebElement) driver.findElement(By.className("XCUIElementTypeScrollView"));
        String parentId = parent.getId();
        HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("element", parentId);
//        scrollObject.put("direction", "down"); // scroll down by length of screen
//        scrollObject.put("predicateString", "label == 'ADD TO CART'");
        scrollObject.put("name", "test-ADD TO CART");*/

        // scroll to element with accessibility id set
        RemoteWebElement element = (RemoteWebElement) driver.findElement(By.name("test-ADD TO CART"));
        String elementId = element.getId();
        HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("element", elementId);
        scrollObject.put("toVisible", "<any text here>");
        driver.executeScript("mobile:scroll", scrollObject);
    }

    @AfterTest
    public void afterTest() {
        driver.quit();
    }

}
