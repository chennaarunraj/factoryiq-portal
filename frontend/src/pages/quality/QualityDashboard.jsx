import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import {
  Table, Tag, Card, Typography, Button, Modal, Form, Input,
  Select, DatePicker, Space, Popconfirm, message, Tabs, Row, Col, Statistic
} from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { fetchNcrs } from '../../store/qualitySlice'
import api from '../../axiosConfig'
import dayjs from 'dayjs'

const { Title } = Typography
const { Option } = Select
const { TextArea } = Input

const ncrStatusOptions = ['OPEN', 'IN_REVIEW', 'CONTAINED', 'ROOT_CAUSE_ANALYSIS', 'CAPA_IN_PROGRESS', 'CLOSED', 'CANCELLED']
const severityOptions = ['CRITICAL', 'MAJOR', 'MINOR']
const capaStatusOptions = ['OPEN', 'IN_PROGRESS', 'PENDING_VERIFICATION', 'CLOSED', 'CANCELLED']

const severityColor = { CRITICAL: 'red', MAJOR: 'orange', MINOR: 'blue' }
const statusColor = { OPEN: 'red', CLOSED: 'green', CANCELLED: 'default' }

function QualityDashboard() {
  const dispatch = useDispatch()
  const { ncrs, loading } = useSelector(state => state.quality)
  const [capas, setCapas] = useState([])

  // NCR modal
  const [ncrModalOpen, setNcrModalOpen] = useState(false)
  const [editNcr, setEditNcr] = useState(null)
  const [ncrForm] = Form.useForm()

  // CAPA modal
  const [capaModalOpen, setCapaModalOpen] = useState(false)
  const [editCapa, setEditCapa] = useState(null)
  const [capaForm] = Form.useForm()

  useEffect(() => {
    dispatch(fetchNcrs())
    fetchCapas()
  }, [dispatch])

  const fetchCapas = async () => {
    try {
      const res = await api.get('/quality/capas')
      setCapas(res.data)
    } catch { setCapas([]) }
  }

  // ── NCR handlers ──────────────────────────
  const openAddNcr = () => {
    setEditNcr(null)
    ncrForm.resetFields()
    setNcrModalOpen(true)
  }

  const openEditNcr = (record) => {
    setEditNcr(record)
    ncrForm.setFieldsValue({
      ...record,
      detectedDate: record.detectedDate ? dayjs(record.detectedDate) : null
    })
    setNcrModalOpen(true)
  }

  const handleNcrSubmit = async (values) => {
    try {
      const payload = {
        ...values,
        detectedDate: values.detectedDate?.format('YYYY-MM-DD')
      }
      if (editNcr) {
        await api.patch(`/quality/ncrs/${editNcr.id}/status`, null, { params: { status: values.status } })
        message.success('NCR updated!')
      } else {
        await api.post('/quality/ncrs', payload)
        message.success('NCR created!')
      }
      setNcrModalOpen(false)
      dispatch(fetchNcrs())
    } catch (err) {
      message.error(err.response?.data?.message || 'Something went wrong!')
    }
  }

  // ── CAPA handlers ─────────────────────────
  const openAddCapa = () => {
    setEditCapa(null)
    capaForm.resetFields()
    setCapaModalOpen(true)
  }

  const openEditCapa = (record) => {
    setEditCapa(record)
    capaForm.setFieldsValue({
      ...record,
      dueDate: record.dueDate ? dayjs(record.dueDate) : null
    })
    setCapaModalOpen(true)
  }

  const handleCapaSubmit = async (values) => {
    try {
      const payload = {
        ...values,
        dueDate: values.dueDate?.format('YYYY-MM-DD')
      }
      if (editCapa) {
        await api.post('/quality/capas', payload)
        message.success('CAPA updated!')
      } else {
        await api.post('/quality/capas', payload)
        message.success('CAPA created!')
      }
      setCapaModalOpen(false)
      fetchCapas()
    } catch (err) {
      message.error(err.response?.data?.message || 'Something went wrong!')
    }
  }

  // ── NCR columns ───────────────────────────
  const ncrColumns = [
    { title: 'NCR Number', dataIndex: 'ncrNumber', key: 'ncrNumber' },
    { title: 'Title', dataIndex: 'title', key: 'title', ellipsis: true },
    {
      title: 'Severity', dataIndex: 'severity', key: 'severity',
      render: s => <Tag color={severityColor[s] || 'blue'}>{s}</Tag>
    },
    {
      title: 'Status', dataIndex: 'status', key: 'status',
      render: s => <Tag color={statusColor[s] || 'blue'}>{s}</Tag>
    },
    { title: 'Detected', dataIndex: 'detectedDate', key: 'detectedDate' },
    {
      title: 'Actions', key: 'actions',
      render: (_, record) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEditNcr(record)}>Edit</Button>
        </Space>
      )
    }
  ]

  // ── CAPA columns ──────────────────────────
  const capaColumns = [
    { title: 'CAPA Number', dataIndex: 'capaNumber', key: 'capaNumber' },
    { title: 'Title', dataIndex: 'title', key: 'title', ellipsis: true },
    { title: 'Root Cause', dataIndex: 'rootCause', key: 'rootCause', ellipsis: true },
    {
      title: 'Status', dataIndex: 'status', key: 'status',
      render: s => <Tag color={statusColor[s] || 'blue'}>{s}</Tag>
    },
    { title: 'Due Date', dataIndex: 'dueDate', key: 'dueDate' },
    {
      title: 'Actions', key: 'actions',
      render: (_, record) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEditCapa(record)}>Edit</Button>
        </Space>
      )
    }
  ]

  const tabItems = [
    {
      key: 'ncr',
      label: `NCRs (${ncrs.length})`,
      children: (
        <Card
          extra={<Button type="primary" icon={<PlusOutlined />} onClick={openAddNcr}>Add NCR</Button>}
        >
          <Table
            columns={ncrColumns}
            dataSource={ncrs.map((n, i) => ({ ...n, key: i }))}
            loading={loading}
            pagination={{ pageSize: 10 }}
            size="middle"
          />
        </Card>
      )
    },
    {
      key: 'capa',
      label: `CAPAs (${capas.length})`,
      children: (
        <Card
          extra={<Button type="primary" icon={<PlusOutlined />} onClick={openAddCapa}>Add CAPA</Button>}
        >
          <Table
            columns={capaColumns}
            dataSource={capas.map((c, i) => ({ ...c, key: i }))}
            pagination={{ pageSize: 10 }}
            size="middle"
          />
        </Card>
      )
    }
  ]

  return (
    <div>
      <Title level={3} style={{ marginBottom: 24 }}>Quality Dashboard</Title>

      {/* Stats */}
      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Total NCRs" value={ncrs.length} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Open NCRs" value={ncrs.filter(n => n.status === 'OPEN').length} valueStyle={{ color: '#ff4d4f' }} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Critical" value={ncrs.filter(n => n.severity === 'CRITICAL').length} valueStyle={{ color: '#ff4d4f' }} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Total CAPAs" value={capas.length} /></Card>
        </Col>
      </Row>

      {/* Tabs */}
      <Tabs items={tabItems} />

      {/* NCR Modal */}
      <Modal
        title={editNcr ? 'Edit NCR' : 'Add New NCR'}
        open={ncrModalOpen}
        onCancel={() => setNcrModalOpen(false)}
        onOk={() => ncrForm.submit()}
        okText={editNcr ? 'Update' : 'Create'}
        width={600}
      >
        <Form form={ncrForm} layout="vertical" onFinish={handleNcrSubmit}>
          <Form.Item name="ncrNumber" label="NCR Number"
            rules={[{ required: true, message: 'NCR number is required' }]}>
            <Input placeholder="e.g. NCR-002" disabled={!!editNcr} />
          </Form.Item>
          <Form.Item name="title" label="Title"
            rules={[{ required: true, message: 'Title is required' }]}>
            <Input placeholder="Brief description of the issue" />
          </Form.Item>
          <Form.Item name="description" label="Description">
            <TextArea rows={3} placeholder="Detailed description..." />
          </Form.Item>
          <Form.Item name="severity" label="Severity"
            rules={[{ required: true, message: 'Severity is required' }]}>
            <Select placeholder="Select severity">
              <Option value="CRITICAL">🔴 CRITICAL</Option>
              <Option value="MAJOR">🟠 MAJOR</Option>
              <Option value="MINOR">🔵 MINOR</Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="Status"
            rules={[{ required: true, message: 'Status is required' }]}>
            <Select placeholder="Select status">
              {ncrStatusOptions.map(s => <Option key={s} value={s}>{s}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="detectedDate" label="Detected Date">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="containmentAction" label="Containment Action">
            <TextArea rows={2} placeholder="Immediate containment steps taken..." />
          </Form.Item>
        </Form>
      </Modal>

      {/* CAPA Modal */}
      <Modal
        title={editCapa ? 'Edit CAPA' : 'Add New CAPA'}
        open={capaModalOpen}
        onCancel={() => setCapaModalOpen(false)}
        onOk={() => capaForm.submit()}
        okText={editCapa ? 'Update' : 'Create'}
        width={600}
      >
        <Form form={capaForm} layout="vertical" onFinish={handleCapaSubmit}>
          <Form.Item name="capaNumber" label="CAPA Number"
            rules={[{ required: true, message: 'CAPA number is required' }]}>
            <Input placeholder="e.g. CAPA-001" disabled={!!editCapa} />
          </Form.Item>
          <Form.Item name="ncrId" label="Linked NCR">
            <Select placeholder="Select linked NCR (optional)" allowClear>
              {ncrs.map(n => <Option key={n.id} value={n.id}>{n.ncrNumber} - {n.title}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="title" label="Title"
            rules={[{ required: true, message: 'Title is required' }]}>
            <Input placeholder="CAPA title" />
          </Form.Item>
          <Form.Item name="rootCause" label="Root Cause">
            <TextArea rows={2} placeholder="5-Why or Fishbone analysis result..." />
          </Form.Item>
          <Form.Item name="correctiveAction" label="Corrective Action">
            <TextArea rows={2} placeholder="Steps to correct the issue..." />
          </Form.Item>
          <Form.Item name="preventiveAction" label="Preventive Action">
            <TextArea rows={2} placeholder="Steps to prevent recurrence..." />
          </Form.Item>
          <Form.Item name="status" label="Status">
            <Select placeholder="Select status">
              {capaStatusOptions.map(s => <Option key={s} value={s}>{s}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="dueDate" label="Due Date">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default QualityDashboard