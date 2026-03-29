import React, { useState } from 'react'
import { Layout, Menu, Avatar, Dropdown, Badge, Typography } from 'antd'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { useKeycloak } from '@react-keycloak/web'
import {
  DashboardOutlined,
  ProjectOutlined,
  ToolOutlined,
  SafetyCertificateOutlined,
  ShoppingCartOutlined,
  CustomerServiceOutlined,
  FileOutlined,
  UserOutlined,
  LogoutOutlined,
  BellOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined
} from '@ant-design/icons'

const { Header, Sider, Content } = Layout
const { Text } = Typography

const menuItems = [
  { key: '/dashboard', icon: <DashboardOutlined />, label: 'Dashboard' },
  { key: '/programs', icon: <ProjectOutlined />, label: 'Programs' },
  { key: '/production', icon: <ToolOutlined />, label: 'Production' },
  { key: '/quality', icon: <SafetyCertificateOutlined />, label: 'Quality' },
  { key: '/supply-chain', icon: <ShoppingCartOutlined />, label: 'Supply Chain' },
  { key: '/after-sales', icon: <CustomerServiceOutlined />, label: 'After Sales' },
  { key: '/documents', icon: <FileOutlined />, label: 'Documents' }
]

function AppLayout() {
  const [collapsed, setCollapsed] = useState(false)
  const { keycloak } = useKeycloak()
  const navigate = useNavigate()
  const location = useLocation()

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: keycloak.tokenParsed?.preferred_username || 'User'
    },
    { type: 'divider' },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Logout',
      onClick: () => keycloak.logout()
    }
  ]

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        style={{ background: '#001529' }}
        width={220}
      >
        <div className="portal-logo">
          {!collapsed && <span>🏭 FactoryIQ</span>}
          {collapsed && <span>🏭</span>}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>

      <Layout>
        <Header style={{
          background: '#fff',
          padding: '0 24px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          boxShadow: '0 1px 4px rgba(0,0,0,0.1)'
        }}>
          <div
            onClick={() => setCollapsed(!collapsed)}
            style={{ cursor: 'pointer', fontSize: 18 }}
          >
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <Badge count={3}>
              <BellOutlined style={{ fontSize: 18, cursor: 'pointer' }} />
            </Badge>
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <div style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8 }}>
                <Avatar icon={<UserOutlined />} style={{ background: '#1890ff' }} />
                <Text>{keycloak.tokenParsed?.preferred_username || 'User'}</Text>
              </div>
            </Dropdown>
          </div>
        </Header>

        <Content style={{ margin: '24px', background: '#f0f2f5' }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

export default AppLayout