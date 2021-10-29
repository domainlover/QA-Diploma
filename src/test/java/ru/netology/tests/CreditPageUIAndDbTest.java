package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.data.Card;
import ru.netology.data.SQLDB;
import ru.netology.pages.CreditPage;
import ru.netology.pages.StartPage;
import ru.netology.data.DataGenerator;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataGenerator.*;

public class CreditPageUIAndDbTest {
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
    void happyPathPayCredit() throws SQLException {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLDB.getCreditStatus());
    }


    @Test
    void declinedPayCredit() throws SQLException {
        Card card = new Card(getDeclinedNumber(), getAnyMonth(), getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkDeclineNotification();
        assertEquals("DECLINED", SQLDB.getCreditStatus());
    }


    @Test
    void validMinimumBoundaryCreditValues() throws SQLException {
        Card card = new Card(getApprovedNumber(), getCurrentMonth(), getCurrentYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLDB.getCreditStatus());
    }


    @Test
    void validMaximumBoundaryCreditValues() throws SQLException{
        Card card = new Card(getApprovedNumber(), getCurrentMonth(), getYearPlusFive(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLDB.getCreditStatus());
    }



    @Test
    void shortNumberCredit() {
        Card card = new Card(getShortCardNumber(), getAnyMonth(), getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidFormat();
    }


    @Test
    void invalidNumberCredit() throws SQLException {
        Card card = new Card(getInvalidCardNumber(), getAnyMonth(), getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkDeclineNotification();
        assertEquals("DECLINED", SQLDB.getPaymentStatus());
    }


    @Test
    void invalidMinimumBoundaryCreditValues() {
        Card card = new Card(getApprovedNumber(), getPastMonth(), "", name(), getCvc());
        if (getPastMonth().equals("12")) {
            card.setYear(DataGenerator.getPastYear());
        } else {
            card.setYear(DataGenerator.getCurrentYear());
        }
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidDate();
    }


    @Test
    void invalidMaximumBoundaryCreditValues() {
        Card card = new Card(getApprovedNumber(), getNextMonth(),"", name(), getCvc());
        if (getNextMonth().equals("01")) {
            card.setYear(DataGenerator.getYearPlusSix());
        } else {
            card.setYear(DataGenerator.getYearPlusFive());
        }
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidDate();
    }


    @Test
    void invalidMonthMinimumValueCredit() {
        Card card = new Card(getApprovedNumber(), "00", getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidDate();
    }


    @Test
    void invalidMonthMaximumValueCredit() {
        Card card = new Card(getApprovedNumber(), "13", getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidDate();
    }


    @Test
    void InvalidPastYearCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getPastYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkExpiredDate();
    }


    @Test
    void invalidCardholderRusCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "ДМИТРИЙ УРИН", getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidFormat();
    }


    @Test
    void invalidCardholderDashedCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "DMITRY-URIN", getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidFormat();
    }


    @Test
    void invalidCardholderNameOnlyCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "DMITRY", getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidFormat();
    }


    @Test
    void invalidCardholderSurnameOnlyCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "URIN", getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidFormat();
    }


    @Test
    void InvalidCardholderDigitsOnlyCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "99999", getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidDataName();
    }


    @Test
    void invalidCardholderShortCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), "V", getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkShortName();
    }


    @Test
    void invalidCardholderLongCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), getLongName(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkLongName();
    }


    @Test
    void invalidCVCOneDigitOnlyCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), name(), getOneDigitCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidFormat();
    }


    @Test
    void invalidCVCTwoDigitsOnlyCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), name(), getTwoDigitCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidFormat();
    }


    @Test
    void invalidCVCTripleZeroCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), name(),"000");
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkInvalidFormat();
    }


    @Test
    void invalidEmptyNumberCredit() {
        Card card = new Card(null, getAnyMonth(), getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkRequiredField();
    }


    @Test
    void invalidEmptyMonthCredit() {
        Card card = new Card(getApprovedNumber(), null, getValidYear(), name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkRequiredField();
    }


    @Test
    void invalidEmptyYearCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), null, name(), getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkRequiredField();
    }


    @Test
    void invalidEmptyCardholderCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), null, getCvc());
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkRequiredField();
    }


    @Test
    void invalidCVCEmptyCredit() {
        Card card = new Card(getApprovedNumber(), getAnyMonth(), getValidYear(), name(), null);
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkRequiredField();
    }


    @Test
    void invalidAllFieldsEmptyCredit() {
        Card card = new Card(null, null, null, null, null);
        val startPage = new StartPage();
        startPage.buyInCredit();
        val creditPage = new CreditPage();
        creditPage.fulfillData(card);
        creditPage.checkAllFieldsRequired();
    }

}
