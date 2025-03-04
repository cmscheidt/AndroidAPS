package info.nightscout.androidaps.plugins.constraints.objectives.objectives

import androidx.fragment.app.FragmentActivity
import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.R
import info.nightscout.androidaps.plugins.constraints.objectives.ObjectivesPlugin
import info.nightscout.androidaps.plugins.general.nsclient.NSClientPlugin
import info.nightscout.androidaps.utils.T
import javax.inject.Inject

@Suppress("SpellCheckingInspection")
class Objective3 @Inject constructor(injector: HasAndroidInjector) : Objective(injector, "openloop", R.string.objectives_openloop_objective, R.string.objectives_openloop_gate) {

    @Inject lateinit var objectivesPlugin: ObjectivesPlugin
    @Inject lateinit var nsClientPlugin: NSClientPlugin

    init {
        tasks.add(MinimumDurationTask(this, T.days(1).msecs()))
        tasks.add(object : Task(this, R.string.objectives_manualenacts) {
            override fun isCompleted(): Boolean {
                return sp.getInt(R.string.key_ObjectivesmanualEnacts, 0) >= MANUAL_ENACTS_NEEDED
            }

            override val progress: String
                get() = if (sp.getInt(R.string.key_ObjectivesmanualEnacts, 0) >= MANUAL_ENACTS_NEEDED) rh.gs(R.string.completed_well_done) else sp.getInt(R.string.key_ObjectivesmanualEnacts, 0).toString() + " / " + MANUAL_ENACTS_NEEDED
        })
    }

    override fun specialActionEnabled(): Boolean =
        nsClientPlugin.nsClientService?.isConnected == true && nsClientPlugin.nsClientService?.hasWriteAuth == true

    override fun specialAction(activity: FragmentActivity, input: String) {
        objectivesPlugin.completeObjectives(activity, input)
    }

    companion object {

        private const val MANUAL_ENACTS_NEEDED = 20
    }
}
