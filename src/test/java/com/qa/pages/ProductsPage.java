package com.qa.pages;

import com.qa.BaseTest;
import com.qa.utils.TestUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;


public class ProductsPage extends MenuPage {
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Toggle\"]/preceding-sibling::android.widget.TextView")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Toggle\"]/parent::*[1]/preceding-sibling::*[1]")
    WebElement productTitleTxt;

    @AndroidFindBy(xpath = "(//android.widget.TextView[@content-desc=\"test-Item title\" ])[1]")
    @iOSXCUITFindBy(xpath = "(//XCUIElementTypeStaticText[@name=\"test-Item title\"])[1]")
    WebElement SLBTitle;

    @AndroidFindBy(xpath = "(//android.widget.TextView[@content-desc=\"test-Price\" ])[1]")
    @iOSXCUITFindBy(xpath = "(//XCUIElementTypeStaticText[@name=\"test-Price\"])[1]")
    WebElement SLBPrice;

    public ProductsPage() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    public String getTitle() {
       return getText(productTitleTxt, "product page title is - ");
    }

    public String getSLBTitle() {
        return getText(SLBTitle, "title is - ");
    }

    public String getSLBPrice() {
         return getText(SLBPrice, "price is - ");
    }

    public ProductDetailsPage pressSLBTitle(){
        click(SLBTitle, "press SLB Title");
        return new ProductDetailsPage();
    }
}
