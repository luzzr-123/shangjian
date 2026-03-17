package com.luuzr.jielv

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TodayOverviewSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun quickCreateFabOpensAllCreateScreensFromToday() {
        assertTagExists("today_summary_card")
        composeRule.onNodeWithTag("today_quick_create_main").performClick()

        composeRule.onNodeWithTag("today_quick_create_task").performClick()
        assertTagExists("task_editor_title_input")
        composeRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()
        }

        composeRule.onNodeWithTag("today_quick_create_main").performClick()
        composeRule.onNodeWithTag("today_quick_create_habit").performClick()
        assertTagExists("habit_editor_title_input")
        composeRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()
        }
        assertTagNotExists("today_quick_create_note")
    }

    private fun assertTagExists(tag: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
        assertTrue(composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty())
    }

    private fun assertTagNotExists(tag: String) {
        composeRule.waitForIdle()
        assertTrue(composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isEmpty())
    }
}
