import axios, {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  AxiosError
} from 'axios';
import { ElMessage, ElLoading } from 'element-plus';

// 扩展AxiosRequestConfig类型，添加showLoading属性
declare module 'axios' {
  interface AxiosRequestConfig {
    showLoading?: boolean;
  }
}

// 定义响应数据结构
interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  success: boolean;
}

// 加载状态管理
let loadingInstance: ReturnType<typeof ElLoading.service> | null = null;
let loadingCount = 0;

const showLoading = () => {
  if (loadingCount === 0) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.1)'
    });
  }
  loadingCount++;
};

const hideLoading = () => {
  if (loadingCount <= 0) return;
  loadingCount--;
  if (loadingCount === 0) {
    loadingInstance?.close();
    loadingInstance = null;
  }
};

// 创建axios实例 - 直接使用环境变量，现在配置已支持
const httpClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
});

// 请求拦截器
httpClient.interceptors.request.use(
  (config) => {
    // 显示加载状态（默认显示）
    if (config.showLoading !== false) {
      showLoading();
    }

    // 如需添加token认证，取消下面注释
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }

    return config;
  },
  (error: AxiosError) => {
    hideLoading();
    ElMessage.error('请求配置错误');
    return Promise.reject(error);
  }
);

// 响应拦截器
httpClient.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    hideLoading();
    
    const { data } = response;
    
    if (!data.success) {
      if (data.code === 401) {
        ElMessage.error('登录状态已过期，请重新登录');
        // 可添加跳转登录页逻辑
        // window.location.href = '/login';
      } else {
        ElMessage.error(data.message || '操作失败');
      }
      return Promise.reject(new Error(data.message || 'Error'));
    }
    
    return data.data;
  },
  (error: AxiosError) => {
    hideLoading();
    
    if (error.message.includes('timeout')) {
      ElMessage.error('请求超时，请稍后再试');
    } else if (error.message.includes('Network Error')) {
      ElMessage.error('网络错误，请检查网络连接');
    } else if (error.response) {
      const status = error.response.status;
      switch (status) {
        case 400:
          ElMessage.error('请求参数错误');
          break;
        case 403:
          ElMessage.error('没有权限访问');
          break;
        case 404:
          ElMessage.error('请求的资源不存在');
          break;
        case 500:
          ElMessage.error('服务器内部错误');
          break;
        default:
          ElMessage.error(`请求错误: ${status}`);
      }
    } else {
      ElMessage.error('未知错误');
    }
    
    return Promise.reject(error);
  }
);

// 封装请求方法
const request = {
  get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    return httpClient.get(url, { params, ...config });
  },
  
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return httpClient.post(url, data, config);
  },
  
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return httpClient.put(url, data, config);
  },
  
  delete<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    return httpClient.delete(url, { params, ...config });
  }
};

export default request;
    