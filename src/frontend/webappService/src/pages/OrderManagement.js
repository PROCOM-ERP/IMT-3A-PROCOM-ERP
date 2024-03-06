import React from 'react'
import Navbar from '../components/Navbar'
import DisplayOrders from '../components/DisplayOrders'
import CheckUserConnection from '../components/CheckUserConnection'

function OrderManagement() {
  return (
    <>
      <CheckUserConnection />
      <Navbar />
      <div className='title'>Order Management</div>
      <DisplayOrders />
    </>
  )
}

export default OrderManagement
