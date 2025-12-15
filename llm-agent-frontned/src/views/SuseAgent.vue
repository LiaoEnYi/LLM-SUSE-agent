<template>
  <div class="chat-container">
    <div class="chat-header">
      <h2>SUSE智能体应用</h2>
      <p class="chat-id">聊天室ID: {{ chatId }}</p>
    </div>
    <div class="chat-messages" ref="messagesContainer">
      <div 
        v-for="(message, index) in messages" 
        :key="index"
        :class="['message', message.type]"
      >
        <div class="message-content">
          {{ message.content }}
        </div>
      </div>
    </div>
    <div class="chat-input-container">
      <textarea 
        v-model="inputMessage"
        @keydown.enter.exact="sendMessage"
        @keydown.enter.shift="addNewline"
        placeholder="请输入您的问题..."
        rows="3"
        class="chat-input"
      ></textarea>
      <button 
        @click="sendMessage"
        class="send-button"
        :disabled="isSending"
      >
        {{ isSending ? '发送中...' : '发送' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'

const messages = ref([])
const inputMessage = ref('')
const isSending = ref(false)
const messagesContainer = ref(null)
const chatId = ref('')

// 生成聊天室ID
const generateChatId = () => {
  return 'chat_' + Date.now() + '_' + Math.floor(Math.random() * 1000)
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 添加换行
const addNewline = (event) => {
  event.preventDefault()
  inputMessage.value += '\n'
}

// 发送消息
const sendMessage = () => {
  if (!inputMessage.value.trim() || isSending.value) return
  
  const message = inputMessage.value.trim()
  messages.value.push({ type: 'user', content: message })
  inputMessage.value = ''
  isSending.value = true
  
  scrollToBottom()
  
  // 使用SSE调用doChat接口
  const eventSource = new EventSource(`/api/suseAgent/doChat?message=${encodeURIComponent(message)}&userId=${chatId.value}`)
  
  let aiMessage = ''
  messages.value.push({ type: 'ai', content: aiMessage })
  
  eventSource.onmessage = (event) => {
    if (event.data) {
      aiMessage += event.data
      messages.value[messages.value.length - 1].content = aiMessage
      scrollToBottom()
    }
  }
  
  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    eventSource.close()
    isSending.value = false
  }
  
  eventSource.onclose = () => {
    isSending.value = false
  }
}

onMounted(() => {
  chatId.value = generateChatId()
  // 欢迎消息
  messages.value.push({ type: 'ai', content: '您好！我是SUSE智能小助手，有什么可以帮助您的？' })
  scrollToBottom()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.chat-header {
  background-color: #42b883;
  color: white;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chat-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.chat-id {
  margin: 0.5rem 0 0 0;
  font-size: 0.8rem;
  opacity: 0.8;
}

.chat-messages {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message {
  max-width: 70%;
  padding: 0.8rem 1rem;
  border-radius: 18px;
  word-wrap: break-word;
  animation: fadeIn 0.3s ease;
}

.message.user {
  align-self: flex-end;
  background-color: #42b883;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.ai {
  align-self: flex-start;
  background-color: white;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message-content {
  line-height: 1.5;
}

.chat-input-container {
  padding: 1rem;
  background-color: white;
  border-top: 1px solid #e0e0e0;
  display: flex;
  gap: 1rem;
}

.chat-input {
  flex: 1;
  padding: 0.8rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  resize: none;
  font-size: 1rem;
  font-family: inherit;
  outline: none;
  transition: border-color 0.2s;
}

.chat-input:focus {
  border-color: #42b883;
}

.send-button {
  padding: 0 1.5rem;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
  align-self: flex-end;
  height: 50px;
}

.send-button:hover:not(:disabled) {
  background-color: #369f70;
}

.send-button:disabled {
  background-color: #a0d9b6;
  cursor: not-allowed;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>