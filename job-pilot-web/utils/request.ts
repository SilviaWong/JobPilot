import { ElMessage } from 'element-plus'

/**
 * 统一后端返回结构
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  total?: number
  hasMore?: boolean
  success: boolean
}

/**
 * 请求配置扩展选项
 */
export interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'
  params?: Record<string, any>
  body?: any
  headers?: Record<string, string>
  showErrorMessage?: boolean // 是否自动弹出错误提示，默认 true
  [key: string]: any
}

/**
 * 统一 API 请求函数
 *
 * 自动拦截 HTTP 状态异常与业务异常 (code !== 200)，统一使用 ElMessage 提示错误
 */
export async function request<T = any>(
  url: string,
  options: RequestOptions = {}
): Promise<ApiResponse<T>> {
  const { showErrorMessage = true, ...fetchOptions } = options

  try {
    const res = await $fetch<ApiResponse<T>>(url, {
      ...fetchOptions,
      headers: {
        'Content-Type': 'application/json',
        ...fetchOptions.headers,
      },
    })

    // 处理标准 Result 结构的业务状态异常
    if (res && typeof res.code === 'number') {
      if (res.code !== 200 && !res.success) {
        if (showErrorMessage && typeof window !== 'undefined') {
          ElMessage.error(res.message || '业务请求失败')
        }
      }
    }

    return res
  } catch (error: any) {
    const errorMessage =
      error?.data?.message ||
      error?.message ||
      '网络请求异常，请检查后端服务是否正常运行'

    if (showErrorMessage && typeof window !== 'undefined') {
      ElMessage.error(errorMessage)
    }

    throw error
  }
}

/**
 * 快捷请求方法封装
 */
export const api = {
  get<T = any>(url: string, params?: Record<string, any>, options?: RequestOptions) {
    return request<T>(url, { method: 'GET', params, ...options })
  },
  post<T = any>(url: string, body?: any, options?: RequestOptions) {
    return request<T>(url, { method: 'POST', body, ...options })
  },
  put<T = any>(url: string, body?: any, options?: RequestOptions) {
    return request<T>(url, { method: 'PUT', body, ...options })
  },
  delete<T = any>(url: string, options?: RequestOptions) {
    return request<T>(url, { method: 'DELETE', ...options })
  },
}
