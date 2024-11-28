package com.android.periodpals.model.alert

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlertTests {
  @Test
  fun testTextToProduct() {
    assertEquals(Product.TAMPON, stringToProduct("Tampon"))
    assertEquals(Product.PAD, stringToProduct("Pad"))
    assertEquals(Product.NO_PREFERENCE, stringToProduct("No Preference"))
    assertNull(stringToProduct("Unknown"))
  }

  @Test
  fun testTextToUrgency() {
    assertEquals(Urgency.LOW, stringToUrgency("Low"))
    assertEquals(Urgency.MEDIUM, stringToUrgency("Medium"))
    assertEquals(Urgency.HIGH, stringToUrgency("High"))
    assertNull(stringToUrgency("Unknown"))
  }
}
