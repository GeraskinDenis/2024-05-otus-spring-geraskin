package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LocalizedIOServiceImpl implements LocalizedIOService {

    private final LocalizedMessagesService localizedMessagesService;

    private final IOService ioService;

    @Override
    public String getMessage(String code, Object... args) {
        return localizedMessagesService.getMessage(code, args);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        ioService.printFormattedLine(s, args);
    }

    @Override
    public void printFormattedLineLocalized(String code, Object... args) {
        ioService.printLine(localizedMessagesService.getMessage(code, args));
    }

    @Override
    public void printLine(String s) {
        ioService.printLine(s);
    }

    @Override
    public void printLineLocalized(String code) {
        ioService.printLine(localizedMessagesService.getMessage(code));
    }

    @Override
    public int readIntForRange(int min,
                               int max,
                               String invalidInputFormatMessage,
                               String errorMessage) {
        return ioService.readIntForRange(min,
                max,
                invalidInputFormatMessage,
                errorMessage);
    }

    @Override
    public int readIntForRangeLocalized(int min, int max,
                                        String invalidInputFormatMessage,
                                        String errorMessageCode) {
        return ioService.readIntForRange(min, max, invalidInputFormatMessage,
                localizedMessagesService.getMessage(errorMessageCode));
    }

    @Override
    public int readIntForRangeWithPrompt(int min, int max,
                                         String prompt,
                                         String invalidInputFormatMessage,
                                         String errorMessage) {
        return ioService.readIntForRangeWithPrompt(min, max, prompt, invalidInputFormatMessage, errorMessage);
    }

    @Override
    public String readString() {
        return ioService.readString();
    }

    @Override
    public String readStringWithPrompt(String prompt) {
        return ioService.readStringWithPrompt(prompt);
    }

    @Override
    public String readStringWithPromptLocalized(String promptCode) {
        return ioService.readStringWithPrompt(localizedMessagesService.getMessage(promptCode));
    }

    @Override
    public int readIntForRangeWithPromptLocalized(int min, int max,
                                                  String promptCode,
                                                  String invalidInputFormatMessage,
                                                  String errorMessageCode) {
        return ioService.readIntForRangeWithPrompt(min, max,
                localizedMessagesService.getMessage(promptCode),
                localizedMessagesService.getMessage(invalidInputFormatMessage),
                localizedMessagesService.getMessage(errorMessageCode)
        );
    }
}
