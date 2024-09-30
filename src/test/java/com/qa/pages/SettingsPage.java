package com.qa.pages;

import com.qa.BaseTest;
import com.qa.utils.TestUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class SettingsPage extends BaseTest {
    @AndroidFindBy(accessibility = "test-LOGOUT")
    @iOSXCUITFindBy(id = "test-LOGOUT")
    private WebElement logoutBtn;

    public SettingsPage(){
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    public LoginPage pressLogoutBtn(){
        click(logoutBtn, "press logout button");
        return new LoginPage();
    }
}
