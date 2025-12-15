<template>
  <div class="home">
    <h1>AI Agent 应用切换</h1>
    <div class="app-list">
      <router-link to="/suse-agent" class="app-card">
        <h2>SUSE智能体应用</h2>
        <p>基于SUSE Agent的智能聊天应用</p>
      </router-link>
      <router-link to="/ai-super-agent" class="app-card">
        <h2>AI超级智能体应用</h2>
        <p>基于AI Super Agent的智能聊天应用</p>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.home {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
  text-align: center;
}

h1 {
  color: #333;
  margin-bottom: 2rem;
}

.app-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.app-card {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-decoration: none;
  color: #333;
  transition: transform 0.2s, box-shadow 0.2s;
}

.app-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.app-card h2 {
  color: #42b883;
  margin-bottom: 0.5rem;
}

.app-card p {
  color: #666;
}
</style>