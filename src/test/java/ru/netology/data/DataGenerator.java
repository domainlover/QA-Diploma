package ru.netology.data;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {
    public static Faker faker = new Faker(new Locale("en"));

    public static String getApprovedNumber() {
        return "4444 4444 4444 4441";
    }

    public static String getDeclinedNumber() {
        return "4444 4444 4444 4442";
    }

    public static String getInvalidCardNumber() {
        return "4444 4444 4444 4445";
    }

    public static String getShortCardNumber() {
        return "4444 4444 4444 444";
    }


    public static String getCurrentMonth() {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        return String.format("%02d", month);
    }

    public static String getPastMonth() {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue() - 1;
        return String.format("%02d", month);
    }

    public static String getAnyMonth() {
        Random random = new Random();
        int month = random.nextInt(12) + 1;
        return String.format("%02d", month);
    }

    public static String getNextMonth() {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue() + 1;
        return String.format("%02d", month);
    }

    public static String getCurrentYear() {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear() - 2000;
        return String.format("%02d", year);
    }

    public static String getPastYear() {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear() - 1;
        return String.format("%02d", year);
    }

    public static String getValidYear() {
        Random random = new Random();
        int y = random.nextInt(5) + 1;
        LocalDate futureYear = LocalDate.now().plusYears(y);
        int year = futureYear.getYear() - 2000;
        return String.format("%02d", year);
    }

    public static String getYearPlusFive() {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear() - 2000 + 5;
        return String.format("%02d", year);
    }

    public static String getYearPlusSix() {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear() - 2000 + 5 + 1;
        return String.format("%02d", year);
    }

    public static String getCvc() {
        FakeValuesService fake = new FakeValuesService(
                new Locale("en"), new RandomService());
        return fake.regexify("[1-999]{3}");
    }

    public static String getOneDigitCvc() {
        FakeValuesService fake = new FakeValuesService(
                new Locale("en"), new RandomService());
        return fake.regexify("[0-9]{1}");
    }

    public static String getTwoDigitCvc() {
        FakeValuesService fake = new FakeValuesService(
                new Locale("en"), new RandomService());
        return fake.regexify("[0-99]{2}");
    }

    public static String name() {
        return faker.name().firstName().toUpperCase() + " " + faker.name().lastName().toUpperCase();
    }

    public static String getLongName() {
        FakeValuesService fake = new FakeValuesService(
                new Locale("en"), new RandomService());
        return fake.regexify("[a-z]{27}");
    }

}

