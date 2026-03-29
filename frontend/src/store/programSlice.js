import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import api from '../axiosConfig'

export const fetchPrograms = createAsyncThunk(
  'programs/fetchAll',
  async () => {
    const response = await api.get('/programs')
    return response.data
  }
)

const programSlice = createSlice({
  name: 'programs',
  initialState: {
    items: [],
    loading: false,
    error: null
  },
  reducers: {},
  extraReducers: builder => {
    builder
      .addCase(fetchPrograms.pending, state => { state.loading = true })
      .addCase(fetchPrograms.fulfilled, (state, action) => {
        state.loading = false
        state.items = action.payload
      })
      .addCase(fetchPrograms.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })
  }
})

export default programSlice.reducer