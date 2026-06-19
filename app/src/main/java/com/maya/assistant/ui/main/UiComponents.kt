package com.maya.assistant.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maya.assistant.R

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()
    private var lastMyraText = ""

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_MAYA = 1
    }

    fun addMessage(message: ChatMessage) {
        // Deduplicate MYRA messages
        if (!message.isUser && message.text == lastMyraText) return
        messages.add(message)
        if (!message.isUser) lastMyraText = message.text
        notifyItemInserted(messages.size - 1)
    }

    fun lastMyraText(): String = lastMyraText

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) TYPE_USER else TYPE_MAYA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_maya, parent, false)
            MayaViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is UserViewHolder -> {
                holder.messageText.text = message.text
            }
            is MayaViewHolder -> {
                holder.messageText.text = message.text
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.chatUserText)
    }

    class MayaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.chatMayaText)
    }
}
