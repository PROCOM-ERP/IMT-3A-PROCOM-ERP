import React from 'react'
import '../css/App.css'
import Navbar from '../components/Navbar.js'
import OrderForm from '../components/OrderForm.js'
import CheckUserConnection from '../components/CheckUserConnection.js'

function AddOrder() {
  return (
    <>
      <CheckUserConnection />
      <Navbar />
      <div className='title'>Add Order</div>
      <OrderForm />
    </>
  )
}
export default AddOrder


