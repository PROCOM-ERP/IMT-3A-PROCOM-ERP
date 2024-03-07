import React from 'react'
import Navbar from '../../components/Navbar'
import CheckAdminConnection from '../../components/CheckAdminConnection'

function AdminHome() {

  return (
    <>
      <CheckAdminConnection />
      <Navbar navUser='admin' />
    </>
  )
}

export default AdminHome
