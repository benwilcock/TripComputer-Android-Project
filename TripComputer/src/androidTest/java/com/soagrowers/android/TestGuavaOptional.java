package com.soagrowers.android;

import com.google.common.base.Optional;

import junit.framework.TestCase;

/**
 * Created by Ben on 11/08/2014.
 */
public class TestGuavaOptional extends TestCase {

    public TestGuavaOptional() {
        super();
    }

    public TestGuavaOptional(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOptional() {

        Optional<Integer> optional = Optional.of(5);
        assertTrue(optional.isPresent());
        assertEquals(new Integer(5), optional.get());
    }

  public void testOptionalRefusesNull(){

    // Nothing can be done about this!
    Optional<Integer> optional = null;

    // This throws an Exception
    try {
      optional = Optional.of(null);
      assertTrue(false);
    } catch (NullPointerException e){
      assertTrue(true);
    }

    // This is OK
    optional = Optional.of(5);
    assertTrue(optional.isPresent());
    optional = Optional.absent();
    assertFalse(optional.isPresent());
  }

    public void testOptionalMutation() {

        Optional<Integer> optional = Optional.of(5);
        assertTrue(optional.isPresent());
        assertEquals(new Integer(5), optional.get());

        optional = Optional.absent();
        assertTrue(null != optional.absent());
    }
}
