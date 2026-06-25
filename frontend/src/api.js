import axios from 'axios'

// All calls go through the API gateway on localhost:8080.
// The gateway rewrites /api/orders -> order-service, /api/inventory -> inventory-service, etc.
const api = axios.create({
  baseURL: 'http://localhost:8080/api'
})

export const placeOrder = (order) => api.post('/orders', order)
export const getOrders = () => api.get('/orders')
export const getInventory = () => api.get('/inventory')
export const getNotifications = () => api.get('/notifications')

export default api
