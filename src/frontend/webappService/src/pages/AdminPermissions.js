import React from 'react';
import Navbar from '../components/Navbar';
import DisplayPermissions from '../components/DisplayPermissions';


function AdminPermissions() {
  return (
    <>
      <Navbar navUser='admin' />
      <div className='title'>Permissions</div>
      <DisplayPermissions />
    </>
  )
}

export default AdminPermissions
