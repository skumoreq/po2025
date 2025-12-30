import org.junit.Test;

import static org.junit.Assert.*;

public class CodingBatTest {

    @Test
    public void startOz() {
        assertEquals("oz", CodingBat.startOz("ozymandias"));
        assertEquals("z", CodingBat.startOz("bzoo"));
        assertEquals("o", CodingBat.startOz("oxx"));
        assertEquals("oz", CodingBat.startOz("oz"));
    }

    @Test
    public void diff21() {
        assertEquals(2, CodingBat.diff21(19));
        assertEquals(11, CodingBat.diff21(10));
        assertEquals(0, CodingBat.diff21(21));
        assertEquals(2, CodingBat.diff21(22));
    }

    @Test
    public void sum67() {
        assertEquals(5, CodingBat.sum67(new int[]{1, 2, 2}));
        assertEquals(5, CodingBat.sum67(new int[]{1, 2, 2, 6, 99, 99, 7}));
        assertEquals(4, CodingBat.sum67(new int[]{1, 1, 6, 7, 2}));
        assertEquals(2, CodingBat.sum67(new int[]{1, 6, 2, 2, 7, 1, 6, 99, 99, 7}));
    }

    @Test
    public void middleTwo() {
        assertEquals("ri", CodingBat.middleTwo("string"));
        assertEquals("od", CodingBat.middleTwo("code"));
        assertEquals("ct", CodingBat.middleTwo("Practice"));
        assertEquals("ab", CodingBat.middleTwo("ab"));
    }
}
