package com.qa.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class ProductDetailsPage extends MenuPage {

    @AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"test-Description\"]/child::android.widget.TextView)[1]")
//    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Description\"]/child::*[1]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Description\"]/child::XCUIElementTypeStaticText[1]")
    WebElement SLBTitle;

    @AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"test-Description\"]/child::android.widget.TextView)[2]")
//    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Description\"]/child::*[2]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Description\"]/child::XCUIElementTypeStaticText[2]")
    WebElement SLBTxt;

    @AndroidFindBy(accessibility = "test-BACK TO PRODUCTS")
    @iOSXCUITFindBy(id = "test-BACK TO PRODUCTS")
    WebElement backToProductsBtn;

    public ProductDetailsPage() {
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    public String getSLBTitle() {
        String title = getText(SLBTitle);
        System.out.println("title is - " + title);
        return title;
    }

    public String getSLBTxt() {
        String txt = getText(SLBTxt);
        System.out.println("txt is - " + txt);
        return txt;
    }

    public ProductsPage pressBackToProductsBtn(){
        System.out.println("navigate back to products page");
        click(backToProductsBtn);
        return new ProductsPage();
    }

}
