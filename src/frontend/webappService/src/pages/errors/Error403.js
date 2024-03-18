import React from 'react'
import Navbar from '../../components/Navbar'
import { Link } from 'react-router-dom'

function Error403({ pageLink = "/", message = "Go back to login" }) {
  return (
    <>
      <Navbar />
      <div className='title'>Error 403: Forbidden</div>
      <div className='text'> <Link to={pageLink} > {message} </Link> </div>
    </>
  )
}

export default Error403
