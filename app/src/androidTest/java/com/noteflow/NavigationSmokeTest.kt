package com.luuzr.jielv

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NavigationSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun switchesAcrossAllTopLevelTabs() {
        assertTagExists("today_summary_card")

        composeRule.onNodeWithTag("nav_tasks").performClick()
        assertTagExists("show_completed_switch")

        composeRule.onNodeWithTag("nav_habits").performClick()
        assertTagExists("show_today_habits_switch")

        composeRule.onNodeWithTag("nav_notes").performClick()
        assertTagExists("notes_fab")

        composeRule.onNodeWithTag("nav_today").performClick()
        assertTagExists("today_summary_card")
    }

    private fun assertTagExists(tag: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
        assertTrue(composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty())
    }
}

