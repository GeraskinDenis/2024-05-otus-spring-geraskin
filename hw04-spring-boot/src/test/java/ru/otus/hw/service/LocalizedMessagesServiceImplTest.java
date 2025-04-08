package ru.otus.hw.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.LocaleConfig;

import java.util.Locale;

@SpringBootTest(classes = LocalizedMessagesServiceImpl.class)
@ImportAutoConfiguration(MessageSourceAutoConfiguration.class)
@DisplayName("Should have get message properly")
public class LocalizedMessagesServiceImplTest {

    @MockitoBean
    private LocaleConfig localeConfig;

    @Autowired
    private LocalizedMessagesServiceImpl messagesService;

    @Test
    @DisplayName("en-US")
    public void shouldHaveGetMessageProperlyEn() {
        Mockito.when(localeConfig.getLocale()).thenReturn(Locale.forLanguageTag("en-US"));
        Assertions.assertThat(messagesService.getMessage("TestService.answer.the.questions"))
                .isEqualTo("Please answer the questions below:");
    }

    @Test
    @DisplayName("ru-RU")
    public void shouldHaveGetMessageProperlyRu() {
        Mockito.when(localeConfig.getLocale()).thenReturn(Locale.forLanguageTag("ru-RU"));
        Assertions.assertThat(messagesService.getMessage("TestService.answer.the.questions"))
                .isEqualTo("\u041F\u043E\u0436\u0430\u043B\u0443\u0439\u0441\u0442\u0430, \u043E\u0442\u0432\u0435\u0442\u044C\u0442\u0435 \u043D\u0430 \u0432\u043E\u043F\u0440\u043E\u0441\u044B \u043D\u0438\u0436\u0435");
    }

}
