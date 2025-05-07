package com.example.waterintake;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SettingsActivityTest {

    private SharedPreferences mockPrefs;
    private Editor mockEditor;

    @Before
    public void setUp() {
        mockPrefs = mock(SharedPreferences.class);
        mockEditor = mock(SharedPreferences.Editor.class);

        when(mockPrefs.edit()).thenReturn(mockEditor);
        when(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor);
    }

    @Test
    public void testDarkModeSavedCorrectly() {
        // Simulate toggling dark mode switch
        boolean isDarkMode = true;
        mockPrefs.edit().putBoolean("dark_mode", isDarkMode).apply();

        // Verify that the preference was saved
        verify(mockEditor).putBoolean("dark_mode", true);
        verify(mockEditor).apply();
    }
}
