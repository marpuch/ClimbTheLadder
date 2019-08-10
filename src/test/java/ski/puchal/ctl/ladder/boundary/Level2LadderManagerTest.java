package ski.puchal.ctl.ladder.boundary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import ski.puchal.ctl.ladder.entity.AccumulatedLevel2LaddersBean;

public class Level2LadderManagerTest {

    private Level2LadderManager manager;

    @Before
    public void init() {
        manager = new Level2LadderManager();
        manager.setTimeToGenerateLadderMilisec(300000);
    }

    @Test
    public void generate() {
        // when
        final AccumulatedLevel2LaddersBean bean = manager.generate();

        // then
        assertEquals(0, bean.getLadderCount());
        assertTrue(System.currentTimeMillis() - bean.getTimestamp() < 1000);
    }

    @Test
    public void validate() {
        // given
        final AccumulatedLevel2LaddersBean bean = manager.generate();

        // when
        manager.validate(bean);

        // then
        // ok if no exception is thrown
    }

    @Test(expected = LadderException.class)
    public void failValidationTimestampInFuture() {
        // given
        final AccumulatedLevel2LaddersBean bean =
                new AccumulatedLevel2LaddersBean(10, System.currentTimeMillis() + 10000);

        // when
        try {
            manager.validate(bean);
            fail();
        } catch (final LadderException e) {
            // then
            assertEquals("Ladder timestamp lies in the future?!?!", e.getMessage());
            throw e;
        }
    }


    @Test(expected = LadderException.class)
    public void failValidationTimestampInPast() {
        // given
        final AccumulatedLevel2LaddersBean bean =
                new AccumulatedLevel2LaddersBean(10, System.currentTimeMillis() - 10000);

        // when
        try {
            manager.validate(bean);
            fail();
        } catch (final LadderException e) {
            // then
            assertEquals("To old ladder definition. Please refresh to get the newest one.", e.getMessage());
            throw e;
        }
    }

    @Test
    public void serialize() {
        // given
        final AccumulatedLevel2LaddersBean bean =
                new AccumulatedLevel2LaddersBean(10, 1000);

        // when
        final String s = manager.serialize(bean);

        // then
        assertEquals("eyJ0aW1lc3RhbXAiOjEwMDAsImxhZGRlckNvdW50IjoxMH0=", s);
    }

    @Test
    public void deserialize() {
        // given
        final String s = "eyJ0aW1lc3RhbXAiOjEwMDAsImxhZGRlckNvdW50IjoxMH0=";

        // when
        final AccumulatedLevel2LaddersBean bean = manager.deserialize(s);

        // then
        assertEquals(10, bean.getLadderCount());
        assertEquals(1000, bean.getTimestamp());
    }

    @Test
    public void deserializingError() {
        // given
        final String s = "ezJ0aW1lc3RhbXAiOjEwMDAsImxhZGRlckNvdW50IjoxMH0=";

        // when
        try {
            final AccumulatedLevel2LaddersBean bean = manager.deserialize(s);
        } catch (final LadderException e) {
            // then
            assertEquals("Don't do malicious things with the token please!", e.getMessage());
        }
    }
}