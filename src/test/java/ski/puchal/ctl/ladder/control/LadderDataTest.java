package ski.puchal.ctl.ladder.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import ski.puchal.ctl.ladder.boundary.LadderException;
import ski.puchal.ctl.ladder.boundary.Level;
import ski.puchal.ctl.ladder.boundary.ListItemBean;
import ski.puchal.ctl.ladder.boundary.ResultBean;

public class LadderDataTest {

    @Test
    public void emptyAfterInit() {
        // given
        final LadderData manager = new LadderData();

        // when
        final ResultBean result = manager.getTopPlayers(null);

        // then
        assertEquals(0, result.getShortList().size());
        assertNull(result.getErrorMessage());
        assertEquals(-1, result.getHighlightIndex());
    }

    @Test
    public void addLadderOnce() {
        // given
        final LadderData manager = new LadderData();

        // when
        manager.addLadderLevel1("user1", 12345);
        final ResultBean result = manager.getTopPlayers(null);

        // then
        assertEquals(1, result.getShortList().size());
        final ListItemBean bean = result.getShortList().get(0);
        assertEquals("user1", bean.getName());
        assertEquals(1, bean.getLadderCount());
        assertEquals(Level.LEVEL1, bean.getLevel());
        assertEquals(1, bean.getPosition());
        assertEquals(12345, bean.getTimestamp());
        assertNull(result.getErrorMessage());
        assertEquals(-1, result.getHighlightIndex());
    }

    @Test
    public void addLadderTwice() {
        // given
        final LadderData manager = new LadderData();

        // when
        manager.addLadderLevel1("user1", 12345);
        manager.addLadderLevel1("user1", 12345000);
        final ResultBean result = manager.getTopPlayers(null);

        // then
        assertEquals(1, result.getShortList().size());
        final ListItemBean bean = result.getShortList().get(0);
        assertEquals("user1", bean.getName());
        assertEquals(2, bean.getLadderCount());
        assertEquals(Level.LEVEL1, bean.getLevel());
        assertEquals(1, bean.getPosition());
        assertEquals(12345000, bean.getTimestamp());
        assertNull(result.getErrorMessage());
        assertEquals(-1, result.getHighlightIndex());
    }

    @Test
    public void addLadderTwiceTooQuick() {
        // given
        final LadderData manager = new LadderData();

        // when
        manager.addLadderLevel1("user1", 0);
        try {
            manager.addLadderLevel1("user1", 3540000);
            fail("Exception expected");
        } catch (final LadderException e) {
            // then
            assertEquals("You can add a ladder again after 1 minutes", e.getMessage());
        }
    }

    // TODO more tests to come
}