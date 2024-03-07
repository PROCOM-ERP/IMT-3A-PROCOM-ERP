import React from 'react'
import Navbar from '../../components/Navbar'
import DirectoryTable from '../../components/DirectoryTable'
import CheckAdminConnection from '../../components/CheckAdminConnection'

function AdminUsers() {
  // TODO: colors management for admin and links
  return (
    <>
      <CheckAdminConnection />
      <Navbar navUser='admin' />
      <div className='title'>Users</div>
      <DirectoryTable isAdmin={true} />
    </>
  )
}

export default AdminUsers
