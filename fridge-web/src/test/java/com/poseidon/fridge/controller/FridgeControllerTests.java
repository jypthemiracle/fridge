package com.poseidon.fridge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.poseidon.ControllerBase;
import com.poseidon.fridge.command.FridgeCommand;

public class FridgeControllerTests extends ControllerBase {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String FRIDGE_API_URL = "http://localhost:8081";
    
    @Override
    protected void setUp() {
        
    }
    
    @Test
    public void findCreateFridgeButtonAndClick() {
        browser.get(BASE_URL + "/fridges");
        WebElement createFridgeBtn = browser.findElementById("btnCreateFridge");
        assertThat(createFridgeBtn).isNotNull();
        
        createFridgeBtn.click();
        assertThat(browser.getCurrentUrl()).isEqualTo(BASE_URL + "/fridges/add");
    }
    
    @Test
    public void fillInCreateFormAndSubmit() {
        browser.get(BASE_URL + "/fridges/add");
        
        String nickname = "myFridge";
        WebElement nicknameElement = browser.findElement(By.name("nickname"));
        nicknameElement.sendKeys(nickname);
        browser.findElementByTagName("form").submit();
        
        WebDriverWait wait = new WebDriverWait(browser, 10);
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = browser.switchTo().alert();
        assertThat(alert.getText()).isEqualTo(nickname + "을 생성했습니다.");
        alert.accept();
    }
    
    @Test
    public void clickAnchorTagFromFridges() {
        FridgeCommand fridge = new FridgeCommand();
        fridge.setNickname("myFridge");
        fridge.setId(createFridge(fridge));
        
        browser.get(BASE_URL + "/fridges");
        
        String viewPageUrl = BASE_URL + "/fridges/" + fridge.getId();
        
        List<WebElement> anchors = browser.findElementsByLinkText(fridge.getNickname());
        assertThat(anchors).filteredOn(new Condition<WebElement>() {
            @Override
            public boolean matches(WebElement element) {
                return element.getAttribute("href").equals(viewPageUrl);
            }
        });
        
        WebElement anchorTag = anchors.stream()
                .filter(element -> element.getAttribute("href").equals(viewPageUrl))
                .findAny()
                .orElse(null);
        
        anchorTag.click();
        
        assertThat(browser.getCurrentUrl()).isEqualTo(viewPageUrl);
    }
    
    private Integer createFridge(FridgeCommand fridgeCommand) {
        String nickname = fridgeCommand.getNickname();
        ResponseEntity<FridgeCommand> response = restTemplate.postForEntity(FRIDGE_API_URL + "/fridges", nickname, FridgeCommand.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        FridgeCommand fridge = response.getBody();
        assertThat(fridge.getId()).isPositive();
        return fridge.getId();
    }

}
