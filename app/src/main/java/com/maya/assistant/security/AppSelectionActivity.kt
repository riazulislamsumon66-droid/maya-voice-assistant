package com.maya.assistant.security

import android.content.pm.Applicationতথ্য
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maya.assistant.R
import kotlinx.coroutines.*

class Appসিলেক্ট করোionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_selection)

        recyclerView = findViewById(R.id.appsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedআকার(true)

        loadApps()
    }

    private fun loadApps() {
        activityScope.launch {
            val apps = withContext(Dispatchers.IO) {
                val pm = packageManager
                val lockedApps =
                    নিরাপত্তাManager.getলক আছেPackages(this@Appসিলেক্ট করোionActivity)

                pm.getইনস্টল করোedApplications(PackageManager.GET_META_DATA)
                    .filter {
                        (it.flags and Applicationতথ্য.FLAG_SYSTEM) == 0 &&
                                it.packageনাম != packageনাম
                    }
                    .map {
                        Appতথ্য(
                            name = it.loadLabel(pm).toString(),
                            packageনাম = it.packageনাম,
                            icon = it.loadIcon(pm),
                            isলক আছে = lockedApps.contains(it.packageনাম)
                        )
                    }
                    .sortedBy { it.name.lowercase() }
            }

            recyclerView.adapter = AppAdapter(apps)
        }
    }

    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }

    data class Appতথ্য(
        val name: String,
        val packageনাম: String,
        val icon: Drawable,
        var isলক আছে: Boolean
    )

    inner class AppAdapter(
        private val apps: List<Appতথ্য>
    ) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

        inner class AppViewHolder(view: View)
            : RecyclerView.ViewHolder(view) {

            val icon: ImageView = view.findViewById(R.id.appIcon)
            val name: TextView = view.findViewById(R.id.appনাম)
            val checkbox: CheckBox = view.findViewById(R.id.appCheckbox)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): AppViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_app_selection,
                    parent,
                    false
                )
            return AppViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: AppViewHolder,
            position: Int
        ) {
            val app = apps[position]

            holder.icon.setImageDrawable(app.icon)
            holder.name.text = app.name

            holder.checkbox.setচালুCheckedChangeListener(null)
            holder.checkbox.isChecked = app.isলক আছে

            holder.checkbox.setচালুCheckedChangeListener { _, isChecked ->
                updateLock(app, isChecked)
            }

            holder.itemView.setচালুClickListener {
                holder.checkbox.isChecked = !holder.checkbox.isChecked
            }
        }

        private fun updateLock(
            app: Appতথ্য,
            locked: Boolean
        ) {
            app.isলক আছে = locked

            if (locked) {
                নিরাপত্তাManager.addলক আছেPackage(
                    this@Appসিলেক্ট করোionActivity,
                    app.packageনাম
                )
            } else {
                নিরাপত্তাManager.removeলক আছেPackage(
                    this@Appসিলেক্ট করোionActivity,
                    app.packageনাম
                )
            }
        }

        override fun getItemগণনা() = apps.size
    }
}