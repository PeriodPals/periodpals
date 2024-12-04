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

  @Test
  fun productToPeriodPalsIconIsCorrect() {
    assertEquals(LIST_OF_PRODUCTS[0], productToPeriodPalsIcon(Product.TAMPON))
    assertEquals(LIST_OF_PRODUCTS[1], productToPeriodPalsIcon(Product.PAD))
    assertEquals(LIST_OF_PRODUCTS[2], productToPeriodPalsIcon(Product.NO_PREFERENCE))
  }

  @Test
  fun urgencyToPeriodPalsIconIsCorrect() {
    assertEquals(LIST_OF_URGENCIES[0], urgencyToPeriodPalsIcon(Urgency.HIGH))
    assertEquals(LIST_OF_URGENCIES[1], urgencyToPeriodPalsIcon(Urgency.MEDIUM))
    assertEquals(LIST_OF_URGENCIES[2], urgencyToPeriodPalsIcon(Urgency.LOW))
  }

  @Test
  fun testParseLocationGIS() {
    assertEquals("POINT(12.34 56.78)", Alert.parseLocationGIS("56.78,12.34,Some Place"))
    assertEquals("POINT(-123.45 67.89)", Alert.parseLocationGIS("67.89,-123.45,Another Place"))
  }
}
