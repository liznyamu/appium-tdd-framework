package com.qa.pages;

import com.qa.BaseTest;
import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BaseTest {
    @AndroidFindBy(accessibility = "test-Username")
    @iOSXCUITFindBy(id = "test-Username")
    WebElement usernameTxtFld;
    @AndroidFindBy(accessibility = "test-Password")
    @iOSXCUITFindBy(id = "test-Password")
    WebElement passwordTxtFld;
    @AndroidFindBy(accessibility = "test-LOGIN")
    @iOSXCUITFindBy(id = "test-LOGIN")
    WebElement loginBtn;
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Error message\"]/child::XCUIElementTypeStaticText")
    WebElement errTxt;

    TestUtils utils = new TestUtils();

    public LoginPage() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    public LoginPage enterUserName(String username) {
        // TODO remove clear() - tests should be application/user state agnostic
        clear(usernameTxtFld);
        sendKeys(usernameTxtFld, username, "login with " + username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        // TODO remove clear() - tests should be application/user state agnostic
        clear(passwordTxtFld);
        sendKeys(passwordTxtFld, password, "login with " + password);
        return this;
    }

    public ProductsPage pressLoginBtn() {
        click(loginBtn, "press login button");
        return new ProductsPage();
    }

    public ProductsPage login(String username, String password) {
        enterUserName(username);
        enterPassword(password);
        return pressLoginBtn();
    }

    public String getErrTxt() {
        return getText(errTxt, "error text is - ");
    }
}
