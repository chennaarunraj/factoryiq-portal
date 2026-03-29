import React from 'react'
import { Card, Table, Tag, Typography, Button, Upload } from 'antd'
import { UploadOutlined } from '@ant-design/icons'

const { Title } = Typography

const columns = [
  { title: 'Title', dataIndex: 'title', key: 'title' },
  { title: 'File Name', dataIndex: 'fileName', key: 'fileName' },
  {
    title: 'Type', dataIndex: 'docType', key: 'docType',
    render: t => <Tag color="blue">{t}</Tag>
  },
  {
    title: 'Status', dataIndex: 'status', key: 'status',
    render: s => <Tag color={s === 'APPROVED' ? 'green' : 'orange'}>{s}</Tag>
  },
  {
    title: 'Action', key: 'action',
    render: () => <Button size="small" type="link">Download</Button>
  }
]

function DocumentList() {
  return (
    <div>
      <Title level={3} style={{ marginBottom: 24 }}>Documents</Title>
      <Card
        title="Document Library"
        extra={
          <Upload>
            <Button icon={<UploadOutlined />}>Upload Document</Button>
          </Upload>
        }
      >
        <Table
          columns={columns}
          dataSource={[]}
          pagination={{ pageSize: 10 }}
          size="middle"
        />
      </Card>
    </div>
  )
}

export default DocumentList