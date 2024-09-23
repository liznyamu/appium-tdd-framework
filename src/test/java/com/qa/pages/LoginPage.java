package com.qa.pages;

import com.qa.BaseTest;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BaseTest {
    @AndroidFindBy(accessibility = "test-Username")
    WebElement usernameTxtFld;
    @AndroidFindBy(accessibility = "test-Password")
    WebElement passwordTxtFld;
    @AndroidFindBy(accessibility = "test-LOGIN")
    WebElement loginBtn;
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView")
    WebElement errTxt;

    public LoginPage(){
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    public LoginPage enterUserName(String username) {
        sendKeys(usernameTxtFld, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        sendKeys(passwordTxtFld, password);
        return this;
    }

    public ProductsPage pressLoginBtn() {
        click(loginBtn);
        return new ProductsPage();
    }

    public String getErrTxt(){
        return getAttribute(errTxt, "text");
    }
}
