import { configureStore } from '@reduxjs/toolkit'
import programReducer from './programSlice'
import qualityReducer from './qualitySlice'
import supplyChainReducer from './supplyChainSlice'

export const store = configureStore({
  reducer: {
    programs: programReducer,
    quality: qualityReducer,
    supplyChain: supplyChainReducer
  }
})

export default store