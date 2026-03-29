import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import {
  Table, Tag, Card, Typography, Button, Modal,
  Form, Input, Select, DatePicker, Space, Popconfirm, message
} from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { fetchPrograms } from '../../store/programSlice'
import api from '../../axiosConfig'
import dayjs from 'dayjs'

const { Title } = Typography
const { Option } = Select

const statusOptions = ['DRAFT', 'ACTIVE', 'ON_HOLD', 'COMPLETED', 'CANCELLED']
const healthOptions = ['GREEN', 'YELLOW', 'RED']

const columns = (onEdit, onDelete) => [
  { title: 'Code', dataIndex: 'code', key: 'code' },
  { title: 'Program Name', dataIndex: 'name', key: 'name' },
  { title: 'Customer', dataIndex: 'customerName', key: 'customerName' },
  { title: 'Site', dataIndex: 'siteName', key: 'siteName' },
  {
    title: 'Health', dataIndex: 'health', key: 'health',
    render: h => <Tag color={h === 'GREEN' ? 'green' : h === 'YELLOW' ? 'gold' : 'red'}>{h}</Tag>
  },
  {
    title: 'Status', dataIndex: 'status', key: 'status',
    render: s => <Tag color="blue">{s}</Tag>
  },
  { title: 'Planned End', dataIndex: 'plannedEndDate', key: 'plannedEndDate' },
  {
    title: 'Actions', key: 'actions',
    render: (_, record) => (
      <Space>
        <Button
          icon={<EditOutlined />}
          size="small"
          onClick={() => onEdit(record)}
        >Edit</Button>
        <Popconfirm
          title="Delete this program?"
          onConfirm={() => onDelete(record.id)}
          okText="Yes"
          cancelText="No"
        >
          <Button icon={<DeleteOutlined />} size="small" danger>Delete</Button>
        </Popconfirm>
      </Space>
    )
  }
]

function ProgramList() {
  const dispatch = useDispatch()
  const { items, loading } = useSelector(state => state.programs)
  const [modalOpen, setModalOpen] = useState(false)
  const [editRecord, setEditRecord] = useState(null)
  const [customers, setCustomers] = useState([])
  const [sites, setSites] = useState([])
  const [form] = Form.useForm()

  useEffect(() => {
    dispatch(fetchPrograms())
    fetchCustomers()
    fetchSites()
  }, [dispatch])

  const fetchCustomers = async () => {
    try {
      const res = await api.get('/programs/customers')
      setCustomers(res.data)
    } catch {
      // fallback — use seed data
      setCustomers([{ id: '', code: 'CUST-001', name: 'Acme Aerospace' }])
    }
  }

  const fetchSites = async () => {
    try {
      const res = await api.get('/programs/sites')
      setSites(res.data)
    } catch {
      setSites([{ id: '', code: 'SITE-HYD', name: 'Hyderabad Plant' }])
    }
  }

  const openAddModal = () => {
    setEditRecord(null)
    form.resetFields()
    setModalOpen(true)
  }

  const openEditModal = (record) => {
    setEditRecord(record)
    form.setFieldsValue({
      ...record,
      plannedStartDate: record.plannedStartDate ? dayjs(record.plannedStartDate) : null,
      plannedEndDate: record.plannedEndDate ? dayjs(record.plannedEndDate) : null,
    })
    setModalOpen(true)
  }

  const handleSubmit = async (values) => {
    try {
      const payload = {
        ...values,
        plannedStartDate: values.plannedStartDate?.format('YYYY-MM-DD'),
        plannedEndDate: values.plannedEndDate?.format('YYYY-MM-DD'),
      }
      if (editRecord) {
        await api.put(`/programs/${editRecord.id}`, payload)
        message.success('Program updated successfully!')
      } else {
        await api.post('/programs', payload)
        message.success('Program created successfully!')
      }
      setModalOpen(false)
      dispatch(fetchPrograms())
    } catch (err) {
      message.error(err.response?.data?.message || 'Something went wrong!')
    }
  }

  const handleDelete = async (id) => {
    try {
      await api.delete(`/programs/${id}`)
      message.success('Program deleted!')
      dispatch(fetchPrograms())
    } catch {
      message.error('Failed to delete program!')
    }
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 24 }}>
        <Title level={3} style={{ margin: 0 }}>Programs</Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openAddModal}>
          Add Program
        </Button>
      </div>

      <Card>
        <Table
          columns={columns(openEditModal, handleDelete)}
          dataSource={items.map((p, i) => ({ ...p, key: i }))}
          loading={loading}
          pagination={{ pageSize: 10 }}
          size="middle"
        />
      </Card>

      <Modal
        title={editRecord ? 'Edit Program' : 'Add New Program'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={() => form.submit()}
        okText={editRecord ? 'Update' : 'Create'}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="code" label="Program Code"
            rules={[{ required: true, message: 'Code is required' }]}>
            <Input placeholder="e.g. PROG-003" disabled={!!editRecord} />
          </Form.Item>

          <Form.Item name="name" label="Program Name"
            rules={[{ required: true, message: 'Name is required' }]}>
            <Input placeholder="e.g. Boeing Avionics NPI" />
          </Form.Item>

          <Form.Item name="description" label="Description">
            <Input.TextArea rows={3} placeholder="Program description..." />
          </Form.Item>

          <Form.Item name="customerId" label="Customer"
            rules={[{ required: true, message: 'Customer is required' }]}>
            <Select placeholder="Select customer">
              {customers.map(c => (
                <Option key={c.id} value={c.id}>{c.name} ({c.code})</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="siteId" label="Site">
            <Select placeholder="Select manufacturing site">
              {sites.map(s => (
                <Option key={s.id} value={s.id}>{s.name} ({s.code})</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="status" label="Status"
            rules={[{ required: true, message: 'Status is required' }]}>
            <Select placeholder="Select status">
              {statusOptions.map(s => <Option key={s} value={s}>{s}</Option>)}
            </Select>
          </Form.Item>

          <Form.Item name="health" label="Health"
            rules={[{ required: true, message: 'Health is required' }]}>
            <Select placeholder="Select health status">
              <Option value="GREEN">🟢 GREEN</Option>
              <Option value="YELLOW">🟡 YELLOW</Option>
              <Option value="RED">🔴 RED</Option>
            </Select>
          </Form.Item>

          <Form.Item name="plannedStartDate" label="Planned Start Date">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="plannedEndDate" label="Planned End Date">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="jiraProjectKey" label="Jira Project Key">
            <Input placeholder="e.g. PROJ-001" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ProgramList