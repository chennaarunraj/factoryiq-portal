import React, { useEffect, useState } from 'react'
import {
  Table, Tag, Card, Typography, Button, Modal, Form, Input,
  Select, DatePicker, Space, Popconfirm, message, Tabs, Row, Col, Statistic
} from 'antd'
import { PlusOutlined, EditOutlined } from '@ant-design/icons'
import api from '../../axiosConfig'
import dayjs from 'dayjs'

const { Title } = Typography
const { Option } = Select
const { TextArea } = Input

const rmaStatusOptions = ['REQUESTED', 'APPROVED', 'SHIPPED_TO_US', 'RECEIVED', 'IN_TRIAGE', 'IN_REPAIR', 'REPAIRED', 'SHIPPED_BACK', 'CLOSED', 'REJECTED', 'CANCELLED']
const warrantyStatusOptions = ['SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'PAID', 'CLOSED']
const repairStatusOptions = ['OPEN', 'IN_PROGRESS', 'COMPLETED', 'CLOSED']

const statusColor = {
  REQUESTED: 'gold', APPROVED: 'blue', IN_REPAIR: 'orange',
  CLOSED: 'green', REJECTED: 'red', CANCELLED: 'default',
  SUBMITTED: 'gold', PAID: 'green', OPEN: 'red', COMPLETED: 'green'
}

function AfterSalesDashboard() {
  const [rmas, setRmas] = useState([])
  const [repairs, setRepairs] = useState([])
  const [warranty, setWarranty] = useState([])

  // RMA Modal
  const [rmaModalOpen, setRmaModalOpen] = useState(false)
  const [editRma, setEditRma] = useState(null)
  const [rmaForm] = Form.useForm()

  // Repair Modal
  const [repairModalOpen, setRepairModalOpen] = useState(false)
  const [editRepair, setEditRepair] = useState(null)
  const [repairForm] = Form.useForm()

  // Warranty Modal
  const [warrantyModalOpen, setWarrantyModalOpen] = useState(false)
  const [editWarranty, setEditWarranty] = useState(null)
  const [warrantyForm] = Form.useForm()

  useEffect(() => {
    fetchRmas()
    fetchRepairs()
    fetchWarranty()
  }, [])

  const fetchRmas = async () => {
    try { const res = await api.get('/after-sales/rmas'); setRmas(res.data) }
    catch { setRmas([]) }
  }

  const fetchRepairs = async () => {
    try { const res = await api.get('/after-sales/repairs'); setRepairs(res.data) }
    catch { setRepairs([]) }
  }

  const fetchWarranty = async () => {
    try { const res = await api.get('/after-sales/warranty'); setWarranty(res.data) }
    catch { setWarranty([]) }
  }

  // ── RMA Handlers ──────────────────────────
  const openAddRma = () => { setEditRma(null); rmaForm.resetFields(); setRmaModalOpen(true) }
  const openEditRma = (r) => { setEditRma(r); rmaForm.setFieldsValue(r); setRmaModalOpen(true) }

  const handleRmaSubmit = async (values) => {
    try {
      if (editRma) {
        await api.patch(`/after-sales/rmas/${editRma.id}/status`, null, { params: { status: values.status } })
        message.success('RMA updated!')
      } else {
        await api.post('/after-sales/rmas', values)
        message.success('RMA created!')
      }
      setRmaModalOpen(false)
      fetchRmas()
    } catch (err) { message.error(err.response?.data?.message || 'Something went wrong!') }
  }

  // ── Repair Handlers ───────────────────────
  const openAddRepair = () => { setEditRepair(null); repairForm.resetFields(); setRepairModalOpen(true) }
  const openEditRepair = (r) => { setEditRepair(r); repairForm.setFieldsValue(r); setRepairModalOpen(true) }

  const handleRepairSubmit = async (values) => {
    try {
      await api.post('/after-sales/repairs', values)
      message.success(editRepair ? 'Repair updated!' : 'Repair case created!')
      setRepairModalOpen(false)
      fetchRepairs()
    } catch (err) { message.error(err.response?.data?.message || 'Something went wrong!') }
  }

  // ── Warranty Handlers ─────────────────────
  const openAddWarranty = () => { setEditWarranty(null); warrantyForm.resetFields(); setWarrantyModalOpen(true) }
  const openEditWarranty = (r) => { setEditWarranty(r); warrantyForm.setFieldsValue(r); setWarrantyModalOpen(true) }

  const handleWarrantySubmit = async (values) => {
    try {
      if (editWarranty) {
        await api.patch(`/after-sales/warranty/${editWarranty.id}/status`, null, { params: { status: values.status } })
        message.success('Warranty claim updated!')
      } else {
        await api.post('/after-sales/warranty', values)
        message.success('Warranty claim created!')
      }
      setWarrantyModalOpen(false)
      fetchWarranty()
    } catch (err) { message.error(err.response?.data?.message || 'Something went wrong!') }
  }

  // ── Columns ───────────────────────────────
  const rmaColumns = [
    { title: 'RMA Number', dataIndex: 'rmaNumber', key: 'rmaNumber' },
    { title: 'Reason', dataIndex: 'reasonCode', key: 'reasonCode' },
    { title: 'Description', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: 'Status', dataIndex: 'status', key: 'status', render: s => <Tag color={statusColor[s] || 'blue'}>{s}</Tag> },
    { title: 'Tracking (In)', dataIndex: 'trackingInbound', key: 'trackingInbound' },
    {
      title: 'Actions', key: 'actions',
      render: (_, r) => <Button icon={<EditOutlined />} size="small" onClick={() => openEditRma(r)}>Edit</Button>
    }
  ]

  const repairColumns = [
    { title: 'Case Number', dataIndex: 'caseNumber', key: 'caseNumber' },
    { title: 'Diagnosis', dataIndex: 'diagnosis', key: 'diagnosis', ellipsis: true },
    { title: 'Repair Cost', dataIndex: 'repairCost', key: 'repairCost', render: v => v ? `$${v}` : '-' },
    { title: 'Status', dataIndex: 'status', key: 'status', render: s => <Tag color={statusColor[s] || 'blue'}>{s}</Tag> },
    {
      title: 'Actions', key: 'actions',
      render: (_, r) => <Button icon={<EditOutlined />} size="small" onClick={() => openEditRepair(r)}>Edit</Button>
    }
  ]

  const warrantyColumns = [
    { title: 'Claim Number', dataIndex: 'claimNumber', key: 'claimNumber' },
    { title: 'Failure', dataIndex: 'failureDescription', key: 'failureDescription', ellipsis: true },
    { title: 'Claim Amount', dataIndex: 'claimAmount', key: 'claimAmount', render: v => v ? `$${v}` : '-' },
    { title: 'Approved', dataIndex: 'approvedAmount', key: 'approvedAmount', render: v => v ? `$${v}` : '-' },
    { title: 'Status', dataIndex: 'status', key: 'status', render: s => <Tag color={statusColor[s] || 'blue'}>{s}</Tag> },
    {
      title: 'Actions', key: 'actions',
      render: (_, r) => <Button icon={<EditOutlined />} size="small" onClick={() => openEditWarranty(r)}>Edit</Button>
    }
  ]

  const tabItems = [
    {
      key: 'rma', label: `RMAs (${rmas.length})`,
      children: (
        <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={openAddRma}>Add RMA</Button>}>
          <Table columns={rmaColumns} dataSource={rmas.map((r, i) => ({ ...r, key: i }))} pagination={{ pageSize: 10 }} size="middle" />
        </Card>
      )
    },
    {
      key: 'repair', label: `Repairs (${repairs.length})`,
      children: (
        <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={openAddRepair}>Add Repair</Button>}>
          <Table columns={repairColumns} dataSource={repairs.map((r, i) => ({ ...r, key: i }))} pagination={{ pageSize: 10 }} size="middle" />
        </Card>
      )
    },
    {
      key: 'warranty', label: `Warranty (${warranty.length})`,
      children: (
        <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={openAddWarranty}>Claim Warranty</Button>}>
          <Table columns={warrantyColumns} dataSource={warranty.map((w, i) => ({ ...w, key: i }))} pagination={{ pageSize: 10 }} size="middle" />
        </Card>
      )
    }
  ]

  return (
    <div>
      <Title level={3} style={{ marginBottom: 24 }}>After-Sales Dashboard</Title>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Open RMAs" value={rmas.filter(r => r.status === 'REQUESTED').length} valueStyle={{ color: '#faad14' }} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="In Repair" value={repairs.filter(r => r.status === 'IN_PROGRESS').length} valueStyle={{ color: '#1890ff' }} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Warranty Claims" value={warranty.filter(w => w.status === 'SUBMITTED').length} valueStyle={{ color: '#faad14' }} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Closed This Month" value={rmas.filter(r => r.status === 'CLOSED').length} valueStyle={{ color: '#52c41a' }} /></Card>
        </Col>
      </Row>

      <Tabs items={tabItems} />

      {/* RMA Modal */}
      <Modal title={editRma ? 'Edit RMA' : 'Add RMA'} open={rmaModalOpen}
        onCancel={() => setRmaModalOpen(false)} onOk={() => rmaForm.submit()}
        okText={editRma ? 'Update' : 'Create'} width={600}>
        <Form form={rmaForm} layout="vertical" onFinish={handleRmaSubmit}>
          <Form.Item name="rmaNumber" label="RMA Number" rules={[{ required: true }]}>
            <Input placeholder="e.g. RMA-2026-002" disabled={!!editRma} />
          </Form.Item>
          <Form.Item name="reasonCode" label="Reason Code" rules={[{ required: true }]}>
            <Select placeholder="Select reason">
              <Option value="DEFECTIVE">Defective</Option>
              <Option value="WRONG_ITEM">Wrong Item</Option>
              <Option value="DAMAGED">Damaged in Transit</Option>
              <Option value="WARRANTY">Warranty Claim</Option>
              <Option value="OTHER">Other</Option>
            </Select>
          </Form.Item>
          <Form.Item name="description" label="Description">
            <TextArea rows={3} placeholder="Describe the issue..." />
          </Form.Item>
          <Form.Item name="status" label="Status" rules={[{ required: true }]}>
            <Select placeholder="Select status">
              {rmaStatusOptions.map(s => <Option key={s} value={s}>{s}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="trackingInbound" label="Inbound Tracking Number">
            <Input placeholder="Customer's return shipment tracking" />
          </Form.Item>
        </Form>
      </Modal>

      {/* Repair Modal */}
      <Modal title={editRepair ? 'Edit Repair Case' : 'Add Repair Case'} open={repairModalOpen}
        onCancel={() => setRepairModalOpen(false)} onOk={() => repairForm.submit()}
        okText={editRepair ? 'Update' : 'Create'} width={600}>
        <Form form={repairForm} layout="vertical" onFinish={handleRepairSubmit}>
          <Form.Item name="caseNumber" label="Case Number" rules={[{ required: true }]}>
            <Input placeholder="e.g. REP-2026-001" disabled={!!editRepair} />
          </Form.Item>
          <Form.Item name="rmaId" label="Linked RMA">
            <Select placeholder="Select RMA" allowClear>
              {rmas.map(r => <Option key={r.id} value={r.id}>{r.rmaNumber}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="diagnosis" label="Diagnosis">
            <TextArea rows={2} placeholder="Technical diagnosis..." />
          </Form.Item>
          <Form.Item name="repairAction" label="Repair Action">
            <TextArea rows={2} placeholder="Steps taken to repair..." />
          </Form.Item>
          <Form.Item name="repairCost" label="Repair Cost ($)">
            <Input type="number" placeholder="0.00" />
          </Form.Item>
          <Form.Item name="status" label="Status" rules={[{ required: true }]}>
            <Select placeholder="Select status">
              {repairStatusOptions.map(s => <Option key={s} value={s}>{s}</Option>)}
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      {/* Warranty Modal */}
      <Modal title={editWarranty ? 'Edit Warranty Claim' : 'Add Warranty Claim'} open={warrantyModalOpen}
        onCancel={() => setWarrantyModalOpen(false)} onOk={() => warrantyForm.submit()}
        okText={editWarranty ? 'Update' : 'Create'} width={600}>
        <Form form={warrantyForm} layout="vertical" onFinish={handleWarrantySubmit}>
          <Form.Item name="claimNumber" label="Claim Number" rules={[{ required: true }]}>
            <Input placeholder="e.g. WRN-2026-001" disabled={!!editWarranty} />
          </Form.Item>
          <Form.Item name="failureDescription" label="Failure Description">
            <TextArea rows={3} placeholder="Describe the failure..." />
          </Form.Item>
          <Form.Item name="claimAmount" label="Claim Amount ($)">
            <Input type="number" placeholder="0.00" />
          </Form.Item>
          <Form.Item name="status" label="Status" rules={[{ required: true }]}>
            <Select placeholder="Select status">
              {warrantyStatusOptions.map(s => <Option key={s} value={s}>{s}</Option>)}
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default AfterSalesDashboard