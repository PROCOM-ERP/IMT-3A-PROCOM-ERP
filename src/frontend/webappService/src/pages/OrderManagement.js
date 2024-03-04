import React from 'react'
import Navbar from '../components/Navbar'
import DisplayOrders from '../components/DisplayOrders'

function OrderManagement() {
  return (
    <>
      <Navbar />
      <div className='title'>Order Management</div>
      <DisplayOrders />
    </>
  )
}

export default OrderManagement
