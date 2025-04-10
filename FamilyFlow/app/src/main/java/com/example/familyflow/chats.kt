package com.example.familyflow.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.familyflow.R
import com.example.familyflow.ui.theme.FamilyFlowTheme

class ChatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyFlowTheme {
                ChatPage()
            }
        }
    }
}

// Comprehensive Chatbot Response System
object ChatbotResponses {
    // Conversation topics with multiple response variations
    private val conversationTopics = mapOf(
        "cleaning" to listOf(
            "Cleaning can be overwhelming. Let's break it down step by step.",
            "What specific area are you struggling to clean?",
            "I have some great cleaning tips that can make your chores easier!",
            "Cleaning is all about creating a system that works for you."
        ),
        "chores" to listOf(
            "Chores are important for maintaining a happy home.",
            "How about we create a chore schedule to make things easier?",
            "Every family member can contribute to household tasks.",
            "Want some tips on making chores more manageable?"
        ),
        "homework" to listOf(
            "Homework can be challenging. How can I help you stay organized?",
            "Let's discuss some study strategies that might work for you.",
            "Breaking down homework into smaller tasks can make it less stressful.",
            "What subject are you finding most difficult right now?"
        ),
        "default" to listOf(
            "I'm here to help! What would you like to chat about?",
            "Feel free to ask me anything about home, chores, or homework.",
            "How can I support you today?",
            "Let's have a productive conversation!"
        )
    )

    // Contextual response patterns
    private val responsePatterns = listOf(
        PatternResponse("help", listOf(
            "I'm always here to help! What specific assistance do you need?",
            "Tell me more about what you're looking for help with.",
            "Breaking down problems is my specialty. What can I help you with?"
        )),
        PatternResponse("tired", listOf(
            "It's okay to feel tired. Let's find ways to make tasks more manageable.",
            "Rest is important. How about we create a plan that doesn't overwhelm you?",
            "Everyone gets tired sometimes. What's making you feel exhausted?"
        )),
        PatternResponse("don't want to", listOf(
            "I understand. Sometimes tasks feel challenging. Let's find a motivating approach.",
            "What's making you hesitant? We can work on strategies to make it easier.",
            "Motivation can be tough. Let's break down why you're feeling resistant."
        ))
    )

    // Data class for pattern-based responses
    private data class PatternResponse(
        val pattern: String,
        val responses: List<String>
    )

    // Generate an initial conversation starter
    fun getInitialResponse(topic: String): String {
        return (conversationTopics[topic.lowercase()]
            ?: conversationTopics["default"])?.random()
            ?: "Hi there! How can I help you today?"
    }

    // Generate a response based on user input
    fun generateResponse(userMessage: String): String {
        // Check pattern-based responses first
        responsePatterns.find {
            userMessage.contains(it.pattern, ignoreCase = true)
        }?.let {
            return it.responses.random()
        }

        // Check topic-based responses
        conversationTopics.forEach { (topic, responses) ->
            if (userMessage.contains(topic, ignoreCase = true)) {
                return responses.random()
            }
        }

        // Fallback generic responses
        return listOf(
            "Tell me more about that.",
            "I'm listening. Can you elaborate?",
            "Interesting point. How does that make you feel?",
            "Let's dig a little deeper into what you're saying."
        ).random()
    }
}

// Message data class
data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage() {
    var selectedChat by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val chatList = listOf(
        "Dad" to "Home & Chores Advisor",
        "Mom" to "Household Helper"
    )
    val filteredChats = chatList.filter {
        it.first.contains(searchQuery, ignoreCase = true)
    }

    if (selectedChat != null) {
        ChatBox(
            chatWith = selectedChat!!,
            onBack = { selectedChat = null }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(32.dp)
                                .padding(start = 8.dp)
                        )
                    },
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.f),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4FC3F7),
                                Color(0xFF039BE5)
                            )
                        )
                    )
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                CustomSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Chats",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                filteredChats.forEach { (name, status) ->
                    ChatItem(
                        name = name,
                        status = status,
                        avatarRes = if (name == "Dad") R.drawable.dad else R.drawable.mom
                    ) {
                        selectedChat = name
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBox(chatWith: String, onBack: () -> Unit) {
    val avatarRes = if (chatWith == "Dad") R.drawable.dad else R.drawable.mom
    var userMessage by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember {
        mutableStateOf(listOf(
            ChatMessage(text = ChatbotResponses.getInitialResponse(""), isUser = false)
        ))
    }
    val listState = rememberLazyListState()

    // Automatically scroll to bottom when messages change
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Chat with $chatWith",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onBack() }
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF039BE5)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // Chat messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                items(messages) { message ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = if (message.isUser)
                            Arrangement.End
                        else
                            Arrangement.Start
                    ) {
                        if (!message.isUser) {
                            Image(
                                painter = painterResource(id = avatarRes),
                                contentDescription = "$chatWith's avatar",
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .background(
                                    color = if (message.isUser)
                                        Color(0xFFE0E0E0)
                                    else
                                        Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = message.text,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Chat input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = userMessage,
                    onValueChange = { userMessage = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black
                    ),
                    decorationBox = { innerTextField ->
                        if (userMessage.text.isEmpty()) {
                            Text(
                                "Type a message...",
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                )
                Text(
                    text = "Send",
                    color = Color(0xFF039BE5),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            if (userMessage.text.isNotBlank()) {
                                // Add user message
                                val userMsg = ChatMessage(
                                    text = userMessage.text,
                                    isUser = true
                                )
                                messages = messages + userMsg

                                // Generate bot response
                                val botResponse = ChatMessage(
                                    text = ChatbotResponses.generateResponse(userMessage.text),
                                    isUser = false
                                )
                                messages = messages + botResponse

                                // Clear input
                                userMessage = TextFieldValue("")
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun CustomSearchBar(query: String, onQueryChange: (String) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = TextStyle(color = Color.Gray, fontSize = 16.sp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White, shape = RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (query.isEmpty()) {
                    Text("Search", color = Color.Gray)
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun ChatItem(
    name: String,
    status: String,
    avatarRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = avatarRes),
            contentDescription = "$name's avatar",
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = status,
                fontSize = 14.sp,
                color = Color.LightGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatPage() {
    FamilyFlowTheme {
        ChatPage()
    }
}