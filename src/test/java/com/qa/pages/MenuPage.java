package com.qa.pages;

import com.qa.BaseTest;
import com.qa.utils.TestUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class MenuPage extends BaseTest {

    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Menu\"]/android.view.ViewGroup/android.widget.ImageView")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Menu\"]/XCUIElementTypeOther")
    private WebElement settingsBtn;

    public MenuPage(){
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    public SettingsPage pressSettingsBtn(){
        // TODO fails on iOS - menu is not opened
        click(settingsBtn, "press Settings button");
        return new SettingsPage();
    }
}
