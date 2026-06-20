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

class AppSelect কRowionActivity : AppCompatActivity() {

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
                    SecurityManager.getলক আছেPackages(this@AppSelect কRowionActivity)

                pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter {
                        (it.flags and Applicationতথ্য.FLAG_SYSTEM) == 0 &&
                                it.packageName != packageName
                    }
                    .map {
                        Appতথ্য(
                            name = it.loadLabel(pm).toString(),
                            packageName = it.packageName,
                            icon = it.loadIcon(pm),
                            isলক আছে = lockedApps.contains(it.packageName)
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
        val packageName: String,
        val icon: Drawable,
        var isলক আছে: Boolean
    )

    inner class AppAdapter(
        private val apps: List<Appতথ্য>
    ) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

        inner class AppViewHolder(view: View)
            : RecyclerView.ViewHolder(view) {

            val icon: ImageView = view.findViewById(R.id.appIcon)
            val name: TextView = view.findViewById(R.id.appName)
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

            holder.checkbox.setEnabledCheckedChangeListener(null)
            holder.checkbox.isChecked = app.isলক আছে

            holder.checkbox.setEnabledCheckedChangeListener { _, isChecked ->
                updateLock(app, isChecked)
            }

            holder.itemView.setEnabledClickListener {
                holder.checkbox.isChecked = !holder.checkbox.isChecked
            }
        }

        private fun updateLock(
            app: Appতথ্য,
            locked: Boolean
        ) {
            app.isলক আছে = locked

            if (locked) {
                SecurityManager.addলক আছেPackage(
                    this@AppSelect কRowionActivity,
                    app.packageName
                )
            } else {
                SecurityManager.removeলক আছেPackage(
                    this@AppSelect কRowionActivity,
                    app.packageName
                )
            }
        }

        override fun getItemCount() = apps.size
    }
}