package com.example.todolist;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EditToDoUnitTest {

    @Test
    public void abbreviatesLongString() {
        assertEquals(EditToDo.abbreviateIfTooLong("ThisIsAStringThatIs35CharactersLong"),
                "ThisIsAStringThatIs35Char...");
        // edge case
        assertEquals(EditToDo.abbreviateIfTooLong("HereAre26CharactersExactly"),
                "HereAre26CharactersExactl...");
    }

    @Test
    public void doesNotAbbreviateShortString() {
        assertEquals(EditToDo.abbreviateIfTooLong("1"),
                "1");
        // edge case
        assertEquals(EditToDo.abbreviateIfTooLong("HereIs25CharactersExactly"),
                "HereIs25CharactersExactly");
    }
}