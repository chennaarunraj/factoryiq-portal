import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useKeycloak } from '@react-keycloak/web'
import { Spin } from 'antd'
import AppLayout from './layouts/AppLayout'
import Dashboard from './pages/analytics/Dashboard'
import ProgramList from './pages/program/ProgramList'
import ProductionDashboard from './pages/production/ProductionDashboard'
import QualityDashboard from './pages/quality/QualityDashboard'
import SupplyChainDashboard from './pages/supplychain/SupplyChainDashboard'
import AfterSalesDashboard from './pages/aftersales/AfterSalesDashboard'
import DocumentList from './pages/documents/DocumentList'

function App() {
  const { initialized } = useKeycloak()

  if (!initialized) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh'
      }}>
        <Spin size="large" tip="Loading portal..." />
      </div>
    )
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AppLayout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="programs" element={<ProgramList />} />
          <Route path="production" element={<ProductionDashboard />} />
          <Route path="quality" element={<QualityDashboard />} />
          <Route path="supply-chain" element={<SupplyChainDashboard />} />
          <Route path="after-sales" element={<AfterSalesDashboard />} />
          <Route path="documents" element={<DocumentList />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App