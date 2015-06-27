package eu.icarus.momca.momcapi.resource;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 27.06.2015.
 */
public class ResourceTypeTest {

    private final ResourceType type = ResourceType.ANNOTATION_IMAGE;
    private final String value = type.getValue();

    @Test
    public void testCreateFromValue() throws Exception {
        assertEquals(ResourceType.createFromValue(value), type);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateFromValueWithWrongValue() throws Exception {
        String notExistingValue = "This is not an existing value";
        ResourceType.createFromValue(notExistingValue);
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals(type.getValue(), value);
    }

}