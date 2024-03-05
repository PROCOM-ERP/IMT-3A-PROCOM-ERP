import React from 'react'
import Navbar from '../components/Navbar'
import { Link } from 'react-router-dom'

function Error404() {
  return (
    <>
      <Navbar />
      <div className='title'>Error 404: page not found</div>
      <div className='text'> <Link to="/" >Go back to home page</Link> </div>
    </>
  )
}

export default Error404
