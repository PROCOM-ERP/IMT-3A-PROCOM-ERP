import React from 'react'
import '../css/App.css'
import Navbar from '../components/Navbar.js'
import OrderForm from '../components/OrderForm'
import CheckUserConnection from '../components/CheckUserConnection'

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


