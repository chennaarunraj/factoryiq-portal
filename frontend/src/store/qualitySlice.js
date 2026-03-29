import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import api from '../axiosConfig'

export const fetchNcrs = createAsyncThunk(
  'quality/fetchNcrs',
  async () => {
    const response = await api.get('/quality/ncrs')
    return response.data
  }
)

const qualitySlice = createSlice({
  name: 'quality',
  initialState: {
    ncrs: [],
    loading: false,
    error: null
  },
  reducers: {},
  extraReducers: builder => {
    builder
      .addCase(fetchNcrs.pending, state => { state.loading = true })
      .addCase(fetchNcrs.fulfilled, (state, action) => {
        state.loading = false
        state.ncrs = action.payload
      })
      .addCase(fetchNcrs.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })
  }
})

export default qualitySlice.reducer