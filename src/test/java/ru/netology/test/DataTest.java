package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static java.time.Duration.ofSeconds;

import static ru.netology.data.DataGenerator.*;

class DataTest {

    @BeforeAll
    @DisplayName("Включение логера перед тестами")
    public static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }
    @BeforeEach
    @DisplayName("Открытие браузера перед выполнением теста")
    void setup() {
        open("http://localhost:9999");
    }

    @AfterAll
    @DisplayName("Выключение логера после выполнения тестов")
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Should successful plan and replay meeting")
    void successfulPlanAndReplayMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 5;
        var firstMeetingDate = generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 9;
        var secondMeetingDate = generateDate(daysToAddForSecondMeeting);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT,Keys.HOME),Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $("[data-test-id='success-notification']  .notification__title").shouldBe(visible, ofSeconds(5)).shouldHave(exactText("Успешно!"));
        $("[data-test-id='success-notification']  .notification__content").shouldBe(visible, ofSeconds(5)).shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate));
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT,Keys.HOME),Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(secondMeetingDate);
        $$("button").find(exactText("Запланировать")).click();
        $("[data-test-id='replan-notification']  .notification__title").shouldBe(visible, ofSeconds(5)).shouldHave(exactText("Необходимо подтверждение"));
        $("[data-test-id='replan-notification']  .notification__content").shouldBe(visible, ofSeconds(5)).shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $$("[data-test-id='replan-notification'] button").find(exactText("Перепланировать")).click();
        $("[data-test-id='success-notification']  .notification__title").shouldBe(visible, ofSeconds(5)).shouldHave(exactText("Успешно!"));
        $("[data-test-id='success-notification']  .notification__content").shouldBe(visible, ofSeconds(5)).shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate));
    }

    @Test
    @DisplayName("Should if you don't enter a date")
    public void ifYouDontEnterDate() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAdd = 3;
        var meetingDate = generateDate(daysToAdd);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $("[data-test-id='success-notification'").should(visible, ofSeconds(15))
                .shouldHave(text("Встреча успешно запланирована на " + meetingDate));
    }
}