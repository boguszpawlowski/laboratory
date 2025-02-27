package io.mehow.laboratory.inspector

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.textview.MaterialTextView
import io.mehow.laboratory.inspector.DeprecationPhenotype.Hide
import io.mehow.laboratory.inspector.DeprecationPhenotype.Show
import io.mehow.laboratory.inspector.DeprecationPhenotype.Strikethrough
import io.mehow.laboratory.supervisorOption

internal class FeatureViewHolder(
  itemView: View,
  listener: FeatureAdapter.Listener,
) : ViewHolder(itemView) {
  private var uiModel: FeatureUiModel? = null
  private val context = itemView.context
  private val nameControl = itemView.findViewById<MaterialTextView>(R.id.io_mehow_laboratory_feature_name)
  private val supervisorControl = itemView.findViewById<MaterialTextView>(R.id.io_mehow_laboratory_feature_supervisor)
  private val descriptionControl = itemView.findViewById<MaterialTextView>(R.id.io_mehow_laboratory_feature_description)
  private val sourcesControl = itemView.findViewById<SourceViewGroup>(R.id.io_mehow_laboratory_feature_sources)
  private val dividerControl = itemView.findViewById<View>(R.id.io_mehow_laboratory_sources_divider)
  private val optionsControl = itemView.findViewById<OptionViewGroup>(R.id.io_mehow_laboratory_feature_options)
  private val goToSupervisor = object : ClickableSpan() {
    override fun onClick(widget: View) {
      listener.onGoToFeature(uiModel!!.supervisorOption!!.javaClass)
    }
  }

  init {
    sourcesControl.setOnSelectSourceListener(listener)
    optionsControl.setOnSelectFeatureListener(listener)
    descriptionControl.movementMethod = LinkMovementMethod.getInstance()
    supervisorControl.movementMethod = LinkMovementMethod.getInstance()
  }

  fun bind(uiModel: FeatureUiModel) {
    this.uiModel = uiModel
    with(uiModel) {
      bindName()
      bindSupervisor()
      bindDescription()
      bindSources()
      bindOptions()
    }
  }

  private fun FeatureUiModel.bindName() {
    nameControl.text = this.name
    nameControl.paintFlags = when (deprecationPhenotype) {
      null, Show -> nameControl.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
      Strikethrough -> nameControl.paintFlags or STRIKE_THRU_TEXT_FLAG
      Hide -> nameControl.paintFlags
    }
  }

  private fun FeatureUiModel.bindSupervisor() = with(type) {
    supervisorControl.isVisible = supervisorOption != null
    supervisorControl.text = supervisorOption?.let { supervisorOption ->
      buildSpannedString {
        append(context.getString(R.string.io_mehow_laboratory_feature_supervisor_prefix))
        val linkStart = length
        append(supervisorOption::class.simpleName)
        setSpan(goToSupervisor, linkStart, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        append(context.getString(R.string.io_mehow_laboratory_feature_supervisor_suffix, supervisorOption))
      }
    }
  }

  private fun FeatureUiModel.bindDescription() {
    descriptionControl.setTextTokens(description)
    descriptionControl.isVisible = description.isNotEmpty()
  }

  private fun FeatureUiModel.bindSources() {
    sourcesControl.render(sources)
    sourcesControl.isVisible = hasMultipleSources
    dividerControl.isVisible = hasMultipleSources
  }

  private fun FeatureUiModel.bindOptions() {
    optionsControl.render(models, isEnabled)
  }
}
