import React from 'react'
import Navbar from '../components/Navbar'
import CheckUserConnection from '../components/CheckUserConnection'

function Inventory() {
  return (
    <>
      <CheckUserConnection />
      <Navbar />
      <div className='title'>Inventory</div>
    </>
  )
}

export default Inventory
