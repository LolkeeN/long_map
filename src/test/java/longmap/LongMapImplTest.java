package longmap;

import de.comparus.opensource.longmap.LongMap;
import de.comparus.opensource.longmap.LongMapImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class LongMapImplTest {

    private LongMap<String> longMap = new LongMapImpl<>(String.class);

    @After
    public void clearLongMap() {
        longMap = new LongMapImpl<>(String.class);
    }

    @Test
    public void getShouldReturnValueWithProperKey() {
        //GIVEN
        //WHEN
        longMap.put(1L, "asd");
        //THEN
        Assert.assertEquals("asd", longMap.get(1L));
    }

    @Test
    public void putShouldIncreaseMapSizeAndAddElementToMap() {
        //GIVEN
        long sizeBefore = longMap.size();
        //WHEN
        longMap.put(1L, "qwe");
        //THEN
        Assert.assertEquals(1, longMap.size() - sizeBefore);
        Assert.assertEquals("qwe", longMap.get(1L));
    }

    @Test
    public void removeShouldDecreaseMapSizeAndRemoveElementFromMap() {
        //GIVEN
        longMap.put(1L, "zxc");
        long sizeBefore = longMap.size();
        //WHEN
        longMap.remove(1L);
        //THEN
        Assert.assertEquals(1, sizeBefore - longMap.size());
    }
    @Test
    public void isEmptyShouldReturnTrueIfMapIsEmpty(){
        //GIVEN
        //WHEN
        //THEN
        Assert.assertTrue(longMap.isEmpty());
    }
    @Test
    public void isEmptyShouldReturnFalseIfMapIsNotEmpty(){
        //GIVEN
        longMap.put(1L, "rty");
        //WHEN
        //THEN
        Assert.assertFalse(longMap.isEmpty());
    }
    @Test
    public void containsKeyShouldReturnTrueIfMapContainsKey(){
        //GIVEN
        longMap.put(1L, "fgh");
        //WHEN
        //THEN
        Assert.assertTrue(longMap.containsKey(1L));
    }
    @Test
    public void containsKeyShouldReturnFalseIfMapNotContainsKey(){
        //GIVEN
        longMap.put(1L, "vbn");
        //WHEN
        //THEN
        Assert.assertFalse(longMap.containsKey(2L));
    }
    @Test
    public void containsValueShouldReturnTrueIfMapContainsValue(){
        //GIVEN
        longMap.put(1L, "fgh");
        //WHEN
        //THEN
        Assert.assertTrue(longMap.containsValue("fgh"));
    }
    @Test
    public void containsValueShouldReturnFalseIfMapNotContainsValue(){
        //GIVEN
        longMap.put(1L, "vbn");
        //WHEN
        //THEN
        Assert.assertFalse(longMap.containsValue("fgh"));
    }
    @Test
    public void keysShouldReturnProperKeyArray(){
        //GIVEN
        longMap.put(1L, "qwe");
        longMap.put(2L, "asd");
        longMap.put(3L, "zxc");
        //WHEN
        long[] keys = longMap.keys();
        //THEN
        Assert.assertTrue(Arrays.stream(keys)
                .anyMatch(x -> x == 1));
        Assert.assertTrue(Arrays.stream(keys)
                .anyMatch(x -> x == 2));
        Assert.assertTrue(Arrays.stream(keys)
                .anyMatch(x -> x == 3));
        Assert.assertFalse(Arrays.stream(keys)
                .anyMatch(x -> x == 4));
    }

    @Test
    public void valuesShouldReturnProperValuesArray(){
        //GIVEN
        longMap.put(1L, "qwe");
        longMap.put(2L, "asd");
        longMap.put(3L, "zxc");
        //WHEN
        String[] values = longMap.values();
        //THEN
        List<String> valueList = Arrays.asList(values);
        Assert.assertTrue(valueList.contains("qwe"));
        Assert.assertTrue(valueList.contains("asd"));
        Assert.assertTrue(valueList.contains("zxc"));
        Assert.assertFalse(valueList.contains("vbn"));
    }
}
