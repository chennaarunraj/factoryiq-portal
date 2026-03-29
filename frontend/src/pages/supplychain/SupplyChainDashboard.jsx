import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import {
  Table, Tag, Card, Typography, Button, Modal, Form, Input,
  Select, DatePicker, Space, Popconfirm, message, Tabs, Row, Col, Statistic
} from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { fetchPurchaseOrders } from '../../store/supplyChainSlice'
import api from '../../axiosConfig'
import dayjs from 'dayjs'

const { Title } = Typography
const { Option } = Select

const poStatusOptions = ['DRAFT', 'SUBMITTED', 'CONFIRMED', 'IN_TRANSIT', 'PARTIALLY_RECEIVED', 'RECEIVED', 'CANCELLED']
const shipmentStatusOptions = ['PENDING', 'PICKED_UP', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'DELIVERED', 'EXCEPTION', 'RETURNED']

const statusColor = {
  CONFIRMED: 'blue', IN_TRANSIT: 'orange', RECEIVED: 'green',
  CANCELLED: 'default', DELIVERED: 'green', EXCEPTION: 'red', PENDING: 'gold'
}

function SupplyChainDashboard() {
  const dispatch = useDispatch()
  const { purchaseOrders, loading } = useSelector(state => state.supplyChain)
  const [inventory, setInventory] = useState([])
  const [shipments, setShipments] = useState([])
  const [suppliers, setSuppliers] = useState([])

  // PO Modal
  const [poModalOpen, setPoModalOpen] = useState(false)
  const [editPo, setEditPo] = useState(null)
  const [poForm] = Form.useForm()

  // Shipment Modal
  const [shipModalOpen, setShipModalOpen] = useState(false)
  const [editShip, setEditShip] = useState(null)
  const [shipForm] = Form.useForm()

  useEffect(() => {
    dispatch(fetchPurchaseOrders())
    fetchInventory()
    fetchShipments()
    fetchSuppliers()
  }, [dispatch])

  const fetchInventory = async () => {
    try {
      const res = await api.get('/supply-chain/inventory/low-stock')
      setInventory(res.data)
    } catch { setInventory([]) }
  }

  const fetchShipments = async () => {
    try {
      const res = await api.get('/supply-chain/shipments')
      setShipments(res.data)
    } catch { setShipments([]) }
  }

  const fetchSuppliers = async () => {
    try {
      const res = await api.get('/supply-chain/suppliers')
      setSuppliers(res.data)
    } catch { setSuppliers([]) }
  }

  // ── PO Handlers ───────────────────────────
  const openAddPo = () => {
    setEditPo(null)
    poForm.resetFields()
    setPoModalOpen(true)
  }

  const openEditPo = (record) => {
    setEditPo(record)
    poForm.setFieldsValue({
      ...record,
      orderDate: record.orderDate ? dayjs(record.orderDate) : null,
      expectedDate: record.expectedDate ? dayjs(record.expectedDate) : null
    })
    setPoModalOpen(true)
  }

  const handlePoSubmit = async (values) => {
    try {
      const payload = {
        ...values,
        orderDate: values.orderDate?.format('YYYY-MM-DD'),
        expectedDate: values.expectedDate?.format('YYYY-MM-DD')
      }
      if (editPo) {
        await api.patch(`/supply-chain/purchase-orders/${editPo.id}/status`, null, { params: { status: values.status } })
        message.success('PO updated!')
      } else {
        await api.post('/supply-chain/purchase-orders', payload)
        message.success('PO created!')
      }
      setPoModalOpen(false)
      dispatch(fetchPurchaseOrders())
    } catch (err) {
      message.error(err.response?.data?.message || 'Something went wrong!')
    }
  }

  const handlePoDelete = async (id) => {
    try {
      await api.patch(`/supply-chain/purchase-orders/${id}/status`, null, { params: { status: 'CANCELLED' } })
      message.success('PO cancelled!')
      dispatch(fetchPurchaseOrders())
    } catch {
      message.error('Failed to cancel PO!')
    }
  }

  // ── Shipment Handlers ─────────────────────
  const openAddShipment = () => {
    setEditShip(null)
    shipForm.resetFields()
    setShipModalOpen(true)
  }

  const openEditShipment = (record) => {
    setEditShip(record)
    shipForm.setFieldsValue({
      ...record,
      shippedDate: record.shippedDate ? dayjs(record.shippedDate) : null,
      estimatedDate: record.estimatedDate ? dayjs(record.estimatedDate) : null
    })
    setShipModalOpen(true)
  }

  const handleShipmentSubmit = async (values) => {
    try {
      const payload = {
        ...values,
        shippedDate: values.shippedDate?.format('YYYY-MM-DD'),
        estimatedDate: values.estimatedDate?.format('YYYY-MM-DD')
      }
      if (editShip) {
        await api.patch(`/supply-chain/shipments/${editShip.id}/status`, null, { params: { status: values.status } })
        message.success('Shipment updated!')
      } else {
        await api.post('/supply-chain/shipments', payload)
        message.success('Shipment created!')
      }
      setShipModalOpen(false)
      fetchShipments()
    } catch (err) {
      message.error(err.response?.data?.message || 'Something went wrong!')
    }
  }

  // ── PO Columns ────────────────────────────
  const poColumns = [
    { title: 'PO Number', dataIndex: 'poNumber', key: 'poNumber' },
    { title: 'Order Date', dataIndex: 'orderDate', key: 'orderDate' },
    { title: 'Expected Date', dataIndex: 'expectedDate', key: 'expectedDate' },
    { title: 'Currency', dataIndex: 'currency', key: 'currency' },
    {
      title: 'Status', dataIndex: 'status', key: 'status',
      render: s => <Tag color={statusColor[s] || 'blue'}>{s}</Tag>
    },
    {
      title: 'Actions', key: 'actions',
      render: (_, record) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEditPo(record)}>Edit</Button>
          <Popconfirm title="Cancel this PO?" onConfirm={() => handlePoDelete(record.id)}>
            <Button icon={<DeleteOutlined />} size="small" danger>Cancel</Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  // ── Inventory Columns ─────────────────────
  const inventoryColumns = [
    { title: 'Item Code', dataIndex: 'itemCode', key: 'itemCode' },
    { title: 'Description', dataIndex: 'description', key: 'description' },
    { title: 'Quantity', dataIndex: 'quantity', key: 'quantity' },
    { title: 'Allocated', dataIndex: 'allocatedQty', key: 'allocatedQty' },
    { title: 'UOM', dataIndex: 'unitOfMeasure', key: 'unitOfMeasure' },
    { title: 'Min Threshold', dataIndex: 'minThreshold', key: 'minThreshold' },
    {
      title: 'Stock Status', key: 'stockStatus',
      render: (_, r) => (
        <Tag color={r.quantity <= r.minThreshold ? 'red' : 'green'}>
          {r.quantity <= r.minThreshold ? 'LOW STOCK' : 'OK'}
        </Tag>
      )
    }
  ]

  // ── Shipment Columns ──────────────────────
  const shipmentColumns = [
    { title: 'Tracking Number', dataIndex: 'trackingNumber', key: 'trackingNumber' },
    { title: 'Carrier', dataIndex: 'carrier', key: 'carrier' },
    { title: 'Origin', dataIndex: 'origin', key: 'origin' },
    { title: 'Destination', dataIndex: 'destination', key: 'destination' },
    { title: 'Est. Delivery', dataIndex: 'estimatedDate', key: 'estimatedDate' },
    {
      title: 'Status', dataIndex: 'status', key: 'status',
      render: s => <Tag color={statusColor[s] || 'blue'}>{s}</Tag>
    },
    {
      title: 'Actions', key: 'actions',
      render: (_, record) => (
        <Button icon={<EditOutlined />} size="small" onClick={() => openEditShipment(record)}>Update</Button>
      )
    }
  ]

  const tabItems = [
    {
      key: 'po',
      label: `Purchase Orders (${purchaseOrders.length})`,
      children: (
        <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={openAddPo}>Add PO</Button>}>
          <Table columns={poColumns} dataSource={purchaseOrders.map((p, i) => ({ ...p, key: i }))}
            loading={loading} pagination={{ pageSize: 10 }} size="middle" />
        </Card>
      )
    },
    {
      key: 'inventory',
      label: `Inventory (${inventory.length} low stock)`,
      children: (
        <Card title="Low Stock Items">
          <Table columns={inventoryColumns} dataSource={inventory.map((i, idx) => ({ ...i, key: idx }))}
            pagination={{ pageSize: 10 }} size="middle" />
        </Card>
      )
    },
    {
      key: 'shipments',
      label: `Shipments (${shipments.length})`,
      children: (
        <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={openAddShipment}>Add Shipment</Button>}>
          <Table columns={shipmentColumns} dataSource={shipments.map((s, i) => ({ ...s, key: i }))}
            pagination={{ pageSize: 10 }} size="middle" />
        </Card>
      )
    }
  ]

  return (
    <div>
      <Title level={3} style={{ marginBottom: 24 }}>Supply Chain Dashboard</Title>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Total POs" value={purchaseOrders.length} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="In Transit" value={purchaseOrders.filter(p => p.status === 'IN_TRANSIT').length} valueStyle={{ color: '#faad14' }} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Low Stock Items" value={inventory.length} valueStyle={{ color: '#ff4d4f' }} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="Active Shipments" value={shipments.filter(s => s.status === 'IN_TRANSIT').length} /></Card>
        </Col>
      </Row>

      <Tabs items={tabItems} />

      {/* PO Modal */}
      <Modal
        title={editPo ? 'Edit Purchase Order' : 'Add Purchase Order'}
        open={poModalOpen}
        onCancel={() => setPoModalOpen(false)}
        onOk={() => poForm.submit()}
        okText={editPo ? 'Update' : 'Create'}
        width={600}
      >
        <Form form={poForm} layout="vertical" onFinish={handlePoSubmit}>
          <Form.Item name="poNumber" label="PO Number"
            rules={[{ required: true, message: 'PO number is required' }]}>
            <Input placeholder="e.g. PO-2026-002" disabled={!!editPo} />
          </Form.Item>
          <Form.Item name="supplierId" label="Supplier">
            <Select placeholder="Select supplier" allowClear>
              {suppliers.map(s => <Option key={s.id} value={s.id}>{s.name} ({s.code})</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="status" label="Status"
            rules={[{ required: true, message: 'Status is required' }]}>
            <Select placeholder="Select status">
              {poStatusOptions.map(s => <Option key={s} value={s}>{s}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="currency" label="Currency" initialValue="USD">
            <Select>
              <Option value="USD">USD</Option>
              <Option value="EUR">EUR</Option>
              <Option value="INR">INR</Option>
              <Option value="GBP">GBP</Option>
            </Select>
          </Form.Item>
          <Form.Item name="orderDate" label="Order Date">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="expectedDate" label="Expected Delivery Date">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>

      {/* Shipment Modal */}
      <Modal
        title={editShip ? 'Update Shipment' : 'Add Shipment'}
        open={shipModalOpen}
        onCancel={() => setShipModalOpen(false)}
        onOk={() => shipForm.submit()}
        okText={editShip ? 'Update' : 'Create'}
        width={600}
      >
        <Form form={shipForm} layout="vertical" onFinish={handleShipmentSubmit}>
          <Form.Item name="trackingNumber" label="Tracking Number"
            rules={[{ required: true, message: 'Tracking number is required' }]}>
            <Input placeholder="e.g. TRK-2026-001" disabled={!!editShip} />
          </Form.Item>
          <Form.Item name="carrier" label="Carrier">
            <Select placeholder="Select carrier" allowClear>
              <Option value="FedEx">FedEx</Option>
              <Option value="DHL">DHL</Option>
              <Option value="UPS">UPS</Option>
              <Option value="BlueDart">BlueDart</Option>
              <Option value="Other">Other</Option>
            </Select>
          </Form.Item>
          <Form.Item name="origin" label="Origin">
            <Input placeholder="e.g. Shanghai, China" />
          </Form.Item>
          <Form.Item name="destination" label="Destination">
            <Input placeholder="e.g. Hyderabad, India" />
          </Form.Item>
          <Form.Item name="status" label="Status"
            rules={[{ required: true, message: 'Status is required' }]}>
            <Select placeholder="Select status">
              {shipmentStatusOptions.map(s => <Option key={s} value={s}>{s}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="shippedDate" label="Shipped Date">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="estimatedDate" label="Estimated Delivery">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default SupplyChainDashboard