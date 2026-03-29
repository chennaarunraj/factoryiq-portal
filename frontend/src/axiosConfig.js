import axios from 'axios'
import keycloak from './keycloak'

const api = axios.create({
  baseURL: '/api/v1'
})

// Attach JWT token to every request
api.interceptors.request.use(config => {
  if (keycloak.token) {
    config.headers.Authorization = `Bearer ${keycloak.token}`
  }
  return config
})

// Refresh token if expired
api.interceptors.response.use(
  response => response,
  async error => {
    if (error.response?.status === 401) {
      try {
        await keycloak.updateToken(30)
        error.config.headers.Authorization = `Bearer ${keycloak.token}`
        return axios(error.config)
      } catch {
        keycloak.logout()
      }
    }
    return Promise.reject(error)
  }
)

export default api