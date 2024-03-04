import React, { useState, useEffect } from 'react'
import Navbar from '../components/Navbar.js'
import OrderForm from '../components/OrderForm'

function AddOrder() {
  return (
    <>
      <Navbar />
      <div className='title1'>Add Order</div>
      <OrderForm />
    </>
  )
}
export default AddOrder


