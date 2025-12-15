<template>
  <article class="content-card" :class="{ 'has-cover': !!item.cover }" @click="$emit('select', item)">
    <div class="cover" v-if="item.cover">
      <img :src="item.cover" alt="推荐内容封面" />
    </div>
    <div class="body">
      <div class="tag-row">
        <div class="tag">{{ item.tag }}</div>
        <span v-if="item.publishTime" class="time">{{ item.publishTime }}</span>
      </div>
      <h3>{{ item.title }}</h3>
      <p class="desc">{{ item.desc }}</p>
      <div class="meta">
        <div class="author">
          <img v-if="item.avatar" :src="item.avatar" alt="作者头像" class="avatar" />
          <span>{{ item.author }}</span>
        </div>
        <div class="stats">
          <span v-if="item.likeCount !== undefined">👍 {{ item.likeCount || 0 }}</span>
          <span v-if="item.collectCount !== undefined">📁 {{ item.collectCount || 0 }}</span>
          <span v-if="item.commentCount !== undefined">💬 {{ item.commentCount || 0 }}</span>
        </div>
      </div>
    </div>
  </article>
</template>

<script>
export default {
  name: 'ContentCard',
  props: {
    item: {
      type: Object,
      required: true
    }
  }
};
</script>

<style scoped>
.content-card {
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px;
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
  background: #fff;
  cursor: pointer;
  transition: transform 0.12s ease, box-shadow 0.12s ease;
}

.content-card.has-cover {
  grid-template-columns: 1fr 120px;
}

.content-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
}

.cover {
  width: 120px;
  height: 100%;
  border-radius: 10px;
  overflow: hidden;
  background: var(--gray-100);
}

.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tag-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.tag {
  align-self: flex-start;
  padding: 4px 8px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--blue);
  border-radius: 8px;
  font-size: 12px;
}

.time {
  color: var(--gray-500);
  font-size: 12px;
}

h3 {
  margin: 0;
  font-size: 16px;
}

.desc {
  margin: 0;
  color: var(--gray-700);
  font-size: 14px;
  line-height: 1.5;
}

.meta {
  display: flex;
  justify-content: space-between;
  color: var(--gray-600);
  font-size: 12px;
  align-items: center;
}

.author {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  object-fit: cover;
}

.stats {
  display: inline-flex;
  gap: 10px;
}

@media (max-width: 720px) {
  .content-card {
    grid-template-columns: 1fr;
  }

  .cover {
    width: 100%;
    height: 160px;
  }
}
</style>
