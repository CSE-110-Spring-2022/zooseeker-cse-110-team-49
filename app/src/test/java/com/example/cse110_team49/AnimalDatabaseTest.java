package com.example.cse110_team49;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class AnimalDatabaseTest {
    private ExhibitDao dao;
    private ExhibitDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.exhibitDao();
    }

    @Test
    public void testInsert() {
        Exhibit item1 = new Exhibit("gorillas", "Gorillas");
        Exhibit item2 = new Exhibit("lions", "Lions");

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testDelete() {
        Exhibit item = new Exhibit("monkey", "Monkey");

        dao.insert(item);
        item = dao.get("Monkey");
        assertNotNull(item);

        dao.delete(item);
        item = dao.get("Monkey");

        assertNull(item);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
}
