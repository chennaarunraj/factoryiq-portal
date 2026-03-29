import React from 'react'
import ReactDOM from 'react-dom/client'
import { ReactKeycloakProvider } from '@react-keycloak/web'
import { Provider } from 'react-redux'
import { store } from './store/store'
import keycloak from './keycloak'
import App from './App'
import 'antd/dist/reset.css'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <ReactKeycloakProvider
    authClient={keycloak}
    initOptions={{ onLoad: 'login-required' }}
  >
    <Provider store={store}>
      <App />
    </Provider>
  </ReactKeycloakProvider>
)