package com.example.espresso;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.todolist.MainActivity;
import com.example.todolist.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void displaysHeaderCorrectly() {
        // checking that filter button is fully to the left of search bar
        onView(withId(R.id.searchView)).check(matches(isDisplayed()));
        onView(withId(R.id.button)).check(matches(isDisplayed()));

        onView(withId(R.id.searchView)).check(isCompletelyLeftOf(withId(R.id.button)));
    }

    @Test
    public void displaysFooterCorrectly() {
        // checking that submit button is fully to the left of input field
        onView(withId(R.id.inputToDo)).check(matches(isDisplayed()));
        onView(withId(R.id.submitButton)).check(matches(isDisplayed()));

        onView(withId(R.id.inputToDo)).check(isCompletelyLeftOf(withId(R.id.submitButton)));
    }

    @Test
    public void snackBarDisplayedOnEmptySubmit() {
        // a snack-bar with the text: "Please enter a task name" should be displayed if the user tries to enter an empty string
        onView(withId(R.id.inputToDo)).perform(ViewActions.typeText(""));
        onView(withId(R.id.submitButton)).perform(ViewActions.click());
        onView(withText("Please enter a task name"))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void resetInputAfterValidSubmit() {
        // the field for user input should be empty after a successful submission
        onView(withId(R.id.inputToDo)).perform(ViewActions.typeText("My task"));
        onView(withId(R.id.submitButton)).perform(ViewActions.click());
        onView(withId(R.id.inputToDo)).check(ViewAssertions.matches(withText("")));
    }
}
