package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.data.Card;
import ru.netology.data.SQLDB;
import ru.netology.pages.PaymentPage;
import ru.netology.pages.StartPage;
import ru.netology.data.DataGenerator;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataGenerator.*;

public class PaymentPageUIAndDbTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:8080");
        SQLDB.clearTables();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }



    @Test
    void happyPathPay() throws SQLException {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLDB.getPaymentStatus());
    }


    @Test
    void declinedPay() throws SQLException {
        Card card = new Card(getDeclinedNumber(), getAnyMonth(), getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkDeclineNotification();
        assertEquals("DECLINED", SQLDB.getPaymentStatus());
    }


   @Test
    void validMinimumBoundaryCardValues() throws SQLException {
        Card card = new Card(getApprovedNumber(), getCurrentMonth(), getCurrentYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLDB.getPaymentStatus());
    }


    @Test
    void validMaximumBoundaryCardValues() throws SQLException {
        Card card = new Card(getApprovedNumber(), getCurrentMonth(), getYearPlusFive(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLDB.getPaymentStatus());
    }



    @Test
    void shortNumber() {
        Card card = new Card(getShortCardNumber(), getAnyMonth(), getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidFormat();
    }


    @Test
    void invalidNumber() throws SQLException {
        Card card = new Card(getInvalidCardNumber(), getAnyMonth(), getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkDeclineNotification();
        assertEquals("DECLINED", SQLDB.getPaymentStatus());
    }



    @Test
    void invalidMinimumBoundaryCardValues() {
        Card card = new Card(getApprovedNumber(), getPastMonth(), "", name(), getCvc());
        if (getPastMonth().equals("12")) {
            card.setYear(DataGenerator.getPastYear());
        } else {
            card.setYear(DataGenerator.getCurrentYear());
        }
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidDate();
    }


    @Test
    void invalidMaximumBoundaryCardValues() {
        Card card = new Card(getApprovedNumber(), getNextMonth(),"", name(), getCvc());
        if (getNextMonth().equals("01")) {
            card.setYear(DataGenerator.getYearPlusSix());
        } else {
            card.setYear(DataGenerator.getYearPlusFive());
        }
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidDate();
    }


    @Test
    void invalidMonthMinimumValue() {
        Card card = new Card(getApprovedNumber(), "00", getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidDate();
    }


    @Test
   void invalidMonthMaximumValue() {
        Card card = new Card(getApprovedNumber(), "13", getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidDate();
    }


    @Test
    void InvalidPastYear() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getPastYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkExpiredDate();
    }


    @Test
    void invalidCardholderRus() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "ДМИТРИЙ УРИН", getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidFormat();
    }


    @Test
    void invalidCardholderDashed() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "DMITRY-URIN", getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidFormat();
    }


    @Test
    void invalidCardholderNameOnly() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "DMITRY", getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidFormat();
    }


    @Test
    void invalidCardholderSurnameOnly() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "URIN", getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidFormat();
    }


    @Test
    void InvalidCardholderDigitsOnly() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "99999", getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidDataName();
    }


    @Test
    void invalidCardholderShort() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "V", getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkShortName();
    }


    @Test
    void invalidCardholderLong() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), getLongName(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkLongName();
    }


    @Test
    void invalidCVCOneDigitOnly() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), name(), getOneDigitCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidFormat();
    }


    @Test
    void invalidCVCTwoDigitsOnly() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), name(), getTwoDigitCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidFormat();
    }


    @Test
    void invalidCVCTripleZero() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), name(),"000");
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkInvalidFormat();
    }


    @Test
    void invalidEmptyNumber() {
        Card card = new Card(null, getAnyMonth(), getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkRequiredField();
    }


    @Test
    void invalidEmptyMonth() {
        Card card = new Card(getApprovedNumber(), null, getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkRequiredField();
    }


    @Test
    void invalidEmptyYear() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), null, name(), getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkRequiredField();
    }


    @Test
   void invalidEmptyCardholder() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), null, getCvc());
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkRequiredField();
    }


    @Test
    void invalidCVCEmpty() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), name(), null);
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkRequiredField();
    }


   @Test
    void invalidAllFieldsEmpty() {
        Card card = new Card(null, null, null, null, null);
        val startPage = new StartPage();
        startPage.buy();
        val paymentPage = new PaymentPage();
        paymentPage.fulfillData(card);
        paymentPage.checkAllFieldsRequired();
    }

}


