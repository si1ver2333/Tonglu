const DEFAULT_BASE_URL = process.env.VUE_APP_API_BASE || 'http://localhost:8080';

export const apiConfig = {
  baseURL: DEFAULT_BASE_URL,
  // 默认使用真实后端，只有显式设置 VUE_APP_USE_MOCK=true 时才启用 mock
  useMock: process.env.VUE_APP_USE_MOCK === 'true'
};
