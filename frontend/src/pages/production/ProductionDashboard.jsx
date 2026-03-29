import React, { useEffect, useState } from 'react'
import {
  Table, Tag, Card, Typography, Button, Modal, Form, Input,
  Select, DatePicker, Space, Popconfirm, message, Tabs, Row, Col, Statistic, Progress
} from 'antd'
import { PlusOutlined, EditOutlined } from '@ant-design/icons'
import api from '../../axiosConfig'
import dayjs from 'dayjs'

const { Title } = Typography
const { Option } = Select

const woStatusOptions = ['PLANNED', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED']
const opStatusOptions = ['PENDING', 'IN_PROGRESS', 'COMPLETED', 'ON_HOLD']

const statusColor = {
  PLANNED: 'blue', IN_PROGRESS: 'orange', COMPLETED: 'green',
  ON_HOLD: 'gold', CANCELLED: 'default', PENDING: 'default'
}

function ProductionDashboard() {
  const [workOrders, setWorkOrders] = useState([])
  const [lines, setLines] = useState([])
  const [operations, setOperations] = useState([])

  // WO Modal
  const [woModalOpen, setWoModalOpen] = useState(false)
  const [editWo, setEditWo] = useState(null)
  const [woForm] = Form.useForm()

  // Operation Modal
  const [opModalOpen, setOpModalOpen] = useState(false)
  const [editOp, setEditOp] = useState(null)
  const [opForm] = Form.useForm()

  useEffect(() => {
    fetchWorkOrders()
    fetchLines()
  }, [])

  const fetchWorkOrders = async () => {
    try {
      const res = await api.get('/production/work-orders')
      setWorkOrders(res.data)
    } catch { setWorkOrders([]) }
  }

  const fetchLines = async () => {
    try {
      const res = await api.get('/production/lines')
      setLines(res.data)
    } catch { setLines([]) }
  }

  const fetchOperations = async (woId) => {
    try {
      const res = await api.get(`/production/operations/work-order/${woId}`)
      setOperations(res.data)
    } catch { setOperations([]) }
  }

  // ── WO Handlers ───────────────────────────
  const openAddWo = () => { setEditWo(null); woForm.resetFields(); setWoModalOpen(true) }
  const openEditWo = (r) => {
    setEditWo(r)
    woForm.setFieldsValue({
      ...r,
      plannedStartDate: r.plannedStartDate ? dayjs(r.plannedStartDate) : null,
      plannedEndDate: r.plannedEndDate ? dayjs(r.plannedEndDate) : null
    })
    setWoModalOpen(true)
  }

  const handleWoSubmit = async (values) => {
    try {
      const payload = {
        ...values,
        plannedStartDate: values.plannedStartDate?.format('YYYY-MM-DD'),
        plannedEndDate: values.plannedEndDate?.format('YYYY-MM-DD')
      }
      if (editWo) {
        await api.patch(`/production/work-orders/${editWo.id}/status`, null, { params: { status: values.status } })
        message.success('Work order updated!')
      } else {
        await api.post('/production/work-orders', payload)
        message.success('Work order created!')
      }
      setWoModalOpen(false)
      fetchWorkOrders()
    } catch (err) { message.error(err.response?.data?.message || 'Something went wrong!') }
  }

  // ── Operation Handlers ────────────────────
  const openAddOp = () => { setEditOp(null); opForm.resetFields(); setOpModalOpen(true) }

  const handleOpSubmit = async (values) => {
    try {
      await api.post('/production/operations', values)
      message.success('Operation created!')
      setOpModalOpen(false)
    } catch (err) { message.error(err.response?.data?.message || 'Something went wrong!') }
  }

  // ── Columns ───────────────────────────────
  const woColumns = [
    { title: 'Order Number', dataIndex: 'orderNumber', key: 'orderNumber' },
    { title: 'Description', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: 'Planned Qty', dataIndex: 'plannedQty', key: 'plannedQty' },
    { title: 'Completed Qty', dataIndex: 'completedQty', key: 'completedQty' },
    {
      title: 'Progress', key: 'progress',
      render: (_, r) => {
        const pct = r.plannedQty > 0 ? Math.round((r.completedQty / r.plannedQty) * 100) : 0
        return <Progress percent={pct} size="small" />
      }
    },
    { title: 'Yield %', dataIndex: 'yieldPercentage', key: 'yieldPercentage', render: v => v ? `${v}%` : '-' },
    {
      title: 'Status', dataIndex: 'status', key: 'status',
      render: s => <Tag color={statusColor[s] || 'blue'}>{s}</Tag>
    },
    { title: 'Start Date', dataIndex: 'plannedStartDate', key: 'plannedStartDate' },
    {
      title: 'Actions', key: 'actions',
      render: (_, r) => (
        <Button icon={<EditOutlined />} size="small" onClick={() => openEditWo(r)}>Edit</Button>
      )
    }
  ]

  const lineColumns = [
    { title: 'Code', dataIndex: 'code', key: 'code' },
    { title: 'Line Name', dataIndex: 'name', key: 'name' },
    { title: 'Capacity/Shift', dataIndex: 'capacityPerShift', key: 'capacityPerShift' },
    {
      title: 'Status', dataIndex: 'isActive', key: 'isActive',
      render: v => <Tag color={v ? 'green' : 'red'}>{v ? 'ACTIVE' : 'INACTIVE'}</Tag>
    }
  ]

  const tabItems = [
    {
      key: 'wo', label: `Work Orders (${workOrders.length})`,
      children: (
        <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={openAddWo}>Add Work Order</Button>}>
          <Table columns={woColumns} dataSource={workOrders.map((w, i) => ({ ...w, key: i }))}
            pagination={{ pageSize: 10 }} size="middle" />
        </Card>
      )
    },
    {
      key: 'lines', label: `Production Lines (${lines.length})`,
      children: (
        <Card title="Manufacturing Lines">
          <Table columns={lineColumns} dataSource={lines.map((l, i) => ({ ...l, key: i }))}
            pagination={{ pageSize: 10 }} size="middle" />
        </Card>
      )
    }
  ]

  return (
    <div>
      <Title level={3} style={{ marginBottom: 24 }}>Production Dashboard</Title>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Total Work Orders" value={workOrders.length} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="In Progress" value={workOrders.filter(w => w.status === 'IN_PROGRESS').length} valueStyle={{ color: '#faad14' }} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Completed" value={workOrders.filter(w => w.status === 'COMPLETED').length} valueStyle={{ color: '#52c41a' }} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Active Lines" value={lines.filter(l => l.isActive).length} /></Card>
        </Col>
      </Row>

      <Tabs items={tabItems} />

      {/* Work Order Modal */}
      <Modal title={editWo ? 'Edit Work Order' : 'Add Work Order'} open={woModalOpen}
        onCancel={() => setWoModalOpen(false)} onOk={() => woForm.submit()}
        okText={editWo ? 'Update' : 'Create'} width={600}>
        <Form form={woForm} layout="vertical" onFinish={handleWoSubmit}>
          <Form.Item name="orderNumber" label="Order Number" rules={[{ required: true }]}>
            <Input placeholder="e.g. WO-2026-001" disabled={!!editWo} />
          </Form.Item>
          <Form.Item name="description" label="Description">
            <Input placeholder="Work order description" />
          </Form.Item>
          <Form.Item name="status" label="Status" rules={[{ required: true }]}>
            <Select placeholder="Select status">
              {woStatusOptions.map(s => <Option key={s} value={s}>{s}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="plannedQty" label="Planned Quantity">
            <Input type="number" placeholder="0" />
          </Form.Item>
          <Form.Item name="completedQty" label="Completed Quantity">
            <Input type="number" placeholder="0" />
          </Form.Item>
          <Form.Item name="plannedStartDate" label="Planned Start Date">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="plannedEndDate" label="Planned End Date">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ProductionDashboard