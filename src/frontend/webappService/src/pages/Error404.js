import React from 'react'
import Navbar from '../components/Navbar'
import { Link } from 'react-router-dom'

function Error404() {
  return (
    <>
      <Navbar />
      <div className='title'>Error 404: Not found</div>
      <div className='text'> <Link to="/home" >Go back to home</Link> </div>
    </>
  )
}

export default Error404
