import React from 'react'
import Navbar from '../components/Navbar'

function AdminUser() {
  return (
    <>
      <Navbar navUser='admin' />
      <UserProfil title="User Profil" userId={userId} />
    </>
  )
}

export default AdminUser
