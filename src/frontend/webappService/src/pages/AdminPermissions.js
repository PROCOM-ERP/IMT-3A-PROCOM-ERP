import React from 'react';
import Navbar from '../components/Navbar';
import DisplayPermissions from '../components/DisplayPermissions';
import CheckAdminConnection from '../components/CheckAdminConnection'


function AdminPermissions() {
  return (
    <>
      <CheckAdminConnection />
      <Navbar navUser='admin' />
      <div className='title'>Permissions Management</div>
      <DisplayPermissions />
    </>
  )
}

export default AdminPermissions
