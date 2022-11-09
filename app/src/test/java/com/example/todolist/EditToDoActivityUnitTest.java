package com.example.todolist;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EditToDoActivityUnitTest {

    @Test
    public void abbreviatesLongString() {
        assertEquals(EditToDoActivity.abbreviateIfTooLong("ThisIsAStringThatIs35CharactersLong"),
                "ThisIsAStringThatIs35Char...");
        // edge case
        assertEquals(EditToDoActivity.abbreviateIfTooLong("HereAre26CharactersExactly"),
                "HereAre26CharactersExactl...");
    }

    @Test
    public void doesNotAbbreviateShortString() {
        assertEquals(EditToDoActivity.abbreviateIfTooLong("1"),
                "1");
        // edge case
        assertEquals(EditToDoActivity.abbreviateIfTooLong("HereIs25CharactersExactly"),
                "HereIs25CharactersExactly");
    }
}