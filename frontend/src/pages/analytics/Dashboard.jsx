import React from 'react'
import { Row, Col, Card, Statistic, Tag, Typography, Table } from 'antd'
import {
  ProjectOutlined, CheckCircleOutlined,
  WarningOutlined, CloseCircleOutlined
} from '@ant-design/icons'

const { Title } = Typography

const stats = [
  { title: 'Active Programs', value: 12, icon: <ProjectOutlined />, color: '#1890ff' },
  { title: 'On Track', value: 8, icon: <CheckCircleOutlined />, color: '#52c41a' },
  { title: 'At Risk', value: 3, icon: <WarningOutlined />, color: '#faad14' },
  { title: 'Delayed', value: 1, icon: <CloseCircleOutlined />, color: '#ff4d4f' }
]

const recentPrograms = [
  { key: 1, code: 'PROG-001', name: 'Acme Avionics NPI', customer: 'Acme Aerospace', health: 'GREEN', status: 'ACTIVE' },
]

const columns = [
  { title: 'Code', dataIndex: 'code', key: 'code' },
  { title: 'Program', dataIndex: 'name', key: 'name' },
  { title: 'Customer', dataIndex: 'customer', key: 'customer' },
  {
    title: 'Health', dataIndex: 'health', key: 'health',
    render: h => <Tag color={h === 'GREEN' ? 'green' : h === 'YELLOW' ? 'gold' : 'red'}>{h}</Tag>
  },
  {
    title: 'Status', dataIndex: 'status', key: 'status',
    render: s => <Tag color="blue">{s}</Tag>
  }
]

function Dashboard() {
  return (
    <div>
      <Title level={3} style={{ marginBottom: 24 }}>
        Portal Dashboard
      </Title>

      <Row gutter={[16, 16]}>
        {stats.map((stat, i) => (
          <Col xs={24} sm={12} lg={6} key={i}>
            <Card>
              <Statistic
                title={stat.title}
                value={stat.value}
                prefix={React.cloneElement(stat.icon, { style: { color: stat.color } })}
                valueStyle={{ color: stat.color }}
              />
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24}>
          <Card title="Recent Programs">
            <Table
              columns={columns}
              dataSource={recentPrograms}
              pagination={false}
              size="middle"
            />
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Dashboard