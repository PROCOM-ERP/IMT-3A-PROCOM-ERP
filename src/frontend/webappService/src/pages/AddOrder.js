import React from 'react'
import '../css/App.css'
import Navbar from '../components/Navbar.js'
import OrderForm from '../components/OrderForm'

function AddOrder() {
  return (
    <>
      <Navbar />
      <div className='title'>Add Order</div>
      <OrderForm />
    </>
  )
}
export default AddOrder


