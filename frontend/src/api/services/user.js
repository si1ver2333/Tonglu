// src/api/services/user.js

import http from '../http';
import { apiConfig } from '../config';
import * as mockUser from '../mock/user';

const useMock = apiConfig.useMock;

const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

const normalizeProfile = (input = {}) => {
  const fieldList = Array.isArray(input.fields)
    ? input.fields
    : Array.isArray(input.focus)
      ? input.focus
      : [];

  const focusText = input.focusArea || (fieldList.length ? fieldList.join(' · ') : '');

  return {
    ...input,
    nickname: input.nickname || input.displayName || '',
    displayName: input.displayName || input.nickname || '',
    bio: input.bio || input.intro || '',
    intro: input.intro || input.bio || '',
    careerStage: input.careerStage || input.stage || '',
    stage: input.stage || input.careerStage || '',
    focus: Array.isArray(input.focus)
      ? input.focus
      : Array.isArray(input.fields)
        ? input.fields
        : [],
    focusArea: focusText,
    city: input.city || input.location || '',
    location: input.location || input.city || ''
  };
};

// 获取个人资料（展示）
export const fetchProfileOverview = async () => {
  const payload = useMock
    ? await mockUser.getProfileOverview()
    : await http.get('/api/user/profile');
  const data = unwrap(payload);
  if (data?.profile) {
    data.profile = normalizeProfile(data.profile);
  }
  return data;
};

// ===============================
// ★★★ 关键修复：编辑个人资料
// ===============================
export const saveProfile = async (body) => {
  const toList = (value) => {
    if (Array.isArray(value)) return value.filter(Boolean);
    if (typeof value === 'string' && value.trim()) {
      return value
        .split(/,|·|\s+/)
        .map((v) => v.trim())
        .filter(Boolean);
    }
    return [];
  };

  // 字段映射，保证前端字段符合后端 UserProfileUpdateDTO
  const mapped = {
    displayName: body.displayName || body.nickname || '',
    bio: body.bio || body.intro || '',
    careerStage: body.careerStage || body.stage || '',
    location: body.location || body.city || '',
    education: body.education || '',
    fields: toList(body.fields || body.focusArea || body.focus)
  };

  // 昵称必填（后端注解 @NotBlank）
  if (!mapped.displayName.trim()) {
    return Promise.reject(new Error('昵称不能为空'));
  }

  const payload = useMock
    ? await mockUser.updateProfile(mapped)
    : await http.put('/api/user/profile', mapped);

  const data = unwrap(payload);
  const profile = normalizeProfile(data?.profile || body);
  return data?.profile ? { ...data, profile } : { profile };
};

// ===============================
// 以下所有接口保持原样
// ===============================

export const savePrivacy = async (body) => {
  const payload = useMock
    ? await mockUser.updatePrivacy(body)
    : await http.put('/api/user/profile/privacy', body);
  return unwrap(payload);
};

export const fetchPrivacySettings = async () => {
  const payload = useMock
    ? await mockUser.updatePrivacy({})
    : await http.get('/api/user/profile/privacy');
  return unwrap(payload);
};

export const fetchHomepage = async () => {
  const payload = useMock
    ? await mockUser.getProfileOverview()
    : await http.get('/api/user/profile/homepage');
  return unwrap(payload);
};

export const fetchHistory = async (params = {}) => {
  const payload = useMock
    ? await mockUser.getHistory(params)
    : await http.get('/api/user/browse-history/list', { params });
  return unwrap(payload);
};

export const addBrowseHistory = async (params = {}) => {
  const payload = useMock
    ? { code: 200, data: null }
    : await http.post('/api/user/browse-history/add', null, { params });
  return unwrap(payload);
};

export const fetchHistoryDetail = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? await mockUser.getHistory({})
    : await http.get(`/api/user/browse-history/detail/${id}`);
  return unwrap(payload);
};

export const deleteHistoryRecord = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.delete(`/api/user/browse-history/delete/${id}`);
  return unwrap(payload);
};

export const fetchMyCircles = async (params = {}) => {
  const payload = useMock
    ? await mockUser.getMyCircles(params)
    : await http.get('/api/user/group/my-groups', { params });
  return unwrap(payload);
};

export const joinCircle = async (groupId) => {
  if (!groupId) return Promise.reject(new Error('groupId is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.post(`/api/user/group/join/${groupId}`);
  return unwrap(payload);
};

export const leaveCircle = async (groupId) => {
  if (!groupId) return Promise.reject(new Error('groupId is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.post(`/api/user/group/leave/${groupId}`);
  return unwrap(payload);
};

export const discoverCircles = async (params = {}) => {
  const payload = useMock
    ? await mockUser.getMyCircles(params)
    : await http.get('/api/user/group/discover', { params });
  return unwrap(payload);
};

export const fetchMyCollections = async (params = {}) => {
  const payload = useMock
    ? await mockUser.getMyCollections(params)
    : await http.get('/api/user/collection/list', { params });
  return unwrap(payload);
};

export const fetchCollectionFolders = async () => {
  const payload = useMock
    ? await mockUser.getMyCollections({})
    : await http.get('/api/user/collection/folders');
  return unwrap(payload);
};

export const createCollectionFolder = async (name) => {
  const payload = useMock
    ? { code: 200, data: null }
    : await http.post('/api/user/collection/folders', null, { params: { name } });
  return unwrap(payload);
};

export const cancelCollection = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.delete(`/api/user/collection/cancel/${id}`);
  return unwrap(payload);
};

export const fetchMyPublish = async (params = {}) => {
  const payload = useMock
    ? await mockUser.getMyPublish(params)
    : await http.get('/api/user/published/list', { params });
  return unwrap(payload);
};

export const createPublish = async (body = {}) => {
  const payload = useMock
    ? await mockUser.getMyPublish(body)
    : await http.post('/api/user/published/publish', body);
  return unwrap(payload);
};

export const updatePublish = async (id, body = {}) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? await mockUser.getMyPublish(body)
    : await http.put(`/api/user/published/edit/${id}`, body);
  return unwrap(payload);
};

export const togglePublishTop = async (id, isTop = 1) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.post(`/api/user/published/top/${id}`, null, { params: { isTop } });
  return unwrap(payload);
};

export const deletePublish = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.delete(`/api/user/published/delete/${id}`);
  return unwrap(payload);
};
