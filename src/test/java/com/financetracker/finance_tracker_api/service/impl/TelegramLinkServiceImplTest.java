package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.dto.response.TelegramLinkCodeResponse;
import com.financetracker.finance_tracker_api.entity.TelegramLinkCode;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.exception.ResourceNotFoundException;
import com.financetracker.finance_tracker_api.repository.TelegramLinkCodeRepository;
import com.financetracker.finance_tracker_api.repository.UserRepository;
import com.financetracker.finance_tracker_api.service.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramLinkServiceImplTest {

    @Mock
    private TelegramLinkCodeRepository linkCodeRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TelegramLinkServiceImpl telegramLinkService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstName("Om")
                .lastName("Patil")
                .email("om@example.com")
                .password("encoded-password")
                .build();
        testUser.setId(UUID.randomUUID());
    }

    @Nested
    class CreateLinkCode {

        @Test
        void shouldDeleteExistingCodesAndCreateNewOne() {

            when(currentUserService.getCurrentUser()).thenReturn(testUser);
            when(linkCodeRepository.existsByCode(anyString())).thenReturn(false);
            when(linkCodeRepository.save(any(TelegramLinkCode.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            TelegramLinkCodeResponse response =
                    telegramLinkService.createLinkCode();

            assertNotNull(response.getCode());
            assertEquals(8, response.getCode().length());
            assertNotNull(response.getExpiresAt());
            assertTrue(response.getExpiresAt().isAfter(LocalDateTime.now()));

            verify(linkCodeRepository).deleteByUser(testUser);
            verify(linkCodeRepository).save(any(TelegramLinkCode.class));
        }

        @Test
        void shouldGenerateCodeWithOnlyValidCharacters() {

            when(currentUserService.getCurrentUser()).thenReturn(testUser);
            when(linkCodeRepository.existsByCode(anyString())).thenReturn(false);
            when(linkCodeRepository.save(any(TelegramLinkCode.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            TelegramLinkCodeResponse response =
                    telegramLinkService.createLinkCode();

            String validChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
            for (char c : response.getCode().toCharArray()) {
                assertTrue(validChars.indexOf(c) >= 0,
                        "Invalid character in code: " + c);
            }
        }

        @Test
        void shouldRetryWhenCodeAlreadyExists() {

            when(currentUserService.getCurrentUser()).thenReturn(testUser);
            // First call returns true (collision), second returns false
            when(linkCodeRepository.existsByCode(anyString()))
                    .thenReturn(true)
                    .thenReturn(false);
            when(linkCodeRepository.save(any(TelegramLinkCode.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            TelegramLinkCodeResponse response =
                    telegramLinkService.createLinkCode();

            assertNotNull(response.getCode());
            verify(linkCodeRepository, times(2))
                    .existsByCode(anyString());
        }

        @Test
        void shouldSaveCodeWithCorrectUserAndExpiry() {

            when(currentUserService.getCurrentUser()).thenReturn(testUser);
            when(linkCodeRepository.existsByCode(anyString())).thenReturn(false);
            when(linkCodeRepository.save(any(TelegramLinkCode.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            LocalDateTime before = LocalDateTime.now().plusMinutes(9);
            telegramLinkService.createLinkCode();
            LocalDateTime after = LocalDateTime.now().plusMinutes(11);

            ArgumentCaptor<TelegramLinkCode> captor =
                    ArgumentCaptor.forClass(TelegramLinkCode.class);
            verify(linkCodeRepository).save(captor.capture());

            TelegramLinkCode saved = captor.getValue();
            assertEquals(testUser, saved.getUser());
            assertTrue(saved.getExpiresAt().isAfter(before));
            assertTrue(saved.getExpiresAt().isBefore(after));
        }
    }

    @Nested
    class LinkChatId {

        @Test
        void shouldLinkChatIdSuccessfully() {

            String code = "A3B7K9X2";
            Long chatId = 123456789L;

            TelegramLinkCode linkCode = TelegramLinkCode.builder()
                    .code(code)
                    .expiresAt(LocalDateTime.now().plusMinutes(5))
                    .user(testUser)
                    .build();

            when(linkCodeRepository.findByCode(code))
                    .thenReturn(Optional.of(linkCode));
            when(userRepository.findByTelegramChatId(chatId))
                    .thenReturn(Optional.empty());

            telegramLinkService.linkChatId(code, chatId);

            assertEquals(chatId, testUser.getTelegramChatId());
            verify(userRepository).save(testUser);
            verify(linkCodeRepository).delete(linkCode);
        }

        @Test
        void shouldNormalizeLowercaseCode() {

            String code = "a3b7k9x2";
            Long chatId = 123456789L;

            TelegramLinkCode linkCode = TelegramLinkCode.builder()
                    .code("A3B7K9X2")
                    .expiresAt(LocalDateTime.now().plusMinutes(5))
                    .user(testUser)
                    .build();

            when(linkCodeRepository.findByCode("A3B7K9X2"))
                    .thenReturn(Optional.of(linkCode));
            when(userRepository.findByTelegramChatId(chatId))
                    .thenReturn(Optional.empty());

            telegramLinkService.linkChatId(code, chatId);

            assertEquals(chatId, testUser.getTelegramChatId());
            verify(userRepository).save(testUser);
        }

        @Test
        void shouldThrowWhenCodeIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> telegramLinkService.linkChatId(null, 123L));

            verifyNoInteractions(linkCodeRepository);
        }

        @Test
        void shouldThrowWhenCodeIsBlank() {

            assertThrows(IllegalArgumentException.class,
                    () -> telegramLinkService.linkChatId("   ", 123L));

            verifyNoInteractions(linkCodeRepository);
        }

        @Test
        void shouldThrowWhenCodeNotFound() {

            when(linkCodeRepository.findByCode("NOTFOUND"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> telegramLinkService.linkChatId("NOTFOUND", 123L));

            verify(userRepository, never()).save(any());
        }

        @Test
        void shouldThrowAndDeleteWhenCodeExpired() {

            String code = "EXPIRED1";
            TelegramLinkCode linkCode = TelegramLinkCode.builder()
                    .code(code)
                    .expiresAt(LocalDateTime.now().minusMinutes(1))
                    .user(testUser)
                    .build();

            when(linkCodeRepository.findByCode(code))
                    .thenReturn(Optional.of(linkCode));

            assertThrows(IllegalArgumentException.class,
                    () -> telegramLinkService.linkChatId(code, 123L));

            verify(linkCodeRepository).delete(linkCode);
            verify(userRepository, never()).save(any());
        }

        @Test
        void shouldThrowWhenChatIdAlreadyLinkedToAnotherUser() {

            String code = "VALID123";
            Long chatId = 999L;

            User otherUser = User.builder()
                    .firstName("Other")
                    .lastName("User")
                    .email("other@example.com")
                    .password("encoded")
                    .build();
            otherUser.setId(UUID.randomUUID());

            TelegramLinkCode linkCode = TelegramLinkCode.builder()
                    .code(code)
                    .expiresAt(LocalDateTime.now().plusMinutes(5))
                    .user(testUser)
                    .build();

            when(linkCodeRepository.findByCode(code))
                    .thenReturn(Optional.of(linkCode));
            when(userRepository.findByTelegramChatId(chatId))
                    .thenReturn(Optional.of(otherUser));

            assertThrows(IllegalArgumentException.class,
                    () -> telegramLinkService.linkChatId(code, chatId));

            verify(userRepository, never()).save(any());
        }

        @Test
        void shouldAllowRelinkingSameChatIdToSameUser() {

            String code = "RELINK12";
            Long chatId = 555L;

            TelegramLinkCode linkCode = TelegramLinkCode.builder()
                    .code(code)
                    .expiresAt(LocalDateTime.now().plusMinutes(5))
                    .user(testUser)
                    .build();

            // Same user already has this chatId — should NOT throw
            when(linkCodeRepository.findByCode(code))
                    .thenReturn(Optional.of(linkCode));
            when(userRepository.findByTelegramChatId(chatId))
                    .thenReturn(Optional.of(testUser));

            telegramLinkService.linkChatId(code, chatId);

            assertEquals(chatId, testUser.getTelegramChatId());
            verify(userRepository).save(testUser);
            verify(linkCodeRepository).delete(linkCode);
        }
    }
}
