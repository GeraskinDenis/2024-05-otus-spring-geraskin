package ru.otus.hw.service;

public interface LocalizedIOService extends LocalizedMessagesService, IOService {

    void printLineLocalized(String code);

    void printFormattedLineLocalized(String code, Object... args);

    int readIntForRangeLocalized(int min, int max, String invalidInputFormatMessage,
                                 String errorMessageCode);

    String readStringWithPromptLocalized(String promptCode);

    int readIntForRangeWithPromptLocalized(int min, int max,
                                           String promptCode,
                                           String invalidInputFormatMessage,
                                           String errorMessageCode);
}
