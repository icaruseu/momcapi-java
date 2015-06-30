package eu.icarus.momca.momcapi.resource;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 27.06.2015.
 */
public class ResourceTypeTest {

    @NotNull
    private static final ResourceType TYPE = ResourceType.ANNOTATION_IMAGE;
    @NotNull
    private static final String VALUE = TYPE.getValue();

    @Test
    public void testCreateFromValue() throws Exception {
        assertEquals(ResourceType.createFromValue(VALUE), TYPE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateFromValueWithWrongValue() throws Exception {
        String notExistingValue = "This is not an existing value";
        ResourceType.createFromValue(notExistingValue);
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals(TYPE.getValue(), VALUE);
    }

}