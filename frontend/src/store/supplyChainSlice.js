import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import api from '../axiosConfig'

export const fetchPurchaseOrders = createAsyncThunk(
  'supplyChain/fetchPOs',
  async () => {
    const response = await api.get('/supply-chain/purchase-orders')
    return response.data
  }
)

const supplyChainSlice = createSlice({
  name: 'supplyChain',
  initialState: {
    purchaseOrders: [],
    loading: false,
    error: null
  },
  reducers: {},
  extraReducers: builder => {
    builder
      .addCase(fetchPurchaseOrders.pending, state => { state.loading = true })
      .addCase(fetchPurchaseOrders.fulfilled, (state, action) => {
        state.loading = false
        state.purchaseOrders = action.payload
      })
      .addCase(fetchPurchaseOrders.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })
  }
})

export default supplyChainSlice.reducer