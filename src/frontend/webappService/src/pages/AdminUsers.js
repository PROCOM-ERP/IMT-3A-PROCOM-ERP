import React from 'react'
import Navbar from '../components/Navbar'
import DirectoryTable from '../components/DirectoryTable'

function AdminUsers() {
  return (
    <>
      <Navbar navUser='admin' />
      <DirectoryTable />
    </>
  )
}

export default AdminUsers
