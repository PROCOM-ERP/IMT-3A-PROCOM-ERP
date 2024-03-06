import React from 'react'
import Navbar from '../components/Navbar'
import { Link } from 'react-router-dom'

function Error401() {
  return (
    <>
      <Navbar />
      <div className='title'>Error 401: Unauthorized</div>
      <div className='text'> <Link to="/" >Go back to login</Link> </div>
    </>
  )
}

export default Error401
