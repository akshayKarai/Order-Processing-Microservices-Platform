import { useEffect, useState } from 'react'
import { placeOrder, getOrders, getInventory, getNotifications } from './api'

const statusColor = {
  PENDING: '#b58900',
  CONFIRMED: '#2e7d32',
  REJECTED: '#c62828'
}

function OrderForm({ onOrderPlaced }) {
  const [productId, setProductId] = useState('SKU-001')
  const [quantity, setQuantity] = useState(1)
  const [customerName, setCustomerName] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError(null)
    try {
      await placeOrder({ productId, quantity: Number(quantity), customerName })
      setCustomerName('')
      setQuantity(1)
      onOrderPlaced()
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to place order. Is the backend running?')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8, marginBottom: 24 }}>
      <h2>Place an order</h2>
      <div style={{ marginBottom: 8 }}>
        <label>Product&nbsp;</label>
        <select value={productId} onChange={(e) => setProductId(e.target.value)}>
          <option value="SKU-001">SKU-001 - Wireless Mouse</option>
          <option value="SKU-002">SKU-002 - Mechanical Keyboard</option>
          <option value="SKU-003">SKU-003 - USB-C Hub</option>
        </select>
      </div>
      <div style={{ marginBottom: 8 }}>
        <label>Quantity&nbsp;</label>
        <input type="number" min="1" value={quantity} onChange={(e) => setQuantity(e.target.value)} />
      </div>
      <div style={{ marginBottom: 8 }}>
        <label>Customer name&nbsp;</label>
        <input type="text" required value={customerName} onChange={(e) => setCustomerName(e.target.value)} />
      </div>
      <button type="submit" disabled={submitting}>
        {submitting ? 'Placing order...' : 'Place order'}
      </button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </form>
  )
}

function OrdersPanel({ orders }) {
  return (
    <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8, marginBottom: 24 }}>
      <h2>Orders</h2>
      <p style={{ fontSize: 13, color: '#666' }}>
        Status starts as PENDING, then flips to CONFIRMED or REJECTED once Inventory Service
        processes the Kafka event. Refreshes every 2 seconds.
      </p>
      <table width="100%">
        <thead>
          <tr><th align="left">ID</th><th align="left">Product</th><th align="left">Qty</th><th align="left">Customer</th><th align="left">Status</th></tr>
        </thead>
        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td>{o.id}</td>
              <td>{o.productId}</td>
              <td>{o.quantity}</td>
              <td>{o.customerName}</td>
              <td style={{ color: statusColor[o.status] || '#333', fontWeight: 'bold' }}>{o.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function InventoryPanel({ inventory }) {
  return (
    <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8, marginBottom: 24 }}>
      <h2>Inventory</h2>
      <p style={{ fontSize: 13, color: '#666' }}>
        Stock decreases when an order is confirmed by Inventory Service.
      </p>
      <table width="100%">
        <thead><tr><th align="left">Product ID</th><th align="left">Name</th><th align="left">Stock</th></tr></thead>
        <tbody>
          {inventory.map((p) => (
            <tr key={p.productId}>
              <td>{p.productId}</td>
              <td>{p.name}</td>
              <td>{p.stockQuantity}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function NotificationsPanel({ notifications }) {
  return (
    <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8 }}>
      <h2>Notification feed</h2>
      <p style={{ fontSize: 13, color: '#666' }}>
        Published asynchronously by Notification Service after consuming order-created from Kafka.
      </p>
      <ul>
        {notifications.map((n) => (
          <li key={n.id}>{n.message}</li>
        ))}
      </ul>
    </div>
  )
}

export default function App() {
  const [orders, setOrders] = useState([])
  const [inventory, setInventory] = useState([])
  const [notifications, setNotifications] = useState([])

  const refreshAll = async () => {
    try {
      const [ordersRes, inventoryRes, notificationsRes] = await Promise.all([
        getOrders(), getInventory(), getNotifications()
      ])
      setOrders(ordersRes.data)
      setInventory(inventoryRes.data)
      setNotifications(notificationsRes.data)
    } catch (err) {
      console.error('Polling failed, is the backend up?', err)
    }
  }

  useEffect(() => {
    refreshAll()
    const interval = setInterval(refreshAll, 2000)
    return () => clearInterval(interval)
  }, [])

  return (
    <div style={{ maxWidth: 800, margin: '40px auto', fontFamily: 'sans-serif', padding: '0 16px' }}>
      <h1>Order processing platform</h1>
      <p style={{ color: '#666' }}>
        React UI for the Spring Boot microservices demo. All requests go through the API gateway at
        localhost:8080. Order status updates flow asynchronously through Kafka.
      </p>
      <OrderForm onOrderPlaced={refreshAll} />
      <OrdersPanel orders={orders} />
      <InventoryPanel inventory={inventory} />
      <NotificationsPanel notifications={notifications} />
    </div>
  )
}
