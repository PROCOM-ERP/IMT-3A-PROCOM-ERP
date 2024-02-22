import React from 'react'
import Navbar from '../components/Navbar'
import DirectoryTable from '../components/DirectoryTable'

function AdminUsers() {
  // TODO: colors management for admin and links
  return (
    <>
      <Navbar navUser='admin' />
      <div className='title'>Users</div>
      <DirectoryTable isAdmin={true} />
    </>
  )
}

export default AdminUsers
