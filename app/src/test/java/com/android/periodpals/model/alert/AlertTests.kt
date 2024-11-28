package com.android.periodpals.model.alert

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlertTests {
  @Test
  fun testTextToProduct() {
    assertEquals(Product.TAMPON, textToProduct("Tampon"))
    assertEquals(Product.PAD, textToProduct("Pad"))
    assertEquals(Product.NO_PREFERENCE, textToProduct("No Preference"))
    assertNull(textToProduct("Unknown"))
  }

  @Test
  fun testTextToUrgency() {
    assertEquals(Urgency.LOW, textToUrgency("Low"))
    assertEquals(Urgency.MEDIUM, textToUrgency("Medium"))
    assertEquals(Urgency.HIGH, textToUrgency("High"))
    assertNull(textToUrgency("Unknown"))
  }
}
