package io.mehow.laboratory.android

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.test.core.app.ApplicationProvider
import io.kotest.matchers.shouldBe
import io.mehow.laboratory.Laboratory
import io.mehow.laboratory.experiment
import org.junit.Test

class SharedPreferencesLaboratoryTest {
  private val preferences = ApplicationProvider
    .getApplicationContext<Context>()
    .getSharedPreferences("laboratory", MODE_PRIVATE)
  private val storage = SharedPreferencesSubjectStorage(preferences)
  private val laboratory = Laboratory(storage)

  @Test fun storedSubjectIsAvailableAsExperiment() {
    storage.setSubject(Feature.B)

    laboratory.experiment<Feature>() shouldBe Feature.B
  }

  @Test fun corruptedSubjectYieldsDefaultExperiment() {
    storage.setSubject(Feature.B)
    preferences.edit().putInt(Feature::class.java.name, 1).commit()

    laboratory.experiment<Feature>() shouldBe Feature.A
  }
}

private enum class Feature {
  A, B
}
