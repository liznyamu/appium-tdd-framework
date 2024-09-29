package com.qa;

import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
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
    // Global variables
    protected static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    protected static ThreadLocal<Properties> props = new ThreadLocal<>();
    protected static ThreadLocal<Properties> strings = new ThreadLocal<>();
    protected static ThreadLocal<String> platform = new ThreadLocal<>();
    protected static ThreadLocal<String> dateTime = new ThreadLocal<>();

    public AppiumDriver getDriver() {
        return driver.get();
    }

    public void setDriver(AppiumDriver driver1) {
        driver.set(driver1);
    }

    public Properties getProps() {
        return props.get();
    }

    public void setProps(Properties props1) {
        props.set(props1);
    }

    public Properties getStrings() {
        return strings.get();
    }

    public void setStrings(Properties strings1) {
        strings.set(strings1);
    }

    public String getPlatform() {
        return platform.get();
    }

    public void setPlatform(String platform1) {
        platform.set(platform1);
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public void setDateTime(String dateTime1) {
        dateTime.set(dateTime1);
    }

    @Parameters({"platformName", "udid", "deviceName",
            "emulator", "systemPort", "chromeDriverPort",
            "wdaLocalPort", "webkitDebugProxyPort"})
    @BeforeTest
    public void beforeTest(String platformName, String udid, String deviceName,
                           @Optional("androidOnly") String emulator, @Optional("androidOnly") String systemPort,
                           @Optional("androidOnly") String chromeDriverPort, @Optional("iOSOnly") String wdaLocalPort,
                           @Optional("iOSOnly") String webkitDebugProxyPort ) {
        TestUtils utils = new TestUtils();
        setDateTime(utils.dateTime());

        System.out.println("platformName: " + platformName);
        setPlatform( platformName);

        String propFileName = "config.properties";
        String stringsFileName = "strings/strings.properties";

        Properties props = new Properties();
        Properties strings = new Properties();
        AppiumDriver driver;
        try (InputStream propsInputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
             InputStream stringsInputStream = getClass().getClassLoader().getResourceAsStream(stringsFileName)) {

            props.load(propsInputStream);
            setProps(props);

            strings.load(stringsInputStream);
            setStrings(strings);

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
                            .setNewCommandTimeout(Duration.ofMinutes(5))
                            .setSystemPort(Integer.parseInt(systemPort))
                            .setChromedriverPort(Integer.parseInt(chromeDriverPort));

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
                            .setSimpleIsVisibleCheck(true)
                            .setWdaLocalPort(Integer.parseInt(wdaLocalPort));
                    // webkitDebugProxyPort capability  not set

                    driver = new IOSDriver(url, iOSOptions);
                    break;
                default:
                    throw new Exception("Invalid platform! " + platformName);
            }

            setDriver(driver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeMethod
    public void beforeMethod() {
        // Docs - https://appium.readthedocs.io/en/latest/en/commands/device/recording-screen/start-recording-screen/

        //  Failure to record iOS videos Refer to https://github.com/appium/appium/issues/16372
//        ((CanRecordScreen) driver).startRecordingScreen(new IOSStartScreenRecordingOptions().withVideoScale("1080:1920"));
        ((CanRecordScreen) getDriver()).startRecordingScreen();

    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        String media = ((CanRecordScreen) getDriver()).stopRecordingScreen();

        if (result.getStatus() == 2) {
            File videoDir = getFile(result);


            try (FileOutputStream stream = new FileOutputStream(videoDir + File.separator + result.getName() + ".mp4")) {
                stream.write(Base64.getDecoder().decode(media));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private File getFile(ITestResult result) {
        Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();

        String dir = "videos" + File.separator
                + params.get("platformName") + "_" + params.get("deviceName") + File.separator
                + getDateTime() + File.separator
                + result.getTestClass().getRealClass().getSimpleName();

        File videoDir = new File(dir);
        // Added synchronization for tutorial purposes - can be added on getting race conditions
        synchronized (videoDir){
            if (!videoDir.exists()) {
                videoDir.mkdirs();
            }
        }

        return videoDir;
    }

    public void waitForVisibility(WebElement e) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(TestUtils.WAIT));
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
        switch (getPlatform()) {
            case "Android":
                return getAttribute(e, "text");
            case "iOS":
                return getAttribute(e, "label");
        }
        return null;
    }

    public void closeApp() {
        switch (getPlatform()) {
            case "Android":
                ((InteractsWithApps) getDriver()).terminateApp(getProps().getProperty("androidAppPackage"));
                break;
            case "iOS":
                ((InteractsWithApps) getDriver()).terminateApp(getProps().getProperty("iOSBundleId"));
                break;
        }
    }

    public void launchApp() {
        switch (getPlatform()) {
            case "Android":
                ((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("androidAppPackage"));
                break;
            case "iOS":
                ((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("iOSBundleId"));
                break;
        }
    }


    public WebElement androidScrollToElement() {
        /*https://developer.android.com/reference/androidx/test/uiautomator/UiScrollable
        Homework - check why and where we used UiSelector on Android*/

        // with multiple scrollable parent locators on the Android page
       /* return getDriver().findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector()"
                + ".description(\"android.widget.ScrollView\")).scrollIntoView("
                + "new UiSelector().description(\"test-Price\"));"));*/

        // with 1 scrollable  element on the Android page
        return getDriver().findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector()"
                        + ".scrollable(true)).scrollIntoView("
                        + "new UiSelector().description(\"test-Price\"));"));
    }

    public void iOSScrollToElement() {

        // scroll without the accessibility id ie use Scrollable Parent
       /* RemoteWebElement parent = (RemoteWebElement) getDriver().findElement(By.className("XCUIElementTypeScrollView"));
        String parentId = parent.getId();
        HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("element", parentId);
//        scrollObject.put("direction", "down"); // scroll down by length of screen
//        scrollObject.put("predicateString", "label == 'ADD TO CART'");
        scrollObject.put("name", "test-ADD TO CART");*/

        // scroll to element with accessibility id set
        RemoteWebElement element = (RemoteWebElement) getDriver().findElement(By.name("test-ADD TO CART"));
        String elementId = element.getId();
        HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("element", elementId);
        scrollObject.put("toVisible", "<any text here>");
        getDriver().executeScript("mobile:scroll", scrollObject);
    }

    @AfterTest
    public void afterTest() {
        getDriver().quit();
    }

}
